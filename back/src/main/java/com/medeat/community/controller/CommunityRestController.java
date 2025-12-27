package com.medeat.community.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.community.dto.CommentDto;
import com.medeat.community.dto.PostDto;
import com.medeat.community.service.CommunityService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.service.GamificationService;

import java.time.LocalDate;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/community")
public class CommunityRestController {

    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private GamificationService gamificationService;


    /** ****************************************
     * 1. 게시글 목록
     ******************************************/
    @GetMapping("")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "EAT") String mode,
            @RequestParam(required = false) String keyword
    ) {
        List<PostDto> posts = communityService.getPostList(mode, keyword);
        return ResponseEntity.ok(posts);
    }

    /** ****************************************
     * 2. 게시글 상세 조회 (게시글 + 댓글)
     ******************************************/
    @GetMapping("/{postId}")
    public ResponseEntity<?> detail(
            @PathVariable Long postId,
            HttpSession session) {

        PostDto post = communityService.getPost(postId);
        if (post == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "게시글을 찾을 수 없습니다."));
        }

        List<CommentDto> comments = communityService.getCommentList(postId);

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        Long loginUserId = (loginUser != null) ? loginUser.getUserId() : null;

        Map<String, Object> result = new HashMap<>();
        result.put("post", post);
        result.put("comments", comments);
        result.put("loginUserId", loginUserId);

        return ResponseEntity.ok(result);
    }


    /** ****************************************
     * 3. 게시글 작성
     ******************************************/
    @PostMapping("")
    public ResponseEntity<?> write(
            @RequestPart("dto") PostDto dto,
            @RequestPart(value = "uploadFile", required = false) MultipartFile file,
            HttpSession session) throws Exception {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        dto.setUserId(user.getUserId());

        // 파일 업로드
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String savePath = "C:/medeat/upload/" + fileName;

            file.transferTo(new File(savePath));
            dto.setImagePath(fileName);
        }

        communityService.writePost(dto);
        
        gamificationService.earnXp(
        	    user.getUserId(),
        	    ActionType.POST_CREATE,
        	    null,                 // ✅ 날짜 refId 강제(하루 1회)
        	    5,
        	    LocalDate.now()
        	);

        return ResponseEntity.ok(Map.of("message", "게시글 작성 완료"));
    }


    /** ****************************************
     * 4. 게시글 수정
     ******************************************/
    @PutMapping("/{postId}")
    public ResponseEntity<?> update(
            @PathVariable Long postId,
            @RequestPart("dto") PostDto dto,
            @RequestPart(value = "uploadFile", required = false) MultipartFile file,
            HttpSession session) throws Exception {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        PostDto origin = communityService.getPost(postId);
        if (!origin.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("message", "수정 권한 없음"));
        }

        // 파일 업로드
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String savePath = "C:/medeat/upload/" + fileName;

            file.transferTo(new File(savePath));
            dto.setImagePath(fileName);
        }

        dto.setPostId(postId);
        communityService.updatePost(dto);

        return ResponseEntity.ok(Map.of("message", "게시글 수정 완료"));
    }


    /** ****************************************
     * 5. 게시글 삭제
     ******************************************/
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> delete(
            @PathVariable Long postId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        PostDto post = communityService.getPost(postId);
        if (!post.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("message", "삭제 권한 없음"));
        }

        communityService.deletePost(postId);

        return ResponseEntity.ok(Map.of("message", "게시글 삭제 완료"));
    }


    /** ****************************************
     * 6. 댓글 작성
     ******************************************/
    @PostMapping("/comment")
    public ResponseEntity<?> comment(
            @RequestBody CommentDto dto,
            HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        dto.setUserId(user.getUserId());
        communityService.writeComment(dto);
        
        Long commentId = dto.getCommentId(); // insert 후 채워진다고 가정
        String refId = (commentId != null) ? ("comment:" + commentId) : ("comment:post:" + dto.getPostId() + ":" + System.currentTimeMillis());

        gamificationService.earnXp(
            user.getUserId(),
            ActionType.COMMENT_CREATE,
            refId,
            2,
            LocalDate.now()
        );

        return ResponseEntity.ok(Map.of("message", "댓글 작성 완료"));
    }


    /** ****************************************
     * 7. 댓글 삭제
     ******************************************/
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        CommentDto comment = communityService.getComment(commentId);
        if (comment == null) {
            return ResponseEntity.status(404).body(Map.of("message", "댓글 없음"));
        }

        // 댓글이 달린 게시글 정보 가져오기
        PostDto post = communityService.getPost(comment.getPostId());
        if (post == null) {
            return ResponseEntity.status(404).body(Map.of("message", "게시글 없음"));
        }

        Long loginId = loginUser.getUserId();
        boolean isCommentWriter = loginId.equals(comment.getUserId());
        boolean isPostWriter = loginId.equals(post.getUserId());

        // 🔥 댓글 작성자 또는 게시글 작성자만 삭제 가능
        if (!isCommentWriter && !isPostWriter) {
            return ResponseEntity.status(403).body(Map.of("message", "삭제 권한 없음"));
        }

        communityService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("message", "댓글 삭제 완료"));
    }



    /** ****************************************
     * 8. 댓글 수정
     ******************************************/
    @PutMapping("/comment")
    public ResponseEntity<?> updateComment(
            @RequestBody CommentDto dto,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        CommentDto origin = communityService.getComment(dto.getCommentId());
        if (!origin.getUserId().equals(loginUser.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("message", "수정 권한 없음"));
        }

        communityService.updateComment(dto);

        return ResponseEntity.ok(Map.of("message", "댓글 수정 완료"));
    }


    /** ****************************************
     * 9. 좋아요 토글
     ******************************************/
    @PostMapping("/like")
    public ResponseEntity<?> like(
            @RequestParam Long postId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        boolean result = communityService.toggleLike(postId, loginUser.getUserId());

        return ResponseEntity.ok(Map.of("liked", result));
    }


    /** ****************************************
     * 10. 스크랩 토글
     ******************************************/
    @PostMapping("/scrap")
    public ResponseEntity<?> scrap(
            @RequestParam Long postId,
            HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 필요"));
        }

        boolean result = communityService.toggleScrap(postId, loginUser.getUserId());

        return ResponseEntity.ok(Map.of("scrapped", result));
    }
    
    /** ****************************************
     * 11. 인기 게시글 1개 조회
     ******************************************/
    @GetMapping("/top")
    public ResponseEntity<?> getTopPost() {
        PostDto post = communityService.getTopPost();
        if (post == null) {
            return ResponseEntity.ok().body(null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("title", post.getTitle());
        result.put("writer", post.getWriterId());
        result.put("likes", post.getLikeCount());
        result.put("scraps", post.getScrapCount());

        return ResponseEntity.ok(result);
    }

}
