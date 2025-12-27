package com.medeat.pdf.service.impl;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.medeat.pdf.service.PdfTemplateService;

import java.util.Map;

@Service
public class PdfTemplateServiceImpl implements PdfTemplateService {

	private final SpringTemplateEngine templateEngine;

    public PdfTemplateServiceImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String generateHtml(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateName, context);
    }
}
