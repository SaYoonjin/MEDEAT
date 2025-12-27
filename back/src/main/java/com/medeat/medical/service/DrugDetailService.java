package com.medeat.medical.service;

import com.medeat.medical.dao.DrugInfoDao;
import com.medeat.medical.dto.DrugInfoDto;
import org.springframework.stereotype.Service;

@Service
public class DrugDetailService {

    private final DrugInfoDao drugInfoDao;
    private final DrugInfoService drugInfoService;

    public DrugDetailService(DrugInfoDao drugInfoDao, DrugInfoService drugInfoService) {
        this.drugInfoDao = drugInfoDao;
        this.drugInfoService = drugInfoService;
    }

    public DrugInfoDto getOrFetchDetail(long itemSeq) {
        System.out.println("[DETAIL] 요청 itemSeq = " + itemSeq);

        DrugInfoDto cached = drugInfoDao.selectByItemSeq(itemSeq);
        System.out.println("[DETAIL] cached 존재 여부 = " + (cached != null));
        System.out.println("[DETAIL] cached efcy = "
                + (cached != null ? cached.getEfcyQesitm() : null));

        if (hasRealDetail(cached)) {
            System.out.println("[DETAIL] 캐시에 상세 정보 있음 → 캐시 반환");
            return cached;
        }

        try {
            String nameHint = (cached != null ? cached.getItemName() : null);
            System.out.println("[DETAIL] 외부 API 호출 시작, nameHint = " + nameHint);

            DrugInfoDto fetched = drugInfoService.getDrugInfo(
            	    String.valueOf(itemSeq)
            	);


            System.out.println("[DETAIL] fetched = " + fetched);
            System.out.println("[DETAIL] fetched efcy = "
                    + (fetched != null ? fetched.getEfcyQesitm() : null));

            if (hasRealDetail(fetched)) {
                System.out.println("[DETAIL] 외부 API 상세 정보 성공");
                return fetched;
            }

            System.out.println("[DETAIL] 외부 API 상세 정보 비어있음 → 캐시 반환");
            return cached;

        } catch (Exception e) {
            System.out.println("[DETAIL] 외부 API 호출 중 예외 발생");
            e.printStackTrace();
            return cached;
        }
    }

    private boolean hasRealDetail(DrugInfoDto d) {
        if (d == null) return false;
        return d.getEfcyQesitm() != null && !d.getEfcyQesitm().trim().isEmpty();
    }
}
