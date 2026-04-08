package com.medeat.medical.service;

import com.medeat.medical.dao.DrugInfoDao;
import com.medeat.medical.dto.DrugInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DrugDetailService {

    private static final Logger log = LoggerFactory.getLogger(DrugDetailService.class);

    private final DrugInfoDao drugInfoDao;
    private final DrugInfoService drugInfoService;

    public DrugDetailService(DrugInfoDao drugInfoDao, DrugInfoService drugInfoService) {
        this.drugInfoDao = drugInfoDao;
        this.drugInfoService = drugInfoService;
    }

    public DrugInfoDto getOrFetchDetail(long itemSeq) {
        log.debug("Fetching drug detail for itemSeq={}", itemSeq);

        DrugInfoDto cached = drugInfoDao.selectByItemSeq(itemSeq);
        if (hasRealDetail(cached)) {
            log.debug("Returning cached drug detail for itemSeq={}", itemSeq);
            return cached;
        }

        try {
            DrugInfoDto fetched = drugInfoService.getDrugInfo(String.valueOf(itemSeq));
            if (hasRealDetail(fetched)) {
                log.debug("Successfully fetched drug detail from external API. itemSeq={}", itemSeq);
                return fetched;
            }

            log.debug("External API returned incomplete detail. Falling back to cached value. itemSeq={}", itemSeq);
            return cached;
        } catch (Exception e) {
            log.warn("Failed to fetch drug detail from external API. Falling back to cached value. itemSeq={}", itemSeq, e);
            return cached;
        }
    }

    private boolean hasRealDetail(DrugInfoDto dto) {
        return dto != null && dto.getEfcyQesitm() != null && !dto.getEfcyQesitm().trim().isEmpty();
    }
}
