package com.emotionmap.business.posts.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.comments.payload.CommentResponse;
import com.emotionmap.business.comments.service.CommentsService;
import com.emotionmap.business.posts.mapper.PostsMapper;
import com.emotionmap.business.posts.payload.*;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsMapper postsMapper;
    private final UserMapper userMapper;
    private final AnonymousNicknameService anonymousNicknameService;
    private final CommentsService commentsService;

    /**포스트 리스트 조회 - 메인 피드 (overrideLocationId 없으면 내 계정 위치, 있으면 그 지역 - 지도에서 지역 선택 시 사용)*/
    public List<PostListResponse> getPostList(int page, int size, Long userId, Long filterUserId, Long overrideLocationId) {
        Long locationId = overrideLocationId;
        if (locationId == null) {
            UserVo user = userMapper.findById(userId);
            locationId = user != null ? user.getLocationId() : null;
        }
        return fetchPosts(page, size, userId, filterUserId, locationId);
    }

    /**마이페이지 - 개인 감정 통계 (최근 N일간 사용 횟수, 많이 쓴 순)*/
    public List<EmotionStatResponse> getMyEmotionStats(Long userId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return postsMapper.getMyEmotionStats(userId, since);
    }

    /**마이페이지 - 나의 글 리스트 (위치 필터 없음)*/
    public List<PostListResponse> getMyPosts(int page, int size, Long userId) {
        return fetchPosts(page, size, userId, userId, null);
    }

    private List<PostListResponse> fetchPosts(int page, int size, Long userId, Long filterUserId, Long locationId) {
        int offset = (page - 1) * size;

        List<PostListResponse> posts = postsMapper.getPosts(offset, size, userId, filterUserId, locationId);
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIdList = posts.stream()
                .map(PostListResponse::getPostId)
                .toList();

        List<Image> images = postsMapper.getPostsImage(postIdList);
        List<Emotion> emotions = postsMapper.getPostsEmotion(postIdList);

        Map<Long, List<Image>> imageMap = images.stream()
                .collect(Collectors.groupingBy(Image::getPostId));
        Map<Long, List<Emotion>> emotionMap = emotions.stream()
                .collect(Collectors.groupingBy(Emotion::getPostId));

        for (PostListResponse post : posts) {
            post.setImageList(imageMap.getOrDefault(post.getPostId(), Collections.emptyList()));
            post.setEmotionList(emotionMap.getOrDefault(post.getPostId(), Collections.emptyList()));
        }

        return posts;
    }
    /**포스트 상세 조회 - 댓글은 대댓글까지 전부 중첩 트리로 한 번에 포함*/
    public PostDetailResponse getPost(Long postId, Long userId) {
        PostDetailResponse.Post post = postsMapper.getPostDetail(postId, userId);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        List<Image> images = postsMapper.getPostsImage(List.of(postId));
        List<Emotion> emotions = postsMapper.getPostsEmotion(List.of(postId));
        post.setImageList(images);
        post.setEmotionList(emotions);

        List<CommentResponse> comments = commentsService.getCommentTree(postId, userId);

        PostDetailResponse response = new PostDetailResponse();
        response.setPost(post);
        response.setComments(comments);
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
        anonymousNicknameService.getOrCreateNickname(request.getPostId(), userId);
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
            log.warn("[Posts] 수정 권한 없음 - postId: {}, userId: {}, ownerId: {}", postId, userId, ownerId);
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        request.setPostId(postId);
        postsMapper.updatePost(request);

        if (request.getEmotionIds() != null && !request.getEmotionIds().isEmpty()) {
            postsMapper.deletePostEmotionTags(postId);
            postsMapper.insertPostEmotionTags(postId, request.getEmotionIds());
        }
    }

    /**좋아요 토글*/
    public String toggleLike(Long postId, Long userId) {
        Long ownerId = postsMapper.selectPostUserId(postId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        if (postsMapper.existsLike(postId, userId)) {
            postsMapper.deleteLike(postId, userId);
            return "N";
        } else {
            postsMapper.insertLike(postId, userId);
            return "Y";
        }
    }

    /**포스트 삭제*/
    public void delete(Long postId, Long userId) {
        Long ownerId = postsMapper.selectPostUserId(postId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        if (!ownerId.equals(userId)) {
            log.warn("[Posts] 삭제 권한 없음 - postId: {}, userId: {}, ownerId: {}", postId, userId, ownerId);
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        postsMapper.softDeletePost(postId);
    }

}
