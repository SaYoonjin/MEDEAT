package com.medeat.mediscan.service.impl;

import com.medeat.mediscan.dto.MediScanRequest;
import com.medeat.mediscan.dto.MediScanResult;
import com.medeat.mediscan.embedding.EmbeddingIndex;
import com.medeat.mediscan.embedding.EmbeddingItem;
import com.medeat.mediscan.service.MediScanEmbeddingService;
import com.medeat.mediscan.service.MediScanService;
import com.medeat.mediscan.util.ImprintNormalizer;

import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.service.DrugInfoService;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MediScanServiceImpl implements MediScanService {

    private final MediScanEmbeddingService embeddingService;
    private final EmbeddingIndex embeddingIndex;
    private final DrugInfoService drugInfoService;

    public MediScanServiceImpl(
            MediScanEmbeddingService embeddingService,
            EmbeddingIndex embeddingIndex,
            DrugInfoService drugInfoService
    ) {
        this.embeddingService = embeddingService;
        this.embeddingIndex = embeddingIndex;
        this.drugInfoService = drugInfoService;
    }

    @Override
    public List<MediScanResult> scan(MediScanRequest request) {

        /* =====================================================
           1️⃣ 이미지 임베딩
        ===================================================== */
        float[] frontEmb = embeddingService.createEmbedding(request.getFrontImage());
        float[] backEmb  = embeddingService.createEmbedding(request.getBackImage());

        /* =====================================================
           2️⃣ 앞/뒤 평균
        ===================================================== */
        float[] query = new float[frontEmb.length];
        for (int i = 0; i < query.length; i++) {
            query[i] = (frontEmb[i] + backEmb[i]) / 2f;
        }

        /* =====================================================
           3️⃣ Top 50 유사도 검색
        ===================================================== */
        List<EmbeddingItem> topN = embeddingIndex.searchTopN(query, 50);

        /* =====================================================
           4️⃣ 각인(imprint) 필터 (최우선)
        ===================================================== */
        String userFrontImprint = normalizeImprint(request.getFrontImprint());
        String userBackImprint  = normalizeImprint(request.getBackImprint());

        List<EmbeddingItem> afterImprint = topN.stream()
                .filter(item -> {
                    // 각인 입력이 없으면 통과
                    if (userFrontImprint == null && userBackImprint == null) {
                        return true;
                    }

                    DrugInfoDto info;
                    try {
                        info = drugInfoService.getDrugInfo(
                                item.getItemSeq().toString()
                        );
                    } catch (Exception e) {
                        return false;
                    }
                    if (info == null) return false;

                    String dbFront = normalizeImprint(info.getPrintFront());
                    String dbBack  = normalizeImprint(info.getPrintBack());

                    // 앞/뒤 그대로
                    boolean normal =
                            matchImprint(userFrontImprint, dbFront)
                         && matchImprint(userBackImprint, dbBack);

                    // 앞/뒤 바뀜
                    boolean swapped =
                            matchImprint(userFrontImprint, dbBack)
                         && matchImprint(userBackImprint, dbFront);

                    // 하나만 입력된 경우
                    if (userFrontImprint != null && userBackImprint == null) {
                        return matchImprint(userFrontImprint, dbFront)
                            || matchImprint(userFrontImprint, dbBack);
                    }
                    if (userFrontImprint == null && userBackImprint != null) {
                        return matchImprint(userBackImprint, dbFront)
                            || matchImprint(userBackImprint, dbBack);
                    }

                    return normal || swapped;
                })
                .collect(Collectors.toList());

        /* =====================================================
           5️⃣ 형태 + 색상 필터 (보조)
        ===================================================== */
        List<EmbeddingItem> filtered = afterImprint.stream()
                .filter(item -> {
                    DrugInfoDto info;
                    try {
                        info = drugInfoService.getDrugInfo(
                                item.getItemSeq().toString()
                        );
                    } catch (Exception e) {
                        return false;
                    }
                    if (info == null) return false;

                    return matchForm(
                                request.getDrugShape(),
                                info.getDrugShape()
                           )
                        && matchColor(
                                request.getFrontColor(),
                                request.getBackColor(),
                                info.getColorClass1(),
                                info.getColorClass2()
                           );
                })
                .collect(Collectors.toList());

        System.out.println("[MediScan] candidates after similarity = " + topN.size());
        System.out.println("[MediScan] after imprint filter = " + afterImprint.size());
        System.out.println("[MediScan] after shape/color filter = " + filtered.size());

        /* =====================================================
        6️⃣ 점수 재계산 + Top 5
     ===================================================== */
     return filtered.stream()
             .map(item -> {
                 try {
                     DrugInfoDto info = drugInfoService.getDrugInfo(
                             item.getItemSeq().toString()
                     );
                     if (info == null) return null;

                     double score = item.getScore();

                     // 🔥 각인 보너스
                     double bonus = imprintBonus(
                             request.getFrontImprint(),
                             request.getBackImprint(),
                             info
                     );

                     if (bonus > 0) {
                         System.out.println(
                             "[IMPRINT BONUS +" + bonus + "] "
                             + info.getItemName()
                         );
                     }

                     return new MediScanResult(
                             item.getItemSeq(),
                             info.getItemName(),
                             score + bonus,
                             info
                     );

                 } catch (Exception e) {
                     return null;
                 }
             })
             .filter(Objects::nonNull)
             .sorted(Comparator.comparingDouble(MediScanResult::getScore).reversed())
             .limit(5)
             .collect(Collectors.toList());

    }

    /* =====================================================
       🔹 각인 유틸
    ===================================================== */
    private String normalizeImprint(String s) {
        if (s == null || s.isBlank()) return null;
        return ImprintNormalizer.normalize(s);
    }

    private boolean matchImprint(String user, String db) {
        if (user == null) return true;
        if (db == null) return false;
        return db.equals(user) || db.contains(user) || user.contains(db);
    }
    
    private double imprintBonus(
            String userFront,
            String userBack,
            DrugInfoDto info
    ) {
        String uf = normalizeImprint(userFront);
        String ub = normalizeImprint(userBack);

        String df = normalizeImprint(info.getPrintFront());
        String db = normalizeImprint(info.getPrintBack());

        double bonus = 0.0;

        // 앞/뒤 모두 입력된 경우
        if (uf != null && ub != null) {
            if (matchImprint(uf, df) && matchImprint(ub, db)) {
                bonus = 0.2;
            } else if (matchImprint(uf, db) && matchImprint(ub, df)) {
                bonus = 0.2;
            } else if (matchImprint(uf, df) || matchImprint(uf, db)
                    || matchImprint(ub, df) || matchImprint(ub, db)) {
                bonus = 0.1;
            }
        }
        // 하나만 입력된 경우
        else if (uf != null) {
            if (matchImprint(uf, df) || matchImprint(uf, db)) {
                bonus = 0.15;
            }
        }
        else if (ub != null) {
            if (matchImprint(ub, df) || matchImprint(ub, db)) {
                bonus = 0.15;
            }
        }

        return bonus;
    }


    /* =====================================================
       🔹 알약 형태 매칭
    ===================================================== */
    private boolean matchForm(String userForm, String dbShape) {
        if (userForm == null || userForm.isBlank()) return true;
        if (dbShape == null) return false;

        PillForm u = normalizeUserForm(userForm);
        PillForm d = normalizeDbForm(dbShape);

        return u != null && u == d;
    }

    private PillForm normalizeUserForm(String s) {
        if (s == null) return null;

        return switch (s) {
            case "정제" -> PillForm.TABLET;
            case "캡슐" -> PillForm.CAPSULE;
            case "연질캡슐" -> PillForm.SOFT_CAPSULE;
            case "필름" -> PillForm.FILM;
            default -> null;
        };
    }

    private PillForm normalizeDbForm(String s) {
        if (s == null) return null;

        String t = s.replaceAll("\\s+", "");

        if (t.contains("연질")) return PillForm.SOFT_CAPSULE;
        if (t.contains("캡슐")) return PillForm.CAPSULE;
        if (t.contains("필름")) return PillForm.FILM;
        if (t.contains("정")) return PillForm.TABLET;

        return null;
    }

    /* =====================================================
       🔹 색상 매칭 (앞/뒤 무시)
    ===================================================== */
    private boolean matchColor(
            String userFront,
            String userBack,
            String dbColor1,
            String dbColor2
    ) {
        if ((userFront == null || userFront.isBlank())
                && (userBack == null || userBack.isBlank())) {
            return true;
        }

        Set<String> userColors = new HashSet<>();
        if (userFront != null && !userFront.isBlank()) {
            userColors.add(normalizeColor(userFront));
        }
        if (userBack != null && !userBack.isBlank()) {
            userColors.add(normalizeColor(userBack));
        }

        Set<String> dbColors = new HashSet<>();
        if (dbColor1 != null) dbColors.add(normalizeColor(dbColor1));
        if (dbColor2 != null) dbColors.add(normalizeColor(dbColor2));

        userColors.retainAll(dbColors);
        return !userColors.isEmpty();
    }

    private String normalizeColor(String c) {
        if (c == null) return null;
        String t = c.trim();

        if (t.contains("하") || t.contains("백") || t.contains("흰")) return "하양";
        if (t.contains("노") || t.contains("황")) return "노랑";
        if (t.contains("분")) return "분홍";
        if (t.contains("파") || t.contains("청")) return "파랑";
        if (t.contains("연두") || t.contains("초록") || t.contains("녹")) return "연두";
        if (t.contains("검") || t.contains("흑")) return "검정";
        if (t.contains("갈")) return "갈색";

        return t;
    }

    /* =====================================================
       🔹 내부 ENUM
    ===================================================== */
    private enum PillForm {
        TABLET,
        CAPSULE,
        SOFT_CAPSULE,
        FILM
    }
}
