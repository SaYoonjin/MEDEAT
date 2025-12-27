package com.medeat.medical.controller;

import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.service.DrugInfoService;
import com.medeat.mediscan.service.MediScanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drug")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class DrugInfoRestController {

    private final DrugInfoService drugInfoService;
    private final MediScanService mediScanService;

    public DrugInfoRestController(
            DrugInfoService drugInfoService,
            MediScanService mediScanService
    ) {
        this.drugInfoService = drugInfoService;
        this.mediScanService = mediScanService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(drugInfoService.searchDrug(keyword));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("약 검색 실패: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{itemSeq}")
    public ResponseEntity<?> getDrugDetail(@PathVariable String itemSeq) {
        try {
            DrugInfoDto info = drugInfoService.getDrugInfo(itemSeq);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("약 상세 조회 실패: " + e.getMessage());
        }
    }
}
