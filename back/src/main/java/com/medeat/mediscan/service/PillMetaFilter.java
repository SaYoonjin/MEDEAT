package com.medeat.mediscan.service;

import com.medeat.mediscan.embedding.EmbeddingItem;
import com.medeat.mediscan.util.ColorNormalizer;
import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.service.DrugInfoService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PillMetaFilter {

    private final DrugInfoService drugInfoService;

    public PillMetaFilter(DrugInfoService drugInfoService) {
        this.drugInfoService = drugInfoService;
    }

    public List<EmbeddingItem> filter(
            List<EmbeddingItem> candidates,
            String shape,
            String frontColor,
            String backColor
    ) {

        // 1️⃣ 사용자 색상 정규화
        Set<String> userColors = new HashSet<>();
        if (frontColor != null && !frontColor.isBlank()) {
            userColors.add(ColorNormalizer.normalize(frontColor));
        }
        if (backColor != null && !backColor.isBlank()) {
            userColors.add(ColorNormalizer.normalize(backColor));
        }

        return candidates.stream()
                .filter(item -> {
                    DrugInfoDto info;
                    try {
                        info = drugInfoService.getDrugInfo(item.getItemSeq().toString());
                    } catch (Exception e) {
                        return false;
                    }
                    if (info == null) return false;

                    // 2️⃣ 형태 필터 (있을 때만)
                    if (shape != null && !shape.isBlank()) {
                        if (info.getDrugShape() == null || !info.getDrugShape().contains(shape)) {
                            return false;
                        }
                    }

                    // 3️⃣ 약 색상 정규화
                    Set<String> pillColors = new HashSet<>();
                    if (info.getColorClass1() != null) {
                        pillColors.add(ColorNormalizer.normalize(info.getColorClass1()));
                    }
                    if (info.getColorClass2() != null) {
                        pillColors.add(ColorNormalizer.normalize(info.getColorClass2()));
                    }

                    // 4️⃣ 색상 교집합
                    pillColors.retainAll(userColors);
                    return !pillColors.isEmpty();
                })
                .collect(Collectors.toList());
    }
}
