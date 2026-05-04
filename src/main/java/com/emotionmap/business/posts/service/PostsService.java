package com.emotionmap.business.posts.service;

import com.emotionmap.business.posts.mapper.PostsMapper;
import com.emotionmap.business.posts.payload.*;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsMapper postsMapper;

    /**포스트 리스트 조회*/
    public List<PostListResponse> getPostList(int page, int size, Long userId, Long filterUserId) {

        int offset = (page - 1) * size;

        // 포스트 조회
        List<PostListResponse> posts = postsMapper.getPosts(offset, size, userId, filterUserId);
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        // 포스트 ID 조회
        List<Long> postIdList = posts.stream()
                .map(PostListResponse::getPostId)
                .toList();

        // 이미지 조회
        List<Image> images = postsMapper.getPostsImage(postIdList);
        // 감정 조회
        List<Emotion> emotions = postsMapper.getPostsEmotion(postIdList);

        // postId 기준 그룹핑
        Map<Long, List<Image>> imageMap = images.stream()
                .collect(Collectors.groupingBy(Image::getPostId));

        Map<Long, List<Emotion>> emotionMap = emotions.stream()
                .collect(Collectors.groupingBy(Emotion::getPostId));

        // 조립
        for (PostListResponse post : posts) {
            post.setImageList(
                    imageMap.getOrDefault(post.getPostId(), Collections.emptyList())
            );
            post.setEmotionList(
                    emotionMap.getOrDefault(post.getPostId(), Collections.emptyList())
            );
        }

        return posts;
    }
    /**포스트 상세 조회*/
    public PostDetailResponse getPost(Long postId, Long userId) {
        PostDetailResponse.Post post = postsMapper.getPostDetail(postId, userId);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        List<Image> images = postsMapper.getPostsImage(List.of(postId));
        List<Emotion> emotions = postsMapper.getPostsEmotion(List.of(postId));
        post.setImageList(images);
        post.setEmotionList(emotions);

        List<PostDetailResponse.Post> children = postsMapper.getPostChildren(postId, userId);
        if (!children.isEmpty()) {
            List<Long> childIdList = children.stream()
                    .map(PostDetailResponse.Post::getPostId)
                    .toList();

            Map<Long, List<Image>> imageMap = postsMapper.getPostsImage(childIdList).stream()
                    .collect(Collectors.groupingBy(Image::getPostId));
            Map<Long, List<Emotion>> emotionMap = postsMapper.getPostsEmotion(childIdList).stream()
                    .collect(Collectors.groupingBy(Emotion::getPostId));

            for (PostDetailResponse.Post child : children) {
                child.setImageList(imageMap.getOrDefault(child.getPostId(), Collections.emptyList()));
                child.setEmotionList(emotionMap.getOrDefault(child.getPostId(), Collections.emptyList()));
            }
        }

        PostDetailResponse response = new PostDetailResponse();
        response.setPost(post);
        response.setChildren(children);
        return response;
    }

    /**포스트 생성*/
    @Transactional
    public Long create(PostCreateRequest request, Long userId) {
        if (request.getLocationId() == null || request.getEmotionIds() == null || request.getEmotionIds().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_POST_REQUEST);
        }
        request.setUserId(userId);
        postsMapper.insertPost(request);
        postsMapper.insertPostEmotionTags(request.getPostId(), request.getEmotionIds());
        return request.getPostId();
    }

    /**포스트 수정*/
    @Transactional
    public void update(Long postId, PostUpdateRequest request, Long userId) {
        Long ownerId = postsMapper.selectPostUserId(postId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        if (!ownerId.equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        request.setPostId(postId);
        postsMapper.updatePost(request);

        if (request.getEmotionIds() != null && !request.getEmotionIds().isEmpty()) {
            postsMapper.deletePostEmotionTags(postId);
            postsMapper.insertPostEmotionTags(postId, request.getEmotionIds());
        }
    }

    /**포스트 삭제*/
    @Transactional
    public void delete(Long postId, Long userId) {
        Long ownerId = postsMapper.selectPostUserId(postId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        if (!ownerId.equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        postsMapper.softDeletePost(postId);
    }

    /**마이페이지 - 나의 글 리스트*/
    public List<PostListResponse> getMyPosts(int page, int size, Long userId) {
        return getPostList(page, size, userId, userId);
    }
}
