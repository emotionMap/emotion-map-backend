package com.emotionmap.business.notification.service;

import com.emotionmap.business.comments.mapper.CommentsMapper;
import com.emotionmap.business.notification.event.NotificationCreatedEvent;
import com.emotionmap.business.notification.mapper.NotificationMapper;
import com.emotionmap.business.posts.mapper.PostsMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final String LIKE_MESSAGE = "누군가 회원님의 감정에 하트를 남겼어요";

    @Mock
    private NotificationMapper notificationMapper;
    @Mock
    private PostsMapper postsMapper;
    @Mock
    private CommentsMapper commentsMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void notifyLike_본인_글에_본인이_좋아요하면_알림_생성_안함() {
        notificationService.notifyLike(1L, 10L, 10L);

        verifyNoInteractions(notificationMapper, eventPublisher);
    }

    @Test
    void notifyLike_게시글_주인을_못찾으면_알림_생성_안함() {
        notificationService.notifyLike(1L, 10L, null);

        verifyNoInteractions(notificationMapper, eventPublisher);
    }

    @Test
    void notifyLike_다른_유저가_좋아요하면_upsert_후_이벤트_발행() {
        notificationService.notifyLike(1L, 10L, 20L);

        verify(notificationMapper).upsertLikeNotification(20L, 1L, LIKE_MESSAGE);
        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(20L, "LIKE", LIKE_MESSAGE, 1L, null));
    }

    @Test
    void cancelLikeNotification_게시글_주인을_못찾으면_아무것도_안함() {
        notificationService.cancelLikeNotification(1L, null);

        verifyNoInteractions(notificationMapper);
    }

    @Test
    void cancelLikeNotification_다른_좋아요가_남아있으면_알림을_지우지_않는다() {
        when(notificationMapper.existsAnyLike(1L)).thenReturn(true);

        notificationService.cancelLikeNotification(1L, 20L);

        verify(notificationMapper, never()).deleteUnreadLikeNotification(any(), any());
    }

    @Test
    void cancelLikeNotification_남은_좋아요가_없으면_안읽은_알림을_지운다() {
        when(notificationMapper.existsAnyLike(1L)).thenReturn(false);

        notificationService.cancelLikeNotification(1L, 20L);

        verify(notificationMapper).deleteUnreadLikeNotification(20L, 1L);
    }

    @Test
    void notifyCommentCreated_최상위_댓글_본인_글에_본인이_작성하면_알림_생성_안함() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(10L);

        notificationService.notifyCommentCreated(1L, 100L, null, 10L, "익명1");

        verify(notificationMapper, never()).insertNotification(any(), any(), any(), any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void notifyCommentCreated_최상위_댓글_게시글_주인을_못찾으면_알림_생성_안함() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(null);

        notificationService.notifyCommentCreated(1L, 100L, null, 10L, "익명1");

        verify(notificationMapper, never()).insertNotification(any(), any(), any(), any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void notifyCommentCreated_최상위_댓글_다른_유저가_작성하면_게시글_작성자에게_COMMENT_알림() {
        when(postsMapper.selectPostUserId(1L)).thenReturn(20L);

        notificationService.notifyCommentCreated(1L, 100L, null, 10L, "익명1");

        String message = "익명1님이 회원님의 감정에 댓글을 남겼어요";
        verify(notificationMapper).insertNotification(20L, "COMMENT", 1L, 100L, message);
        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(20L, "COMMENT", message, 1L, 100L));
        verifyNoInteractions(commentsMapper);
    }

    @Test
    void notifyCommentCreated_대댓글_부모_댓글_작성자_본인이면_알림_생성_안함() {
        when(commentsMapper.selectCommentUserId(50L)).thenReturn(10L);

        notificationService.notifyCommentCreated(1L, 100L, 50L, 10L, "익명1");

        verify(notificationMapper, never()).insertNotification(any(), any(), any(), any(), any());
        verifyNoInteractions(eventPublisher);
        verifyNoInteractions(postsMapper);
    }

    @Test
    void notifyCommentCreated_대댓글_부모_댓글_작성자를_못찾으면_알림_생성_안함() {
        when(commentsMapper.selectCommentUserId(50L)).thenReturn(null);

        notificationService.notifyCommentCreated(1L, 100L, 50L, 10L, "익명1");

        verify(notificationMapper, never()).insertNotification(any(), any(), any(), any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void notifyCommentCreated_대댓글이면_부모_댓글_작성자에게만_REPLY_알림_게시글_작성자는_건너뜀() {
        when(commentsMapper.selectCommentUserId(50L)).thenReturn(30L);

        notificationService.notifyCommentCreated(1L, 100L, 50L, 10L, "익명1");

        String message = "익명1님이 회원님의 댓글에 답글을 남겼어요";
        verify(notificationMapper).insertNotification(30L, "REPLY", 1L, 100L, message);
        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(30L, "REPLY", message, 1L, 100L));
        verifyNoInteractions(postsMapper);
    }

    @Test
    void getUnreadCount_mapper에게_그대로_위임한다() {
        when(notificationMapper.countUnread(10L)).thenReturn(3);

        int count = notificationService.getUnreadCount(10L);

        org.assertj.core.api.Assertions.assertThat(count).isEqualTo(3);
    }

    @Test
    void getNotifications_1페이지는_offset_0으로_조회한다() {
        notificationService.getNotifications(1, 20, 10L);

        verify(notificationMapper).getNotifications(10L, 0, 20);
    }

    @Test
    void getNotifications_3페이지는_offset을_size의_2배만큼_밀어서_조회한다() {
        notificationService.getNotifications(3, 20, 10L);

        verify(notificationMapper).getNotifications(10L, 40, 20);
    }

    @Test
    void readAll_mapper에게_그대로_위임한다() {
        notificationService.readAll(10L);

        verify(notificationMapper).markAllRead(10L);
    }
}
