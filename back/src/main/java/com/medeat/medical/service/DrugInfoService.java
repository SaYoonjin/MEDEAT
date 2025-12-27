package com.medeat.medical.service;

import java.util.List;

import com.medeat.medical.dto.DrugInfoDto;

public interface DrugInfoService {

    List<DrugInfoDto> searchDrug(String keyword) throws Exception;
    DrugInfoDto fetchDetailByItemSeq(String itemSeq);
    List<DrugInfoDto> searchDrugHybrid(String keyword) throws Exception;

    DrugInfoDto getDrugInfo(String itemSeq) throws Exception;
    DrugInfoDto getDrugInfo(Long itemSeq, String nameHint) throws Exception;

    
 // ✅ PDF / MEDI 분석용
    List<DrugInfoDto> getDrugInfoListByItemSeq(List<Long> itemSeqList);
}
