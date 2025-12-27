package com.medeat.mediscan.service;

import com.medeat.mediscan.embedding.EmbeddingItem;
import com.medeat.mediscan.util.ImprintNormalizer;
import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.service.DrugInfoService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ImprintFilter {

    private final DrugInfoService drugInfoService;

    public ImprintFilter(DrugInfoService drugInfoService) {
        this.drugInfoService = drugInfoService;
    }

    /**
     * @param candidates embedding 후보
     * @param userFrontImprint 사용자 앞면 각인(선택)
     * @param userBackImprint 사용자 뒷면 각인(선택)
     * @return 각인 기반으로 좁힌 후보 (각인 입력이 없으면 candidates 그대로 반환)
     */
    public List<EmbeddingItem> filterByImprint(
            List<EmbeddingItem> candidates,
            String userFrontImprint,
            String userBackImprint
    ) {
        boolean hasFront = userFrontImprint != null && !userFrontImprint.isBlank();
        boolean hasBack = userBackImprint != null && !userBackImprint.isBlank();

        // 사용자 각인 입력이 아예 없으면: 필터링 안 함
        if (!hasFront && !hasBack) return candidates;

        String uFront = ImprintNormalizer.normalize(userFrontImprint);
        String uBack  = ImprintNormalizer.normalize(userBackImprint);

        return candidates.stream()
                .filter(item -> {
                    DrugInfoDto info;
                    try {
                        info = drugInfoService.getDrugInfo(item.getItemSeq().toString());
                    } catch (Exception e) {
                        return false;
                    }
                    if (info == null) return false;

                    String pFront = ImprintNormalizer.normalize(info.getPrintFront());
                    String pBack  = ImprintNormalizer.normalize(info.getPrintBack());

                    // ✅ 핵심: 앞/뒤가 바뀌어도 맞게 처리
                    // (사용자가 앞/뒤 헷갈려도 TYLENOL이 필터링되게)
                    boolean matchNormal = match(uFront, pFront) && match(uBack, pBack);
                    boolean matchSwapped = match(uFront, pBack) && match(uBack, pFront);

                    // 사용자가 하나만 입력했으면 그 하나만 체크
                    if (hasFront && !hasBack) {
                        return matchOne(uFront, pFront, pBack);
                    }
                    if (!hasFront && hasBack) {
                        return matchOne(uBack, pFront, pBack);
                    }

                    return matchNormal || matchSwapped;
                })
                .collect(Collectors.toList());
    }

    private boolean match(String user, String pill) {
        // user가 없으면 통과(=조건 없음)
        if (user == null || user.isBlank()) return true;
        // user가 있는데 pill이 없으면 불일치
        if (pill == null || pill.isBlank()) return false;

        // 완전 일치 OR 포함(각인 일부만 입력할 수도 있어서)
        return pill.equals(user) || pill.contains(user) || user.contains(pill);
    }

    private boolean matchOne(String u, String pFront, String pBack) {
        // 앞/뒤 중 어디에 있든 하나라도 매칭되면 통과
        return match(u, pFront) || match(u, pBack);
    }
}
