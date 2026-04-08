package com.medeat.medical.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.common.web.SessionUserSupport;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.dto.MedicationLogDto;
import com.medeat.medical.service.MedicationService;
import com.medeat.notification.service.WebPushService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medication")
public class MedicationRestController {

    private final MedicationService medicationService;
    private final WebPushService webPushService;
    private final SessionUserSupport sessionUserSupport;
    private final TaskScheduler taskScheduler;

    public MedicationRestController(
            MedicationService medicationService,
            WebPushService webPushService,
            SessionUserSupport sessionUserSupport,
            TaskScheduler taskScheduler
    ) {
        this.medicationService = medicationService;
        this.webPushService = webPushService;
        this.sessionUserSupport = sessionUserSupport;
        this.taskScheduler = taskScheduler;
    }

    @GetMapping("")
    public ResponseEntity<List<MedicationDto>> list(HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(medicationService.getMedicationList(user.getUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationDto> detail(@PathVariable Long id, HttpSession session) {
        sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(medicationService.getMedication(id));
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> create(@RequestBody MedicationDto dto, HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        dto.setUserId(user.getUserId());
        medicationService.addMedication(dto);
        return ResponseEntity.ok(Map.of("message", "복약 정보 등록이 완료되었습니다."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestBody MedicationDto dto,
            HttpSession session
    ) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        dto.setMedicationId(id);
        dto.setUserId(user.getUserId());
        medicationService.updateMedication(dto);
        return ResponseEntity.ok(Map.of("message", "복약 정보 수정이 완료되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, HttpSession session) {
        sessionUserSupport.getRequiredUser(session);
        medicationService.deleteMedication(id);
        return ResponseEntity.ok(Map.of("message", "복약 정보 삭제가 완료되었습니다."));
    }

    @GetMapping("/log/today")
    public ResponseEntity<List<MedicationLogDto>> getTodayLogs(HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(medicationService.getTodayLogs(user.getUserId()));
    }

    @PostMapping("/log")
    public ResponseEntity<Map<String, String>> saveLog(
            @RequestParam Long medicationId,
            @RequestParam int takenIndex,
            HttpSession session
    ) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        medicationService.saveLog(user.getUserId(), medicationId, takenIndex);
        return ResponseEntity.ok(Map.of("message", "복용 기록 저장이 완료되었습니다."));
    }

    @PostMapping("/take")
    public ResponseEntity<Map<String, String>> takeMedication(@RequestBody Map<String, Object> req, HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        Long medicationId = Long.valueOf(req.get("medicationId").toString());
        int doseIndex = Integer.parseInt(req.get("doseIndex").toString());
        medicationService.saveLog(user.getUserId(), medicationId, doseIndex);
        return ResponseEntity.ok(Map.of("message", "복용 완료 처리되었습니다."));
    }

    @PostMapping("/reschedule")
    public ResponseEntity<Map<String, String>> rescheduleAlarm(@RequestBody Map<String, Object> body, HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);

        Long medicationId = Long.valueOf(body.get("medicationId").toString());
        int delayMinutes = Integer.parseInt(body.get("delayMinutes").toString());
        int doseIndex = Integer.parseInt(body.get("doseIndex").toString());

        MedicationDto med = medicationService.getMedication(medicationId);
        if (med == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "복약 정보를 찾을 수 없습니다.");
        }

        taskScheduler.schedule(() -> webPushService.sendMedicationNotification(
                user.getUserId(),
                medicationId,
                doseIndex,
                "복용 알림",
                med.getDrugName() + " 복용 시간입니다."
        ), Instant.now().plusSeconds(delayMinutes * 60L));

        return ResponseEntity.ok(Map.of("message", "알림이 다시 예약되었습니다."));
    }
}
