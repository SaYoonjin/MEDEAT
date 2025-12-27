package com.medeat.mediscan.controller;

import com.medeat.mediscan.dto.MediScanRequest;
import com.medeat.mediscan.dto.MediScanResult;
import com.medeat.mediscan.service.MediScanService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mediscan")
public class MediScanRestController {

    private final MediScanService mediScanService;

    public MediScanRestController(MediScanService mediScanService) {
        this.mediScanService = mediScanService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<List<MediScanResult>> scan(
            @RequestPart("frontImage") MultipartFile frontImage,
            @RequestPart("backImage") MultipartFile backImage,
            @RequestPart(value = "drugShape", required = false) String drugShape,
            @RequestPart(value = "frontColor", required = false) String frontColor,
            @RequestPart(value = "backColor", required = false) String backColor
    ) {

        MediScanRequest request = new MediScanRequest();
        request.setFrontImage(frontImage);
        request.setBackImage(backImage);
        request.setDrugShape(drugShape);
        request.setFrontColor(frontColor);
        request.setBackColor(backColor);

        List<MediScanResult> result = mediScanService.scan(request);
        return ResponseEntity.ok(result);
    }
}
