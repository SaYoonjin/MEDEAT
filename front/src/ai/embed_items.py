import os
import json
import argparse
from pathlib import Path
from typing import List, Dict, Tuple, Optional

import torch
import torch.nn.functional as F
from PIL import Image
from tqdm import tqdm
from transformers import AutoImageProcessor, AutoModel

IMAGE_EXTS = {".jpg", ".jpeg", ".png", ".webp", ".bmp"}

# -----------------------
# 모델/프로세서 캐시 (single_embed에서 계속 호출해도 매번 로드 안 하게)
# -----------------------
_MODEL = None
_PROCESSOR = None
_DEVICE: Optional[torch.device] = None
_MODEL_ID: Optional[str] = None


def list_item_folders(root: Path) -> List[Path]:
    return sorted([p for p in root.iterdir() if p.is_dir()])


def list_images(folder: Path) -> List[Path]:
    imgs = []
    for p in folder.iterdir():
        if p.is_file() and p.suffix.lower() in IMAGE_EXTS:
            imgs.append(p)
    return sorted(imgs)


def load_image(path: Path) -> Image.Image:
    return Image.open(path).convert("RGB")


def resolve_model_id(model: str) -> str:
    if model == "dinov2":
        return "facebook/dinov2-base"
    if model == "siglip2":
        return "google/siglip2-base-patch16-224"
    raise ValueError(f"Unsupported model: {model}")


def resolve_device(device: str) -> torch.device:
    if device == "auto":
        return torch.device("cuda" if torch.cuda.is_available() else "cpu")
    return torch.device(device)


def init_model(model: str = "siglip2", device: str = "auto"):
    """
    single_embed.py에서도 이걸 공유해서:
    - 아이템 임베딩 생성과 스캔 임베딩 생성이 완전히 같은 모델/전처리/출력 공간이 되게 한다.
    """
    global _MODEL, _PROCESSOR, _DEVICE, _MODEL_ID

    model_id = resolve_model_id(model)
    dev = resolve_device(device)

    # 이미 같은 모델로 초기화 되어 있으면 재사용
    if _MODEL is not None and _PROCESSOR is not None and _MODEL_ID == model_id and _DEVICE == dev:
        return _MODEL, _PROCESSOR, _DEVICE, _MODEL_ID

    _MODEL_ID = model_id
    _DEVICE = dev

    print(f"[INFO] init_model model_id={_MODEL_ID}")
    print(f"[INFO] init_model device={_DEVICE}")

    _PROCESSOR = AutoImageProcessor.from_pretrained(_MODEL_ID)
    _MODEL = AutoModel.from_pretrained(_MODEL_ID)
    _MODEL.to(_DEVICE)
    _MODEL.eval()

    return _MODEL, _PROCESSOR, _DEVICE, _MODEL_ID


@torch.no_grad()
def embed_batch(
    model,
    processor,
    images: List[Image.Image],
    device: torch.device,
) -> torch.Tensor:
    """
    returns: (B, D) float32
    """
    inputs = processor(images=images, return_tensors="pt")
    inputs = {k: v.to(device) for k, v in inputs.items()}

    # SigLIP2/CLIP 류는 get_image_features가 있을 수 있음
    if hasattr(model, "get_image_features"):
        feats = model.get_image_features(**inputs)
        return feats.float()

    outputs = model(**inputs)

    # ViT 계열: last_hidden_state (B, seq, dim) → CLS 토큰 사용
    if hasattr(outputs, "last_hidden_state") and outputs.last_hidden_state is not None:
        return outputs.last_hidden_state[:, 0, :].float()

    # 혹시 pooler_output이 있으면 그걸 사용
    if hasattr(outputs, "pooler_output") and outputs.pooler_output is not None:
        return outputs.pooler_output.float()

    raise RuntimeError("Could not extract image embeddings from model outputs.")


@torch.no_grad()
def create_embedding(image_path: str, model: str = "siglip2", device: str = "auto"):
    """
    ✅ single_embed.py가 호출할 함수
    - embed_items(main)에서 쓰는 것과 완전히 동일한 모델/전처리/특징 추출 로직
    - 반환: (D,) numpy array (L2 normalize 완료)
    """
    m, p, dev, model_id = init_model(model=model, device=device)
    img = load_image(Path(image_path))
    feats = embed_batch(m, p, [img], device=dev)  # (1, D)
    feats = F.normalize(feats, p=2, dim=1)        # (1, D)
    return feats[0].detach().cpu().numpy()


@torch.no_grad()
def compute_prototype_for_item(
    model,
    processor,
    image_paths: List[Path],
    device: torch.device,
    batch_size: int,
    max_images: int,
) -> Tuple[torch.Tensor, int]:
    """
    returns: (D,) prototype embedding, and number of images used
    """
    if max_images > 0:
        image_paths = image_paths[:max_images]

    all_feats = []
    used = 0

    for i in range(0, len(image_paths), batch_size):
        batch_paths = image_paths[i : i + batch_size]
        imgs = [load_image(p) for p in batch_paths]
        feats = embed_batch(model, processor, imgs, device=device)  # (B, D)
        feats = F.normalize(feats, p=2, dim=1)  # L2 normalize per image
        all_feats.append(feats.cpu())
        used += len(batch_paths)

    feats_cat = torch.cat(all_feats, dim=0)  # (N, D)
    proto = feats_cat.mean(dim=0)            # (D,)
    proto = F.normalize(proto, p=2, dim=0)   # L2 normalize prototype
    return proto, used


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--input_dir", required=True, help="Root dir: input_dir/itemSeq/*.jpg")
    ap.add_argument("--output_json", required=True, help="Output JSON path")
    ap.add_argument(
        "--model",
        choices=["dinov2", "siglip2"],
        required=True,
        help="Which embedding model to use",
    )
    ap.add_argument("--batch_size", type=int, default=32)
    ap.add_argument("--max_images_per_item", type=int, default=0, help="0 = use all")
    ap.add_argument("--device", default="auto", choices=["auto", "cpu", "cuda"])
    args = ap.parse_args()

    model_id = resolve_model_id(args.model)
    device = resolve_device(args.device)

    print(f"[INFO] model_id={model_id}")
    print(f"[INFO] device={device}")
    print(f"[INFO] input_dir={args.input_dir}")

    processor = AutoImageProcessor.from_pretrained(model_id)
    model = AutoModel.from_pretrained(model_id)
    model.to(device)
    model.eval()

    root = Path(args.input_dir)
    out_path = Path(args.output_json)
    out_path.parent.mkdir(parents=True, exist_ok=True)

    items = list_item_folders(root)
    if not items:
        raise RuntimeError(f"No subfolders found under {root}. Expected input_dir/itemSeq/...")

    results: List[Dict] = []
    dims = None
    total_imgs = 0

    for item_dir in tqdm(items, desc="Embedding items"):
        item_seq = item_dir.name
        imgs = list_images(item_dir)
        if not imgs:
            continue

        # ⭐ 앞 2장 + 뒤 2장만 사용 (탭 말고 스페이스로 들여쓰기!!)
        if len(imgs) > 4:
            imgs = imgs[:2] + imgs[-2:]

        proto, used = compute_prototype_for_item(
            model=model,
            processor=processor,
            image_paths=imgs,
            device=device,
            batch_size=args.batch_size,
            max_images=args.max_images_per_item,
        )

        emb = proto.numpy().astype("float32").tolist()
        if dims is None:
            dims = len(emb)

        results.append({
            "itemSeq": item_seq,
            "dim": dims,
            "embedding": emb,
            "usedImages": used,
        })
        total_imgs += used

    payload = {
        "model": args.model,
        "model_id": model_id,
        "dim": dims,
        "count": len(results),
        "total_used_images": total_imgs,
        "items": results,
    }

    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False)

    print(f"[DONE] saved: {out_path} (items={len(results)}, dim={dims}, total_imgs={total_imgs})")


if __name__ == "__main__":
    main()

# ====== add below to embed_items.py ======
from transformers import AutoImageProcessor, AutoModel
import torch
import torch.nn.functional as F
from PIL import Image
from pathlib import Path

_MODEL_CACHE = {}  # (model_id, device_str) -> (model, processor)

def _resolve_device(device: str):
    if device == "auto":
        return torch.device("cuda" if torch.cuda.is_available() else "cpu")
    return torch.device(device)

def get_model_bundle(model_id: str, device: str = "auto"):
    dev = _resolve_device(device)
    key = (model_id, str(dev))
    if key in _MODEL_CACHE:
        model, processor = _MODEL_CACHE[key]
        return model, processor, dev

    processor = AutoImageProcessor.from_pretrained(model_id)
    model = AutoModel.from_pretrained(model_id).to(dev).eval()
    _MODEL_CACHE[key] = (model, processor)
    print(f"[INFO] loaded model_id={model_id} device={dev}")
    return model, processor, dev

@torch.no_grad()
def create_embedding_by_model_id(image_path: str, model_id: str, device: str = "auto"):
    model, processor, dev = get_model_bundle(model_id=model_id, device=device)
    img = Image.open(Path(image_path)).convert("RGB")
    inputs = processor(images=[img], return_tensors="pt")
    inputs = {k: v.to(dev) for k, v in inputs.items()}

    if hasattr(model, "get_image_features"):
        feats = model.get_image_features(**inputs).float()
    else:
        outputs = model(**inputs)
        if hasattr(outputs, "last_hidden_state") and outputs.last_hidden_state is not None:
            feats = outputs.last_hidden_state[:, 0, :].float()
        elif hasattr(outputs, "pooler_output") and outputs.pooler_output is not None:
            feats = outputs.pooler_output.float()
        else:
            raise RuntimeError("Could not extract image embeddings from model outputs.")

    feats = F.normalize(feats, p=2, dim=1)
    return feats[0].detach().cpu().numpy()
