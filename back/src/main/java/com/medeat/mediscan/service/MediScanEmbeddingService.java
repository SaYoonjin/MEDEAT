package com.medeat.mediscan.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediScanEmbeddingService {
    float[] createEmbedding(MultipartFile image);
}
