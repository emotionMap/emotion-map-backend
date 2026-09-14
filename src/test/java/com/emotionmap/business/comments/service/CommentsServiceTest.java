package com.emotionmap.business.comments.service;

import com.emotionmap.business.comments.mapper.CommentsMapper;
import com.emotionmap.business.comments.payload.CommentCreateRequest;
import com.emotionmap.business.comments.payload.CommentResponse;
import com.emotionmap.business.comments.payload.CommentUpdateRequest;
import com.emotionmap.business.comments.vo.CommentRow;
import com.emotionmap.business.posts.payload.Status;
import com.emotionmap.business.posts.service.AnonymousNicknameService;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentsServiceTest {

    @Mock
    private CommentsMapper commentsMapper;
    @Mock
    private AnonymousNicknameService anonymousNicknameService;

    @InjectMocks
    private CommentsService commentsService;

    @Test
    void create_내용이_비어있으면_INVALID_COMMENT_REQUEST() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("   ");

        assertThatThrownBy(() -> commentsService.create(1L, request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_COMMENT_REQUEST);

        verifyNoInteractions(commentsMapper);
    }

    @Test
    void create_parentCommentId가_다른_게시글_소속이면_COMMENT_NOT_FOUND() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("대댓글");
        request.setParentCommentId(5L);
        when(commentsMapper.selectCommentPostId(5L)).thenReturn(999L); // 요청한 postId(1L)와 다름

        assertThatThrownBy(() -> commentsService.create(1L, request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND);

        verify(commentsMapper, never()).insertComment(any());
    }

    @Test
    void create_최상위_댓글이면_postId_기준으로_닉네임을_배정한다() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("댓글");
        doAnswer(inv -> {
            request.setCommentId(42L);
            return null;
        }).when(commentsMapper).insertComment(request);

        Long commentId = commentsService.create(1L, request, 1L);

        assertThat(commentId).isEqualTo(42L);
        assertThat(request.getPostId()).isEqualTo(1L);
        verify(anonymousNicknameService).getOrCreateNickname(1L, 1L);
    }

    @Test
    void update_존재하지_않으면_COMMENT_NOT_FOUND() {
        when(commentsMapper.selectCommentUserId(1L)).thenReturn(null);

        assertThatThrownBy(() -> commentsService.update(1L, new CommentUpdateRequest(), 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    void update_소유자가_아니면_FORBIDDEN() {
        when(commentsMapper.selectCommentUserId(1L)).thenReturn(2L);

        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setContent("수정");

        assertThatThrownBy(() -> commentsService.update(1L, request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);

        verify(commentsMapper, never()).updateComment(any());
    }

    @Test
    void delete_소유자가_아니면_FORBIDDEN() {
        when(commentsMapper.selectCommentUserId(1L)).thenReturn(2L);

        assertThatThrownBy(() -> commentsService.delete(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);

        verify(commentsMapper, never()).softDeleteComment(any());
    }

    @Test
    void getCommentTree_댓글_대댓글_대대댓글이_중첩트리로_조립되고_삭제된_댓글의_자식도_유지된다() {
        // 1(최상위, DELETED) -> 2(대댓글) -> 3(대대댓글)
        // 4(별도 최상위 댓글)
        CommentRow root = row(1L, null, "삭제된댓글", Status.DELETED);
        CommentRow reply = row(2L, 1L, "대댓글", Status.ACTIVE);
        CommentRow replyOfReply = row(3L, 2L, "대대댓글", Status.ACTIVE);
        CommentRow anotherRoot = row(4L, null, "다른 최상위 댓글", Status.ACTIVE);

        when(commentsMapper.getComments(1L, 1L)).thenReturn(List.of(root, reply, replyOfReply, anotherRoot));

        List<CommentResponse> tree = commentsService.getCommentTree(1L, 1L);

        assertThat(tree).hasSize(2);

        CommentResponse rootNode = tree.get(0);
        assertThat(rootNode.getCommentId()).isEqualTo(1L);
        assertThat(rootNode.getStatus()).isEqualTo(Status.DELETED);
        assertThat(rootNode.getChildren()).hasSize(1);

        CommentResponse replyNode = rootNode.getChildren().get(0);
        assertThat(replyNode.getCommentId()).isEqualTo(2L);
        assertThat(replyNode.getChildren()).hasSize(1);
        assertThat(replyNode.getChildren().get(0).getCommentId()).isEqualTo(3L);
        assertThat(replyNode.getChildren().get(0).getChildren()).isEmpty();

        assertThat(tree.get(1).getCommentId()).isEqualTo(4L);
        assertThat(tree.get(1).getChildren()).isEmpty();
    }

    @Test
    void getCommentTree_댓글이_없으면_빈_리스트를_반환한다() {
        when(commentsMapper.getComments(1L, 1L)).thenReturn(List.of());

        assertThat(commentsService.getCommentTree(1L, 1L)).isEmpty();
    }

    private static CommentRow row(Long id, Long parentId, String content, Status status) {
        CommentRow row = new CommentRow();
        row.setCommentId(id);
        row.setParentCommentId(parentId);
        row.setNickname("닉네임");
        row.setContent(content);
        row.setCreatedAt("2026-09-13 00:00:00");
        row.setIsMine(true);
        row.setStatus(status);
        return row;
    }
}
