package com.medeat.mediscan.service;

import com.medeat.mediscan.dto.MediScanRequest;
import com.medeat.mediscan.dto.MediScanResult;

import java.util.List;

public interface MediScanService {
    List<MediScanResult> scan(MediScanRequest request);
}
