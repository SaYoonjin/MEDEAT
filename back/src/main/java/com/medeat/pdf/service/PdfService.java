package com.medeat.pdf.service;

import java.io.OutputStream;

import com.medeat.pdf.dto.eat.EatPdfRequestDto;
import com.medeat.pdf.dto.eat.EatPdfUserDto;
import com.medeat.pdf.dto.medi.MediEatPdfDto;

public interface PdfService {

    // ✅ 기존 EAT PDF (유지)
    void generateDietAnalysisPdf(
        EatPdfRequestDto req,
        OutputStream out,
        EatPdfUserDto user
    ) throws Exception;

    // 🆕 MEDI_EAT PDF
    void generateMediEatPdf(
        MediEatPdfDto pdf,
        OutputStream out,
        Long userId
    ) throws Exception;
}

