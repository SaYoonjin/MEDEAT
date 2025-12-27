package com.medeat.mediscan.service;

import com.medeat.mediscan.embedding.EmbeddingItem;
import com.medeat.mediscan.util.ColorNormalizer;
import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.service.DrugInfoService;
import org.springframework.stereotype.Component;

@Component
public class PillMetaScorer {

    private final DrugInfoService drugInfoService;

    public PillMetaScorer(DrugInfoService drugInfoService) {
        this.drugInfoService = drugInfoService;
    }

    /**
     * 기존 similarity score에 메타 점수를 더해서 최종 점수 반환
     */
    public double score(
            EmbeddingItem item,
            String shape,
            String frontColor,
            String backColor
    ) {
        double score = item.getScore(); // 기본 임베딩 유사도

        DrugInfoDto info;
        try {
            info = drugInfoService.getDrugInfo(item.getItemSeq().toString());
        } catch (Exception e) {
            // 메타 정보 못 가져오면 기본 점수 유지
            return score;
        }

        if (info == null) return score;

        // 1️⃣ 형태 보너스
        if (shape != null && !shape.isBlank()
                && info.getDrugShape() != null
                && info.getDrugShape().contains(shape)) {
            score += 0.05;
        }

        // 2️⃣ 색상 보너스
        String userFront = ColorNormalizer.normalize(frontColor);
        String userBack  = ColorNormalizer.normalize(backColor);

        String pillC1 = ColorNormalizer.normalize(info.getColorClass1());
        String pillC2 = ColorNormalizer.normalize(info.getColorClass2());

        // 앞/뒤 순서 무시
        if (userFront != null && (userFront.equals(pillC1) || userFront.equals(pillC2))) {
            score += 0.07;
        }
        if (userBack != null && (userBack.equals(pillC1) || userBack.equals(pillC2))) {
            score += 0.05;
        }

        return score;
    }

}
