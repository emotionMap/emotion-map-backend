package com.emotionmap.business.posts.controller;

import com.emotionmap.business.jwt.vo.JwtUser;
import com.emotionmap.business.posts.payload.PostCreateRequest;
import com.emotionmap.business.posts.payload.PostDetailResponse;
import com.emotionmap.business.posts.payload.PostListResponse;
import com.emotionmap.business.posts.payload.PostUpdateRequest;
import com.emotionmap.business.posts.service.PostsService;
import com.emotionmap.common.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "posts", description = "포스트 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor


public class PostsController {

    private final PostsService postService;


    @Operation(summary = "포스트 리스트 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getPostList(@AuthenticationPrincipal JwtUser jwtUser
            , @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        List<PostListResponse> response = postService.getPostList(page, size, jwtUser.getUserId(), null);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "포스트 디테일 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@AuthenticationPrincipal JwtUser jwtUser, @PathVariable Long postId) {
        PostDetailResponse response = postService.getPost(postId, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "포스트 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(@AuthenticationPrincipal JwtUser jwtUser, @RequestBody PostCreateRequest request) {
        Long postId = postService.create(request, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(postId));
    }

    @Operation(summary = "포스트 수정")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@AuthenticationPrincipal JwtUser jwtUser, @PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        postService.update(postId, request, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "포스트 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@AuthenticationPrincipal JwtUser jwtUser, @PathVariable Long postId) {
        postService.delete(postId, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "좋아요 토글 (없으면 추가, 있으면 취소)")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> toggleLike(@AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long postId) {
        String likeYN = postService.toggleLike(postId, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(likeYN));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(@AuthenticationPrincipal JwtUser jwtUser,
            @PathVariable Long postId, @RequestBody PostCreateRequest request) {
        Long commentId = postService.createComment(postId, request, jwtUser.getUserId());
        return ResponseEntity.ok(ApiResponse.of(commentId));
    }

    @Operation(summary = "내가 작성한 포스트")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> myPosts(@AuthenticationPrincipal JwtUser jwtUser,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.of(postService.getMyPosts(page, size, jwtUser.getUserId())));
    }

}