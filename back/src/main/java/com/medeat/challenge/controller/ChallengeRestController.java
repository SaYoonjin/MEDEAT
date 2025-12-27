package com.medeat.challenge.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.medeat.challenge.dao.UserChallengeDao;
import com.medeat.challenge.dto.ChallengeDto;
import com.medeat.challenge.dto.ChallengeLogDto;
import com.medeat.challenge.dto.UserChallengeDto;
import com.medeat.auth.dto.UserDto;
import com.medeat.auth.service.UserService;
import com.medeat.challenge.service.ChallengeService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/challenge")
public class ChallengeRestController {

    @Autowired
    private ChallengeService challengeService;
    
    @Autowired
    private UserChallengeDao userChallengeDao;

    @Autowired
    private UserService authService; // or 세션 유저 가져오는 서비스

    
    @GetMapping("/{challengeId}/me")
    public UserChallengeDto getMyChallengeStatus(
            @PathVariable Long challengeId,
            HttpSession session
    ) {
    	UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "로그인이 필요합니다."
            );
        }
        
        Long userId = ((UserDto) session.getAttribute("loginUser")).getUserId();


        UserChallengeDto uc =
            userChallengeDao.selectByUserAndChallenge(userId, challengeId);

        if (uc == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "참여 정보 없음"
            );
        }

        return uc;
    }


    // 1. 챌린지 리스트
    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam(defaultValue = "EAT") String mode,
                                  HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        Long userId = user.getUserId();

        Map<String, Object> result = new HashMap<>();
        result.put("popular", challengeService.getPopularChallenges(mode));
        result.put("ongoing", challengeService.getOngoingChallenges(userId, mode));
        result.put("available", challengeService.getAvailableChallenges(userId, mode));

        return ResponseEntity.ok(result);
    }

    // 1-1. 챌린지 기본 정보 조회
    @GetMapping("/detail/{challengeId}")
    public ResponseEntity<?> challengeInfo(@PathVariable Long challengeId) {
        ChallengeDto challenge = challengeService.getChallengeDetail(challengeId);
        if (challenge == null) {
            return ResponseEntity.status(404).body(Map.of("message", "챌린지가 존재하지 않습니다."));
        }
        return ResponseEntity.ok(challenge);
    }

    // 2. 챌린지 상세 조회 (user_challenge 기준)
    @GetMapping("/{userChallengeId}")
    public ResponseEntity<?> detail(@PathVariable Long userChallengeId,
                                    @RequestParam(defaultValue = "EAT") String mode) {

        UserChallengeDto uc = challengeService.getUserChallenge(userChallengeId);
        if (uc == null) {
            return ResponseEntity.status(404).body(Map.of("message", "챌린지 기록 없음"));
        }

        ChallengeDto challenge = challengeService.getChallengeDetail(uc.getChallengeId());
        List<ChallengeLogDto> logs = challengeService.getLogs(userChallengeId);

        Map<String, Object> result = new HashMap<>();
        result.put("userChallenge", uc);
        result.put("challenge", challenge);
        result.put("logs", logs);

        return ResponseEntity.ok(result);
    }

    // 3. 챌린지 생성
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody ChallengeDto dto, HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        dto.setUserId(user.getUserId());

        if (dto.getMaxParticipants() == null) dto.setMaxParticipants(10);
        dto.setCurrentParticipants(0); // <- 앞으로 표시용으로만 쓰거나, 아예 안 쓰는 걸 추천

        challengeService.createChallenge(dto);
        return ResponseEntity.ok(Map.of("message", "챌린지 생성 완료"));
    }

    // 4. 챌린지 참여
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestParam Long challengeId, HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        try {
            // ✅ 서비스가 정원/쿨타임/이미참여중 처리 후 결과를 반환하도록 만들 예정
            Map<String, Object> res = challengeService.joinChallenge(user.getUserId(), challengeId);
            return ResponseEntity.ok(res);

        } catch (IllegalStateException e) {
            // ✅ 정원 마감, 쿨타임 등 “상태 충돌”은 409로
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 5. 챌린지 포기
    @PostMapping("/giveup")
    public ResponseEntity<?> giveUp(
            @RequestParam Long userChallengeId,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "로그인 필요"));
        }

        challengeService.giveUpChallenge(user.getUserId(), userChallengeId);

        return ResponseEntity.ok(Map.of("message", "챌린지 포기 완료"));
    }

    // 6. 챌린지 로그 추가
    @PostMapping("/log")
    public ResponseEntity<?> addLog(@RequestParam Long userChallengeId,
                                    @RequestParam String status,
                                    @RequestParam String memo,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate,
                                    HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // ✅ 미래 날짜 금지(오늘 포함 과거만)
        LocalDate today = LocalDate.now();
        if (logDate.isAfter(today)) {
            return ResponseEntity.badRequest().body("미래 날짜에는 로그를 등록할 수 없습니다.");
        }

        try {
            ChallengeLogDto log = new ChallengeLogDto();
            log.setUserChallengeId(userChallengeId);
            log.setStatus(status);
            log.setMemo(memo);
            log.setLogDate(logDate);

            challengeService.addLog(user.getUserId(), log);
            return ResponseEntity.ok("success");

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("이미 등록된 로그가 있는 날짜입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 8. 챌린지 로그 삭제
    @PostMapping("/log/delete")
    public ResponseEntity<?> deleteLog(@RequestParam Long challengeLogId, HttpSession session) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        ChallengeLogDto log = challengeService.getLogById(challengeLogId);
        if (log == null) return ResponseEntity.status(404).body("로그가 존재하지 않습니다.");

        UserChallengeDto uc = challengeService.getUserChallenge(log.getUserChallengeId());
        if (uc == null) return ResponseEntity.status(404).body("챌린지 참여 정보가 없습니다.");

        if (!uc.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(403).body("본인 글만 삭제할 수 있습니다.");
        }

        challengeService.deleteLog(challengeLogId);
        return ResponseEntity.ok(Map.of("message", "로그 삭제 완료"));
    }

    // 9. 챌린지 삭제 (작성자만)
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long challengeId, HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));

        boolean result = challengeService.deleteChallenge(challengeId, user.getUserId());
        if (!result) {
            return ResponseEntity.status(403).body(Map.of("message", "작성자만 삭제할 수 있습니다."));
        }
        return ResponseEntity.ok(Map.of("message", "챌린지가 삭제되었습니다."));
    }
}
