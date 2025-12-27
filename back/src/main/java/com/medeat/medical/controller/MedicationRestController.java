package com.medeat.medical.controller;

import java.util.List;
import java.util.Map;

import com.medeat.auth.dto.UserDto;
import com.medeat.medical.dto.MedicationDto;
import com.medeat.medical.dto.MedicationLogDto;
import com.medeat.medical.service.MedicationService;
import com.medeat.notification.service.WebPushService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medication")
public class MedicationRestController {

    @Autowired
    private MedicationService medicationService;
    
    @Autowired
    private WebPushService webPushService;


    /** ****************************************
     * 1. 유저 약 목록 조회
     ******************************************/
    @GetMapping("")
    public ResponseEntity<?> list(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        List<MedicationDto> list = medicationService.getMedicationList(user.getUserId());
        return ResponseEntity.ok(list);
    }
    


    /** ****************************************
     * 2. 단일 약 조회
     ******************************************/
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @PathVariable Long id,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        MedicationDto med = medicationService.getMedication(id);
        return ResponseEntity.ok(med);
    }


    @PostMapping("")
    public ResponseEntity<?> create(
            @RequestBody MedicationDto dto,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        dto.setUserId(user.getUserId());

        try {
            medicationService.addMedication(dto);
            return ResponseEntity.ok("약 등록 완료");

        } catch (IllegalArgumentException e) {
            // 🔥 중복일 경우 여기로 들어옴
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    /** ****************************************
     * 4. 수정
     ******************************************/
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody MedicationDto dto,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        dto.setMedicationId(id);
        dto.setUserId(user.getUserId());

        try {
            medicationService.updateMedication(dto);
            return ResponseEntity.ok("약 수정 완료");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    /** ****************************************
     * 5. 삭제
     ******************************************/
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        medicationService.deleteMedication(id);

        return ResponseEntity.ok("약 삭제 완료");
    }
    
    /** 오늘 복용한 기록 조회 */
    @GetMapping("/log/today")
    public ResponseEntity<?> getTodayLogs(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        List<MedicationLogDto> logs = medicationService.getTodayLogs(user.getUserId());
        return ResponseEntity.ok(logs);
    }

    /** 복용 기록 저장 */
    @PostMapping("/log")
    public ResponseEntity<?> saveLog(
            @RequestParam Long medicationId,
            @RequestParam int takenIndex,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        medicationService.saveLog(
                user.getUserId(),
                medicationId,
                takenIndex
        );

        return ResponseEntity.ok(Map.of("message", "복용 기록 저장 완료"));
    }
    
    /** ****************************************
     * 6. 약 복용 완료 처리 (푸시 알림 버튼)
     ******************************************/
    @PostMapping("/take")
    public ResponseEntity<?> takeMedication(@RequestBody Map<String, Object> req,
                                            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        Long medicationId = Long.valueOf(req.get("medicationId").toString());
        int doseIndex = Integer.parseInt(req.get("doseIndex").toString());

        // 기록 저장
        medicationService.saveLog(user.getUserId(), medicationId, doseIndex);

        return ResponseEntity.ok("복용 완료 처리됨");
    }

    /** ****************************************
     * 7. 알림 재설정 (delayMinutes 후 다시 알림)
     ******************************************/
    @PostMapping("/reschedule")
    public ResponseEntity<?> rescheduleAlarm(@RequestBody Map<String, Object> body,
                                             HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body("로그인 필요");

        Long medicationId = Long.valueOf(body.get("medicationId").toString());
        int delayMinutes = Integer.parseInt(body.get("delayMinutes").toString());
        int doseIndex = Integer.parseInt(body.get("doseIndex").toString());

        MedicationDto med = medicationService.getMedication(medicationId);
        if (med == null) return ResponseEntity.status(404).body("약 정보를 찾을 수 없습니다.");

        // 🔥 delayMinutes 뒤 알림 재발송 (Thread 사용)
        new Thread(() -> {
            try {
                Thread.sleep(delayMinutes * 60 * 1000);

                String title = "🔔 약 복용 재알림";
                String bodyMsg = med.getDrugName() + " 복용 시간입니다.";

                webPushService.sendMedicationNotification(
                        user.getUserId(),
                        medicationId,
                        doseIndex,
                        title,
                        bodyMsg
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok("재알림 예약됨");
    }

}
