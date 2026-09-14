package com.emotionmap.business.comments.controller;

import com.emotionmap.business.comments.payload.CommentCreateRequest;
import com.emotionmap.business.comments.payload.CommentUpdateRequest;
import com.emotionmap.business.comments.service.CommentsService;
import com.emotionmap.business.jwt.vo.JwtUser;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "comments", description = "댓글 API (댓글/대댓글 무제한 중첩)")
@RestController
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    @Operation(summary = "댓글/대댓글 작성", description = "parentCommentId가 없으면 게시글에 바로 다는 최상위 댓글, 있으면 그 댓글의 대댓글")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(@AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        Long commentId = commentsService.create(postId, request, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(commentId));
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(@AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long commentId, @RequestBody CommentUpdateRequest request) {
        commentsService.update(commentId, request, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "댓글 삭제", description = "soft delete - 대댓글은 트리에 그대로 남는다")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long commentId) {
        commentsService.delete(commentId, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
