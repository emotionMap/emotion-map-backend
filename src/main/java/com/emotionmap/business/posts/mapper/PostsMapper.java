package com.emotionmap.business.posts.mapper;

import com.emotionmap.business.posts.payload.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostsMapper {
    // 포스트 리스트
    List<PostListResponse> getPosts(@Param("offset") int offset, @Param("size") int size,
                                    @Param("userId") Long userId, @Param("filterUserId") Long filterUserId,
                                    @Param("locationId") Long locationId);
    List<Image> getPostsImage(List<Long> postIdList);
    List<Emotion> getPostsEmotion(List<Long> postIdList);

    // 포스트 상세
    PostDetailResponse.Post getPostDetail(Long postId, Long userId);
    List<PostDetailResponse.Post> getPostChildren(Long postId, Long userId);

    // 포스트 생성
    void insertPost(PostCreateRequest request);
    void insertPostEmotionTags(@Param("postId") Long postId, @Param("emotionIds") List<Long> emotionIds);

    // 포스트 수정
    Long selectPostUserId(Long postId);
    void updatePost(PostUpdateRequest request);
    void deletePostEmotionTags(Long postId);

    // 포스트 삭제
    void softDeletePost(Long postId);
}
