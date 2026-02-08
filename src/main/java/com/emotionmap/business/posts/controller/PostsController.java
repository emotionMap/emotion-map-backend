package com.emotionmap.business.posts.controller;

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
import org.springframework.web.bind.annotation.*;

@Tag(name = "posts", description = "포스트 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor


public class PostsController {

    private final PostsService postService;

    @Operation(summary = "포스트 리스트 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> getPostList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        PostListResponse response = postService.getPostList(page, size);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "포스트 디테일 조회")
    @GetMapping("/detail/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@PathVariable Long postId) {
        PostDetailResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "포스트 생성")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createPost(@RequestBody PostCreateRequest request) {
        postService.create(request);
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "포스트 수정")
    @PostMapping("/update/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        postService.update(postId, request);
        return ResponseEntity.ok(ApiResponse.of(null));
    }

    @Operation(summary = "포스트 삭제")
    @PostMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ApiResponse.of(null));
    }

}
