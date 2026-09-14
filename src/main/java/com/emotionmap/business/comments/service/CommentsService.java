package com.emotionmap.business.comments.service;

import com.emotionmap.business.comments.mapper.CommentsMapper;
import com.emotionmap.business.comments.payload.CommentCreateRequest;
import com.emotionmap.business.comments.payload.CommentResponse;
import com.emotionmap.business.comments.payload.CommentUpdateRequest;
import com.emotionmap.business.comments.vo.CommentRow;
import com.emotionmap.business.posts.service.AnonymousNicknameService;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsMapper commentsMapper;
    private final AnonymousNicknameService anonymousNicknameService;

    /**댓글/대댓글 작성 (무제한 중첩) - parentCommentId가 없으면 게시글에 바로 다는 최상위 댓글*/
    @Transactional
    public Long create(Long postId, CommentCreateRequest request, Long userId) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_COMMENT_REQUEST);
        }

        Long parentCommentId = request.getParentCommentId();
        if (parentCommentId != null) {
            Long parentPostId = commentsMapper.selectCommentPostId(parentCommentId);
            if (parentPostId == null || !parentPostId.equals(postId)) {
                throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
            }
        }

        request.setPostId(postId);
        request.setUserId(userId);
        commentsMapper.insertComment(request);

        // 게시글 자체든 몇 단계 대댓글이든, 이 게시글 안에서는 항상 같은 닉네임
        anonymousNicknameService.getOrCreateNickname(postId, userId);

        return request.getCommentId();
    }

    /**댓글 수정*/
    public void update(Long commentId, CommentUpdateRequest request, Long userId) {
        Long ownerId = commentsMapper.selectCommentUserId(commentId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!ownerId.equals(userId)) {
            log.warn("[Comments] 수정 권한 없음 - commentId: {}, userId: {}, ownerId: {}", commentId, userId, ownerId);
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_COMMENT_REQUEST);
        }

        request.setCommentId(commentId);
        commentsMapper.updateComment(request);
    }

    /**댓글 삭제 (soft delete) - 대댓글은 그대로 트리에 남는다*/
    public void delete(Long commentId, Long userId) {
        Long ownerId = commentsMapper.selectCommentUserId(commentId);
        if (ownerId == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!ownerId.equals(userId)) {
            log.warn("[Comments] 삭제 권한 없음 - commentId: {}, userId: {}, ownerId: {}", commentId, userId, ownerId);
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        commentsMapper.softDeleteComment(commentId);
    }

    /**게시글의 전체 댓글을 한 번에 중첩 트리로 조립 (부모가 항상 자식보다 먼저 생성되므로 2-pass로 충분)*/
    public List<CommentResponse> getCommentTree(Long postId, Long userId) {
        List<CommentRow> rows = commentsMapper.getComments(postId, userId);
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, CommentResponse> byId = new HashMap<>();
        List<CommentResponse> roots = new ArrayList<>();

        for (CommentRow row : rows) {
            CommentResponse node = new CommentResponse();
            node.setCommentId(row.getCommentId());
            node.setNickname(row.getNickname());
            node.setContent(row.getContent());
            node.setCreatedAt(row.getCreatedAt());
            node.setIsMine(row.getIsMine());
            node.setStatus(row.getStatus());
            node.setChildren(new ArrayList<>());
            byId.put(row.getCommentId(), node);
        }

        for (CommentRow row : rows) {
            CommentResponse node = byId.get(row.getCommentId());
            if (row.getParentCommentId() == null) {
                roots.add(node);
            } else {
                CommentResponse parent = byId.get(row.getParentCommentId());
                parent.getChildren().add(node);
            }
        }

        return roots;
    }
}
