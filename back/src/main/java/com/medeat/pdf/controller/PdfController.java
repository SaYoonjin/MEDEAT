package com.medeat.pdf.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;
import com.medeat.dashboard.dto.EatModeAnalysisResponse;
import com.medeat.dashboard.dto.MedDashboard;
import com.medeat.dashboard.dto.MedModeAnalysisResponse;
import com.medeat.diet.dto.DietLogDto;
import com.medeat.diet.service.ChartService;
import com.medeat.diet.service.DietService;
import com.medeat.diet.service.EatAnalysisService;
import com.medeat.food.dto.FoodDto;
import com.medeat.medical.dto.DrugInfoDto;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.dto.MedicationLogDto;
import com.medeat.medical.service.DrugInfoService;
import com.medeat.medical.service.MedAnalysisService;
import com.medeat.medical.service.MedicationService;
import com.medeat.pdf.assembler.MediEatPdfAssembler;
import com.medeat.pdf.dto.eat.EatPdfRequestDto;
import com.medeat.pdf.dto.medi.MediEatPdfDto;
import com.medeat.pdf.service.PdfService;
import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.service.GamificationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

	private static final Logger log = LoggerFactory.getLogger(PdfController.class);

	private final PdfService pdfService;
	private final EatAnalysisService eatAnalysisService;
	private final MedAnalysisService medAnalysisService;
	private final MediEatPdfAssembler mediEatPdfAssembler;
	private final MedicationService medicationService;
	private final DietService dietService;
	private final ChartService chartService;
	private final UserService userService;
	private final DrugInfoService drugInfoService;
	private final GamificationService gamificationService;

	public PdfController(PdfService pdfService, EatAnalysisService eatAnalysisService,
			MedAnalysisService medAnalysisService, MediEatPdfAssembler mediEatPdfAssembler,
			MedicationService medicationService, DietService dietService, ChartService chartService,
			UserService userService, DrugInfoService drugInfoService, GamificationService gamificationService) {
		this.pdfService = pdfService;
		this.eatAnalysisService = eatAnalysisService;
		this.medAnalysisService = medAnalysisService;
		this.mediEatPdfAssembler = mediEatPdfAssembler;
		this.medicationService = medicationService;
		this.dietService = dietService;
		this.chartService = chartService;
		this.userService = userService;
		this.drugInfoService = drugInfoService;
		this.gamificationService = gamificationService;
	}

	// =========================================================
	// ① EAT PDF (이미 해결됨)
	// =========================================================
	@GetMapping("/diet-analysis")
	public void generateEatPdf(@RequestParam Long userId, @RequestParam String startDate, @RequestParam String endDate,
			HttpServletResponse response) {
		try {
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=medeat_eat_report.pdf");

			EatModeAnalysisResponse analysis = eatAnalysisService.analyze(userId, start, end);

			EatPdfRequestDto req = EatPdfRequestDto.fromAnalysis(analysis, startDate, endDate);

			// ================================
			// ✅ 🔥 추천 식단 추가 (여기!!!)
			// ================================
			List<FoodDto> foods = eatAnalysisService.getRecommendedFoods(userId, start, end);

			req.setFoods(foods);

			if (analysis != null && analysis.getPattern() != null) {
				req.setChartSnack(chartService.generateSnackChart(analysis.getPattern()));
			}

			if (analysis != null && analysis.getDailyChart() != null && !analysis.getDailyChart().isEmpty()) {

				req.setChartDaily(chartService.generateDailyChart(analysis.getDailyChart()));
			}

			pdfService.generateDietAnalysisPdf(req, response.getOutputStream(), null);

		} catch (Exception e) {
			log.error("EAT PDF 생성 실패", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/medi-report")
	public void generateMediEatPdf(@RequestParam String startDate, @RequestParam String endDate,
			HttpServletResponse response, HttpSession session) {

		UserDto loginUser = (UserDto) session.getAttribute("loginUser");

		if (loginUser == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Long loginUserId = loginUser.getUserId();

		try {
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=medeat_medi_report.pdf");

			// ==============================
			// 1️⃣ 사용자 정보 조회
			// ==============================
			UserDto user = userService.getUserById(loginUserId);

			String userName = "사용자";
			int userAge = 0;

			if (user != null) {
				if (user.getName() != null && !user.getName().isBlank()) {
					userName = user.getName(); // ✅ 실명 사용
				}
				if (user.getAge() != null) {
					userAge = user.getAge(); // ✅ 나이만 사용
				}
			}

			// ==============================
			// 2️⃣ MEDI 분석
			// ==============================
			MedModeAnalysisResponse medAnalysis = medAnalysisService.analyze(loginUserId, start, end);

			MedDashboard dashboard = medAnalysis != null ? medAnalysis.getDashboard() : null;

			// ==============================
			// 3️⃣ 로그 데이터
			// ==============================
			List<MedicationLogDto> medicationLogs = medicationService.getLogs(loginUserId, startDate, endDate);

			List<DietLogDto> dietLogs = dietService.getLogs(loginUserId, startDate, endDate);

			List<MedicationDto> medications = medicationService.getMedicationList(loginUserId);

			List<Long> itemSeqList = medications.stream().map(MedicationDto::getItemSeq).filter(Objects::nonNull)
					.distinct().toList();

			List<DrugInfoDto> drugInfoList = drugInfoService.getDrugInfoListByItemSeq(itemSeqList);

			// ==============================
			// 4️⃣ PDF DTO 조립
			// ==============================
			MediEatPdfDto pdfDto = mediEatPdfAssembler.assemble(dashboard, medications, medicationLogs, dietLogs,
					userName, String.valueOf(loginUserId), userAge, start, end, drugInfoList);

			// ==============================
			// 5️⃣ PDF 생성
			// ==============================
			pdfService.generateMediEatPdf(pdfDto, response.getOutputStream(), loginUserId);

			gamificationService.earnXp(loginUserId, ActionType.PDF_DOWNLOAD, null, // ✅ 날짜 refId 강제(하루 1회)
					10, LocalDate.now());

		} catch (Exception e) {
			log.error("MEDI_EAT PDF 생성 실패", e);
			response.setStatus(500);
		}
	}

}
