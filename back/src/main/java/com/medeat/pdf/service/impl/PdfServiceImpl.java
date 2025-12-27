package com.medeat.pdf.service.impl;

import com.medeat.pdf.dto.eat.EatPdfRequestDto;
import com.medeat.pdf.dto.eat.EatPdfUserDto;
import com.medeat.pdf.dto.medi.MediEatPdfDto;
import com.medeat.pdf.service.PdfService;
import com.medeat.pdf.service.PdfTemplateService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medeat.notification.feed.service.NotificationFeedService;
import com.medeat.notification.feed.dto.NotificationFeedType;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger log =
            LoggerFactory.getLogger(PdfServiceImpl.class);

    private final PdfTemplateService pdfTemplateService;
    
    @Autowired
    private NotificationFeedService notificationFeedService;


    public PdfServiceImpl(PdfTemplateService pdfTemplateService) {
        this.pdfTemplateService = pdfTemplateService;
    }

    @Override
    public void generateDietAnalysisPdf(
            EatPdfRequestDto req,
            OutputStream out,
            EatPdfUserDto user
    ) throws Exception {

        Map<String, Object> data = new HashMap<>();

        // =========================
        // 공통
        // =========================
        data.put("mode", req.getMode());
        data.put("startDate", req.getStartDate());
        data.put("endDate", req.getEndDate());
        data.put("user", user);

        // =========================
        // EAT
        // =========================
        data.put("score", req.getScore());
        data.put("periodSummary", req.getPeriodSummary());
        data.put("pattern", req.getPattern());
        data.put("dailyChart", req.getDailyChart());

        // =========================
        // MEDI_EAT
        // =========================
        data.put("medDashboard", req.getMedDashboard());

        // =========================
        // 추천
        // =========================
        data.put("foods", req.getFoods());
        data.put("alternatives", req.getAlternatives());
        data.put("cookingAdvices", req.getCookingAdvices());

        // =========================
        // 차트
        // =========================
        String chartSnack = req.getChartSnack();
        String chartDaily = req.getChartDaily();

        data.put("chartSnack", chartSnack);
        data.put("chartDaily", chartDaily);

        // 🔍 === 여기 로그가 핵심 ===
        log.info("PDF chartSnack prefix = {}",
                chartSnack != null && chartSnack.length() > 30
                        ? chartSnack.substring(0, 30)
                        : chartSnack);

        log.info("PDF chartDaily prefix = {}",
                chartDaily != null && chartDaily.length() > 30
                        ? chartDaily.substring(0, 30)
                        : chartDaily);

        // =========================
        // HTML 생성
        // =========================
        String html = pdfTemplateService.generateHtml("pdf/diet_report", data);

        // =========================
        // PDF 렌더링
        // =========================
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();

        builder.withHtmlContent(
                html,
                PdfServiceImpl.class.getResource("/").toExternalForm()
        );

        builder.toStream(out);

        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/NotoSansKR-Regular.ttf"),
                "Noto Sans KR"
        );
        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/NotoSansKR-Bold.ttf"),
                "Noto Sans KR"
        );

        builder.run();

        log.info("PDF 생성 완료");
    }

    // (지금은 안 써도 됨, 차트 직접 생성 시 사용)
    private String toBase64Image(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            return "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("차트 Base64 변환 실패", e);
        }
    }

    @Override
    public void generateMediEatPdf(MediEatPdfDto pdf, OutputStream out, Long userId) throws Exception {

        Map<String, Object> data = new HashMap<>();

        // =========================
        // MEDI_EAT PDF Root Data
        // =========================
        data.put("startDate", pdf.getStartDate());
        data.put("endDate", pdf.getEndDate());
        data.put("generatedAt", pdf.getGeneratedAt());

        data.put("patientSummary", pdf.getPatientSummary());
        data.put("adherenceStats", pdf.getAdherenceStats());
        data.put("safetyReport", pdf.getSafetyReport());   // ⭐ 추가
        data.put("clinicalDiet", pdf.getClinicalDiet());
        data.put("dailyHistories", pdf.getDailyHistories());
        data.put("disclaimer", pdf.getDisclaimer());
        
        data.put("medicationDailyLogs", pdf.getMedicationDailyLogs());
        data.put("medicationList", pdf.getMedicationList());
        data.put("drugInfoList", pdf.getDrugInfoList());


        // =========================
        // HTML 생성
        // =========================
        String html = pdfTemplateService.generateHtml(
            "pdf/medi_report",
            data
        );

        // =========================
        // PDF 렌더링
        // =========================
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();

        builder.withHtmlContent(
            html,
            PdfServiceImpl.class.getResource("/").toExternalForm()
        );

        builder.toStream(out);

        builder.useFont(
            () -> getClass().getResourceAsStream("/fonts/NotoSansKR-Regular.ttf"),
            "Noto Sans KR"
        );
        builder.useFont(
            () -> getClass().getResourceAsStream("/fonts/NotoSansKR-Bold.ttf"),
            "Noto Sans KR"
        );

        builder.run();

        log.info("MEDI_EAT PDF 생성 완료");
    }

}
