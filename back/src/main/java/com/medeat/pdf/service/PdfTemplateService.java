package com.medeat.pdf.service;

import java.util.Map;

public interface PdfTemplateService {
    String generateHtml(String templateName, Map<String, Object> data);
}
