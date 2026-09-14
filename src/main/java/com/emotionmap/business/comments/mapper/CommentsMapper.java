package com.emotionmap.business.comments.mapper;

import com.emotionmap.business.comments.payload.CommentCreateRequest;
import com.emotionmap.business.comments.payload.CommentUpdateRequest;
import com.emotionmap.business.comments.vo.CommentRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentsMapper {

    // 댓글 작성
    void insertComment(CommentCreateRequest request);

    // 게시글의 전체 댓글(대댓글 포함) 평면 조회 - Java에서 트리로 조립
    List<CommentRow> getComments(@Param("postId") Long postId, @Param("userId") Long userId);

    // 소유권/존재 확인
    Long selectCommentUserId(Long commentId);
    Long selectCommentPostId(Long commentId);

    // 수정 / 삭제
    void updateComment(CommentUpdateRequest request);
    void softDeleteComment(Long commentId);
}
