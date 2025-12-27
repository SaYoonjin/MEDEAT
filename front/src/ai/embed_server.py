from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse
import requests, tempfile, os
import numpy as np
from rembg import remove
from embed_items import create_embedding_by_model_id

app = FastAPI()

MODEL_IDS = [
    "facebook/dinov2-base",
    "google/siglip2-base-patch16-224",
]

def l2norm(v, eps=1e-12):
    v = np.asarray(v, dtype=np.float32)
    n = np.linalg.norm(v)
    if n < eps or not np.isfinite(n):
        return None
    return (v / n).astype(np.float32)

def save_nobg_png_bytes(img_bytes: bytes, out_path: str):
    out_bytes = remove(img_bytes)
    with open(out_path, "wb") as f:
        f.write(out_bytes)

def add_emb(embs, img_path, model_id):
    e = create_embedding_by_model_id(img_path, model_id=model_id, device="auto")
    e = l2norm(e)
    if e is not None and e.size > 0:
        embs.append(e)

def embed_bytes_pair(front_bytes: bytes, back_bytes: bytes | None):
    with tempfile.TemporaryDirectory(prefix="pill_embed_") as td:
        # front 저장
        front_path = os.path.join(td, "front.png")
        with open(front_path, "wb") as f:
            f.write(front_bytes)

        front_nobg = os.path.join(td, "front_nobg.png")
        save_nobg_png_bytes(front_bytes, front_nobg)

        # back 저장(있으면)
        back_path = None
        back_nobg = None
        if back_bytes:
            back_path = os.path.join(td, "back.png")
            with open(back_path, "wb") as f:
                f.write(back_bytes)
            back_nobg = os.path.join(td, "back_nobg.png")
            save_nobg_png_bytes(back_bytes, back_nobg)

        embs = []
        for model_id in MODEL_IDS:
            # front 원본 + nobg
            add_emb(embs, front_path, model_id)
            add_emb(embs, front_nobg, model_id)

            # back 원본 + nobg (옵션)
            if back_path and back_nobg:
                add_emb(embs, back_path, model_id)
                add_emb(embs, back_nobg, model_id)

        if not embs:
            return []

        emb = np.mean(np.stack(embs, axis=0), axis=0)
        emb = l2norm(emb)
        if emb is None:
            return []
        return emb.tolist()

@app.post("/embed")
async def embed(front: UploadFile = File(...), back: UploadFile | None = File(None)):
    """
    multipart/form-data
    - front: file (required)
    - back : file (optional)
    """
    try:
        front_bytes = await front.read()
        if not front_bytes:
            return JSONResponse({"error": "front is required"}, status_code=400)

        back_bytes = None
        if back is not None:
            back_bytes = await back.read()
            if not back_bytes:
                back_bytes = None

        embedding = embed_bytes_pair(front_bytes, back_bytes)
        return JSONResponse({"embedding": embedding})

    except Exception as e:
        return JSONResponse({"error": str(e)}, status_code=500)

@app.post("/embed-from-url")
async def embed_from_url(payload: dict):
    """
    application/json
    {"url": "..."}
    """
    try:
        url = payload.get("url")
        if not url:
            return JSONResponse({"error": "url is required"}, status_code=400)

        r = requests.get(url, timeout=15)
        if r.status_code != 200 or not r.content:
            return JSONResponse({"error": f"download failed: {r.status_code}"}, status_code=400)

        embedding = embed_bytes_pair(r.content, None)
        return JSONResponse({"embedding": embedding})

    except Exception as e:
        return JSONResponse({"error": str(e)}, status_code=500)
