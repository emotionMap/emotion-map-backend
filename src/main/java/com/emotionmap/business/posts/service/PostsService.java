package com.emotionmap.business.posts.service;

import com.emotionmap.business.posts.payload.PostCreateRequest;
import com.emotionmap.business.posts.payload.PostDetailResponse;
import com.emotionmap.business.posts.payload.PostListResponse;
import com.emotionmap.business.posts.payload.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsService {

    public PostListResponse getPostList(int page, int size) {
        return null;
    }

    public PostDetailResponse getPost(Long postId) {
        return null;
    }

    public void create(PostCreateRequest request) {
    }

    public void update(Long postId, PostUpdateRequest request) {
    }

    public void delete(Long postId) {
    }
}
