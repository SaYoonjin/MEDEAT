package com.medeat.medical.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.medeat.medical.dto.DrugInfoDto;
import java.util.List;

@Mapper
public interface DrugInfoDao {

    // 단건 조회
    DrugInfoDto selectByItemSeq(@Param("itemSeq") Long itemSeq);

    // ✅ MediScan용 다건 조회 (핵심)
    List<DrugInfoDto> selectByItemSeqList(
        @Param("itemSeqList") List<Long> itemSeqList
    );

    int upsertDrugInfo(DrugInfoDto dto);
}
