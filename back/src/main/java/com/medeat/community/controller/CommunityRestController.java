package com.medeat.community.controller;

import com.medeat.auth.dto.UserDto;
import com.medeat.common.file.FileStorageService;
import com.medeat.common.web.SessionUserSupport;
import com.medeat.community.dto.CommentDto;
import com.medeat.community.dto.PostDto;
import com.medeat.community.service.CommunityService;
import com.medeat.gamification.model.ActionType;
import com.medeat.gamification.service.GamificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
public class CommunityRestController {

    private final CommunityService communityService;
    private final GamificationService gamificationService;
    private final SessionUserSupport sessionUserSupport;
    private final FileStorageService fileStorageService;

    public CommunityRestController(
            CommunityService communityService,
            GamificationService gamificationService,
            SessionUserSupport sessionUserSupport,
            FileStorageService fileStorageService
    ) {
        this.communityService = communityService;
        this.gamificationService = gamificationService;
        this.sessionUserSupport = sessionUserSupport;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("")
    public ResponseEntity<List<PostDto>> list(
            @RequestParam(defaultValue = "EAT") String mode,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(communityService.getPostList(mode, keyword));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long postId, HttpSession session) {
        PostDto post = requirePost(postId);
        List<CommentDto> comments = communityService.getCommentList(postId);
        UserDto loginUser = sessionUserSupport.getOptionalUser(session);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("post", post);
        result.put("comments", comments);
        result.put("loginUserId", loginUser != null ? loginUser.getUserId() : null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> write(
            @RequestPart("dto") PostDto dto,
            @RequestPart(value = "uploadFile", required = false) MultipartFile file,
            HttpSession session
    ) throws IOException {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        dto.setUserId(user.getUserId());

        String fileName = fileStorageService.store(file, "");
        if (fileName != null) {
            dto.setImagePath(fileName);
        }

        communityService.writePost(dto);
        gamificationService.earnXp(user.getUserId(), ActionType.POST_CREATE, null, 5, LocalDate.now());

        return ResponseEntity.ok(Map.of("message", "게시글 작성이 완료되었습니다."));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long postId,
            @RequestPart("dto") PostDto dto,
            @RequestPart(value = "uploadFile", required = false) MultipartFile file,
            HttpSession session
    ) throws IOException {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        PostDto origin = requirePost(postId);
        validateOwner(origin.getUserId(), loginUser.getUserId(), "게시글 수정 권한이 없습니다.");

        String fileName = fileStorageService.store(file, "");
        if (fileName != null) {
            dto.setImagePath(fileName);
        }

        dto.setPostId(postId);
        dto.setUserId(loginUser.getUserId());
        communityService.updatePost(dto);

        return ResponseEntity.ok(Map.of("message", "게시글 수정이 완료되었습니다."));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long postId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        PostDto post = requirePost(postId);
        validateOwner(post.getUserId(), loginUser.getUserId(), "게시글 삭제 권한이 없습니다.");

        communityService.deletePost(postId);
        return ResponseEntity.ok(Map.of("message", "게시글 삭제가 완료되었습니다."));
    }

    @PostMapping("/comment")
    public ResponseEntity<Map<String, String>> comment(@RequestBody CommentDto dto, HttpSession session) {
        UserDto user = sessionUserSupport.getRequiredUser(session);
        dto.setUserId(user.getUserId());
        communityService.writeComment(dto);

        Long commentId = dto.getCommentId();
        String refId = (commentId != null)
                ? "comment:" + commentId
                : "comment:post:" + dto.getPostId() + ":" + System.currentTimeMillis();

        gamificationService.earnXp(user.getUserId(), ActionType.COMMENT_CREATE, refId, 2, LocalDate.now());
        return ResponseEntity.ok(Map.of("message", "댓글 작성이 완료되었습니다."));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        CommentDto comment = requireComment(commentId);
        PostDto post = requirePost(comment.getPostId());

        Long loginUserId = loginUser.getUserId();
        boolean isCommentWriter = loginUserId.equals(comment.getUserId());
        boolean isPostWriter = loginUserId.equals(post.getUserId());
        if (!isCommentWriter && !isPostWriter) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }

        communityService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("message", "댓글 삭제가 완료되었습니다."));
    }

    @PutMapping("/comment")
    public ResponseEntity<Map<String, String>> updateComment(@RequestBody CommentDto dto, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        CommentDto origin = requireComment(dto.getCommentId());
        validateOwner(origin.getUserId(), loginUser.getUserId(), "댓글 수정 권한이 없습니다.");

        communityService.updateComment(dto);
        return ResponseEntity.ok(Map.of("message", "댓글 수정이 완료되었습니다."));
    }

    @PostMapping("/like")
    public ResponseEntity<Map<String, Boolean>> like(@RequestParam Long postId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(Map.of("liked", communityService.toggleLike(postId, loginUser.getUserId())));
    }

    @PostMapping("/scrap")
    public ResponseEntity<Map<String, Boolean>> scrap(@RequestParam Long postId, HttpSession session) {
        UserDto loginUser = sessionUserSupport.getRequiredUser(session);
        return ResponseEntity.ok(Map.of("scrapped", communityService.toggleScrap(postId, loginUser.getUserId())));
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopPost() {
        PostDto post = communityService.getTopPost();
        if (post == null) {
            return ResponseEntity.ok().body(null);
        }

        return ResponseEntity.ok(Map.of(
                "title", post.getTitle(),
                "writer", post.getWriterId(),
                "likes", post.getLikeCount(),
                "scraps", post.getScrapCount()
        ));
    }

    private PostDto requirePost(Long postId) {
        PostDto post = communityService.getPost(postId);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return post;
    }

    private CommentDto requireComment(Long commentId) {
        CommentDto comment = communityService.getComment(commentId);
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");
        }
        return comment;
    }

    private void validateOwner(Long ownerId, Long loginUserId, String message) {
        if (!ownerId.equals(loginUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }
}
