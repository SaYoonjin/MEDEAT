package com.medeat.diet.service.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.medeat.dashboard.dto.DailyChartData;
import com.medeat.dashboard.dto.ScoreSummary;
import com.medeat.diet.service.ChartService;
import com.medeat.pdf.dto.eat.EatPatternSummary;
import com.medeat.pdf.dto.eat.EatPeriodSummary;

@Service
public class ChartServiceImpl implements ChartService {

    /**
     * ✅ 1) 일별 칼로리 막대 그래프 (PDF용)
     *  - X축: 날짜(인덱스로 표시)
     *  - Y축: totalKcal
     */
    @Override
    public String generateDailyChart(List<DailyChartData> dataList) {
        int width = 550;
        int height = 260;

        // 데이터가 없으면 "데이터가 없습니다"만 표시
        if (dataList == null || dataList.isEmpty()) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            try {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);

                g.setColor(Color.GRAY);
                g.drawString("데이터가 없습니다.", 20, 20);
            } finally {
                g.dispose();
            }
            return toBase64(image);
        }


        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            int left = 60;
            int right = 20;
            int top = 30;
            int bottom = 40;

            int chartW = width - left - right;
            int chartH = height - top - bottom;

            int n = dataList.size();

            // 최대 칼로리 찾기
            double maxKcal = 0;
            for (DailyChartData d : dataList) {
                if (d.getTotalKcal() > maxKcal) {
                    maxKcal = d.getTotalKcal();
                }
            }
            if (maxKcal <= 0) {
                maxKcal = 1000; // 최소 스케일
            }

            // 제목
            g.setColor(Color.GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("일별 총 섭취 칼로리 추이", left, top - 10);

            // Y축 라벨 및 가이드 라인 (4분할)
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = 0; i <= 4; i++) {
                double yVal = maxKcal * (i / 4.0);
                int y = top + chartH - (int) (chartH * (yVal / maxKcal));

                g.setColor(new Color(235, 235, 235));
                g.drawLine(left, y, left + chartW, y);

                g.setColor(Color.GRAY);
                String label = (int) yVal + " kcal";
                g.drawString(label, left - 50, y + 4);
            }

            // X축, Y축 경계
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(left, top, chartW, chartH);

            // 막대 폭
            double stepX = (double) chartW / Math.max(1, n);
            int barWidth = (int) (stepX * 0.6);

            // 막대 그리기
            g.setColor(new Color(74, 144, 226)); // 파란색
            for (int i = 0; i < n; i++) {
                DailyChartData d = dataList.get(i);
                double kcal = d.getTotalKcal();

                double ratio = kcal / maxKcal;
                if (ratio > 1) ratio = 1;

                int xCenter = left + (int) (stepX * (i + 0.5));
                int barHeight = (int) (chartH * ratio);
                int x = xCenter - barWidth / 2;
                int y = top + chartH - barHeight;

                g.fillRect(x, y, barWidth, barHeight);
            }

            // X축 인덱스 라벨 (1, 2, 3... 일차)
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            for (int i = 0; i < n; i++) {
                int xCenter = left + (int) (stepX * (i + 0.5));
                String label = (i + 1) + "일차";
                int strW = g.getFontMetrics().stringWidth(label);
                g.drawString(label, xCenter - strW / 2, top + chartH + 12);
            }

        } catch (Exception e) {
            // 필요하면 로깅
        } finally {
            g.dispose();
        }

        return toBase64(image);
    }

    /**
     * ✅ 2) DailyChartData 단일 버전 (기존) - 필요시 그대로 사용
     */
    @Override
    public String generateDailyChart(DailyChartData data) {
        try {
            int width = 800;
            int height = 300;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Noto Sans KR", Font.BOLD, 26));
            g.drawString("일별 영양 요약", 40, 60);

            g.setFont(new Font("Noto Sans KR", Font.PLAIN, 20));
            g.drawString("날짜: " + data.getDate(), 60, 120);
            g.drawString("칼로리: " + data.getTotalKcal(), 60, 160);

            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            return "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ✅ 3) 야식/간식 파이 차트
     */
    @Override
    public String generateSnackChart(EatPatternSummary pattern) {
        int width = 400;
        int height = 220;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            int night = pattern != null ? pattern.getNightCount() : 0;
            int snack = pattern != null ? pattern.getSnackCount() : 0;
            int total = night + snack;

            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.setColor(Color.DARK_GRAY);
            g.drawString("야식 · 간식 패턴", 20, 25);

            if (total <= 0) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g.setColor(Color.GRAY);
                g.drawString("분석 기간 동안 기록된 야식/간식이 없습니다.", 20, 50);
                return toBase64(image);
            }

            // 파이차트 영역
            int cx = 130;
            int cy = 120;
            int r = 80;
            int x = cx - r;
            int y = cy - r;
            int size = r * 2;

            double nightAngle = (double) night / total * 360.0;
            double snackAngle = 360.0 - nightAngle;

            double start = 90; // 위에서 시작

            // 야식 (파란 계열)
            g.setColor(new Color(74, 144, 226));
            g.fill(new Arc2D.Double(x, y, size, size, start, -nightAngle, Arc2D.PIE));
            start -= nightAngle;

            // 간식 (주황 계열)
            g.setColor(new Color(244, 157, 65));
            g.fill(new Arc2D.Double(x, y, size, size, start, -snackAngle, Arc2D.PIE));

            // 가운데 흰색 원 (도넛 느낌)
            g.setColor(Color.WHITE);
            g.fillOval(cx - 35, cy - 35, 70, 70);

            // 가운데 총 횟수 표시
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            String centerTxt = "총 " + total + "회";
            int strW = g.getFontMetrics().stringWidth(centerTxt);
            g.drawString(centerTxt, cx - strW / 2, cy + 4);

            // 범례
            int legendX = 250;
            int legendY = 70;
            int box = 12;

            g.setFont(new Font("SansSerif", Font.PLAIN, 12));

            // 야식
            g.setColor(new Color(74, 144, 226));
            g.fillRect(legendX, legendY, box, box);
            g.setColor(Color.DARK_GRAY);
            g.drawString("야식: " + night + "회", legendX + 18, legendY + 10);
            legendY += 22;

            // 간식
            g.setColor(new Color(244, 157, 65));
            g.fillRect(legendX, legendY, box, box);
            g.setColor(Color.DARK_GRAY);
            g.drawString("간식: " + snack + "회", legendX + 18, legendY + 10);

        } finally {
            g.dispose();
        }

        return toBase64(image);
    }
    
    @Override
    public BufferedImage generateMacroRatioImage(EatPeriodSummary period) {
        int width = 500;
        int height = 260;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            double recCarb = 0.5;
            double recProtein = 0.3;
            double recFat = 0.2;

            double actCarb = period.getCarbRatio();
            double actProtein = period.getProteinRatio();
            double actFat = period.getFatRatio();

            int cx = 150;
            int cy = 130;
            int outerR = 100;
            int innerR = 70;

            int outerX = cx - outerR;
            int outerY = cy - outerR;
            int outerSize = outerR * 2;

            int innerX = cx - innerR;
            int innerY = cy - innerR;
            int innerSize = innerR * 2;

            double start = 90;

            // 권장
            g.setColor(new Color(80, 130, 255));
            g.fill(new Arc2D.Double(outerX, outerY, outerSize, outerSize, start, recCarb * 360, Arc2D.PIE));
            start += recCarb * 360;

            g.setColor(new Color(90, 200, 140));
            g.fill(new Arc2D.Double(outerX, outerY, outerSize, outerSize, start, recProtein * 360, Arc2D.PIE));
            start += recProtein * 360;

            g.setColor(new Color(255, 180, 80));
            g.fill(new Arc2D.Double(outerX, outerY, outerSize, outerSize, start, recFat * 360, Arc2D.PIE));

            // 실제 데이터
            start = 90;

            g.setColor(new Color(60, 100, 220));
            g.fill(new Arc2D.Double(innerX, innerY, innerSize, innerSize, start, actCarb * 360, Arc2D.PIE));
            start += actCarb * 360;

            g.setColor(new Color(70, 170, 120));
            g.fill(new Arc2D.Double(innerX, innerY, innerSize, innerSize, start, actProtein * 360, Arc2D.PIE));
            start += actProtein * 360;

            g.setColor(new Color(230, 150, 60));
            g.fill(new Arc2D.Double(innerX, innerY, innerSize, innerSize, start, actFat * 360, Arc2D.PIE));

        } finally {
            g.dispose();
        }

        return image;
    }


    @Override
    public String generateMacroRatioChart(EatPeriodSummary period) {

        int width = 500;
        int height = 260;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            double recCarb = 0.5;
            double recProtein = 0.3;
            double recFat = 0.2;

            double actCarb = period.getCarbRatio();
            double actProtein = period.getProteinRatio();
            double actFat = period.getFatRatio();

            int cx = 150;
            int cy = 130;
            int outerR = 100;
            int innerR = 70;

            int outerX = cx - outerR;
            int outerY = cy - outerR;
            int innerX = cx - innerR;
            int innerY = cy - innerR;

            int outerSize = outerR * 2;
            int innerSize = innerR * 2;

            // ===================================
            //  권장 비율 ARC (반시계 또는 음수각도 사용)
            // ===================================
            double start = 90;

            g.setColor(new Color(80, 130, 255)); // 탄수화물
            double angle = recCarb * 360;
            g.fill(new Arc2D.Double(outerX, outerY, outerSize, outerSize, start, -angle, Arc2D.PIE));
            start -= angle;

            g.setColor(new Color(90, 200, 140)); // 단백질
            angle = recProtein * 360;
            g.fill(new Arc2D.Double(outerX, outerY, outerSize, outerSize, start, -angle, Arc2D.PIE));
            start -= angle;

            g.setColor(new Color(255, 180, 80)); // 지방
            angle = recFat * 360;
            g.fill(new Arc2D.Double(outerX, outerY, outerSize, outerSize, start, -angle, Arc2D.PIE));


            // ===================================
            //  실제 비율 ARC
            // ===================================
            start = 90;

            g.setColor(new Color(60, 100, 220));
            angle = actCarb * 360;
            g.fill(new Arc2D.Double(innerX, innerY, innerSize, innerSize, start, -angle, Arc2D.PIE));
            start -= angle;

            g.setColor(new Color(70, 170, 120));
            angle = actProtein * 360;
            g.fill(new Arc2D.Double(innerX, innerY, innerSize, innerSize, start, -angle, Arc2D.PIE));
            start -= angle;

            g.setColor(new Color(230, 150, 60));
            angle = actFat * 360;
            g.fill(new Arc2D.Double(innerX, innerY, innerSize, innerSize, start, -angle, Arc2D.PIE));


            // 텍스트
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString("권장 vs 실제 탄단지 비율", 260, 40);

        } finally {
            g.dispose();
        }

        return toBase64(image);
    }



    @Override
    public String generateScoreGaugeChart(ScoreSummary score) {
        int width = 400;
        int height = 220;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            int cx = width / 2;
            int cy = 150;
            int radius = 100;

            // 바깥 반원 배경
            int x = cx - radius;
            int y = cy - radius;
            int size = radius * 2;

            // 영역 색 (빨-노-초)
            g.setColor(new Color(230, 90, 80));
            g.fill(new Arc2D.Double(x, y, size, size, 180, 60, Arc2D.PIE));

            g.setColor(new Color(255, 200, 80));
            g.fill(new Arc2D.Double(x, y, size, size, 240, 60, Arc2D.PIE));

            g.setColor(new Color(90, 200, 140));
            g.fill(new Arc2D.Double(x, y, size, size, 300, 60, Arc2D.PIE));

            // 안쪽 흰색
            g.setColor(Color.WHITE);
            g.fillOval(cx - radius + 20, cy - radius + 20, (radius - 20) * 2, (radius - 20) * 2);

            // 점수 포인터
            int value = Math.max(0, Math.min(100, score.getValue()));
            double angle = 180 + (value * 1.8); // 0~100 -> 180~360

            double rad = Math.toRadians(angle);
            int rPointer = radius - 25;
            int px = cx + (int) (Math.cos(rad) * rPointer);
            int py = cy + (int) (Math.sin(rad) * rPointer);

            g.setColor(Color.DARK_GRAY);
            g.setStroke(new BasicStroke(3));
            g.drawLine(cx, cy, px, py);

            // 점수 텍스트
            g.setFont(new Font("SansSerif", Font.BOLD, 26));
            g.setColor(Color.BLACK);
            String txt = value + " 점";
            int strW = g.getFontMetrics().stringWidth(txt);
            g.drawString(txt, cx - strW / 2, cy);

            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            String sub = "MEDEAT 건강 점수";
            strW = g.getFontMetrics().stringWidth(sub);
            g.drawString(sub, cx - strW / 2, cy + 20);

        } finally {
            g.dispose();
        }

        return toBase64(image);
    }

    @Override
    public String generateScoreTrendChart(List<DailyChartData> dataList, int goalKcal) {
        int width = 550;
        int height = 260;

        if (dataList == null || dataList.isEmpty()) {
            BufferedImage empty = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = empty.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.GRAY);
            g.drawString("데이터가 없습니다.", 20, 20);
            g.dispose();
            return toBase64(empty);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            int left = 60;
            int right = 20;
            int top = 30;
            int bottom = 40;

            int chartW = width - left - right;
            int chartH = height - top - bottom;

            int n = dataList.size();
            double[] scores = new double[n];

            for (int i = 0; i < n; i++) {
                DailyChartData d = dataList.get(i);
                scores[i] = calcDailyScore(d, goalKcal);
            }

            // 축 그리기
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(left, top, chartW, chartH);

            g.setColor(Color.GRAY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("일별 건강 점수 추이", left, top - 10);

            // Y축 라벨 (0~100 기준)
            for (int i = 0; i <= 5; i++) {
                double yVal = 100 - i * 20;
                int y = top + (int) ((100 - yVal) / 100 * chartH);
                g.setColor(new Color(230, 230, 230));
                g.drawLine(left, y, left + chartW, y);
                g.setColor(Color.GRAY);
                g.drawString((int) yVal + "", left - 30, y + 4);
            }

            // X 간격
            double stepX = (double) chartW / Math.max(1, n - 1);

            // 라인 그리기
            g.setColor(new Color(80, 130, 255));
            g.setStroke(new BasicStroke(2));

            for (int i = 0; i < n - 1; i++) {
                int x1 = left + (int) (i * stepX);
                int x2 = left + (int) ((i + 1) * stepX);

                int y1 = top + (int) ((100 - scores[i]) / 100 * chartH);
                int y2 = top + (int) ((100 - scores[i + 1]) / 100 * chartH);

                g.drawLine(x1, y1, x2, y2);
            }

            // 점 그리기
            g.setColor(new Color(80, 130, 255));
            for (int i = 0; i < n; i++) {
                int x = left + (int) (i * stepX);
                int y = top + (int) ((100 - scores[i]) / 100 * chartH);
                g.fillOval(x - 3, y - 3, 6, 6);
            }

        } finally {
            g.dispose();
        }

        return toBase64(image);
    }

    // === 내부 유틸 메서드들 ===

    private String toBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            return null;
        }
    }

    private double calcDailyScore(DailyChartData d, int goalKcal) {
        if (goalKcal <= 0) goalKcal = 1800;

        // 1) 칼로리 점수
        double kcal = d.getTotalKcal();
        double diffRatio = Math.abs(goalKcal - kcal) / goalKcal;  // 편차%
        double calorieScore = (1 - diffRatio) * 100.0;
        if (calorieScore < 0) calorieScore = 0;

        // 2) 탄단지 점수
        double carb = d.getTotalCarb();
        double protein = d.getTotalProtein();
        double fat = d.getTotalFat();

        double macroKcal = carb * 4 + protein * 4 + fat * 9;
        double actCarbRatio = macroKcal > 0 ? (carb * 4) / macroKcal : 0.5;
        double actProteinRatio = macroKcal > 0 ? (protein * 4) / macroKcal : 0.3;
        double actFatRatio = macroKcal > 0 ? (fat * 9) / macroKcal : 0.2;

        double recCarb = 0.5;
        double recProtein = 0.3;
        double recFat = 0.2;

        double devCarb = Math.abs(actCarbRatio - recCarb) * 100;
        double devProtein = Math.abs(actProteinRatio - recProtein) * 100;
        double devFat = Math.abs(actFatRatio - recFat) * 100;

        double avgDev = (devCarb + devProtein + devFat) / 3.0;

        double macroScore = 100 - (avgDev * 1.5); // 보정값 1.5
        if (macroScore < 0) macroScore = 0;

        // 3) 패턴 감점
        int penalty = d.getNightCount() * 5 + d.getSnackCount() * 3;

        // 4) 최종 점수
        double score = ((calorieScore + macroScore) / 2.0) - penalty;

        if (score < 0) score = 0;
        if (score > 100) score = 100;

        return score;
    }
}
