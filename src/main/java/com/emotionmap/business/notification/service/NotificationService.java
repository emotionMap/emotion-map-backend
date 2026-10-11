package com.emotionmap.business.notification.service;

import com.emotionmap.business.comments.mapper.CommentsMapper;
import com.emotionmap.business.notification.mapper.NotificationMapper;
import com.emotionmap.business.notification.payload.NotificationResponse;
import com.emotionmap.business.posts.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String LIKE_MESSAGE = "누군가 회원님의 감정에 하트를 남겼어요";

    private final NotificationMapper notificationMapper;
    private final PostsMapper postsMapper;
    private final CommentsMapper commentsMapper;

    /**
     * 좋아요 추가 - 자기 글이면 알림 없음.
     * 같은 유저/게시글에 안읽은 LIKE 알림이 이미 있으면 새로 만들지 않고 시간만 갱신 (반복 토글 노이즈 방지).
     * like_dedup_key 유니크 제약으로 upsert 하므로 동시 요청에도 중복 생성되지 않는다.
     */
    @Transactional
    public void notifyLike(Long postId, Long actorUserId, Long postOwnerId) {
        if (postOwnerId == null || actorUserId.equals(postOwnerId)) {
            return;
        }
        notificationMapper.upsertLikeNotification(postOwnerId, postId, LIKE_MESSAGE);
    }

    /**
     * 좋아요 취소 - 이 게시글에 남은 좋아요가 하나도 없을 때만 안읽은 LIKE 알림을 지운다.
     * (다른 사람이 눌러둔 좋아요가 아직 남아있으면 알림을 그대로 유지)
     */
    @Transactional
    public void cancelLikeNotification(Long postId, Long postOwnerId) {
        if (postOwnerId == null || notificationMapper.existsAnyLike(postId)) {
            return;
        }
        notificationMapper.deleteUnreadLikeNotification(postOwnerId, postId);
    }

    /**
     * 댓글/대댓글 작성 - 최상위 댓글은 게시글 작성자에게 COMMENT,
     * 대댓글은 그 바로 위 부모 댓글 작성자에게만 REPLY (게시글 작성자에게 중복 전송하지 않음)
     */
    @Transactional
    public void notifyCommentCreated(Long postId, Long commentId, Long parentCommentId, Long actorUserId, String actorNickname) {
        if (parentCommentId == null) {
            Long postOwnerId = postsMapper.selectPostUserId(postId);
            if (postOwnerId == null || actorUserId.equals(postOwnerId)) {
                return;
            }
            String message = actorNickname + "님이 회원님의 감정에 댓글을 남겼어요";
            notificationMapper.insertNotification(postOwnerId, "COMMENT", postId, commentId, message);
        } else {
            Long parentOwnerId = commentsMapper.selectCommentUserId(parentCommentId);
            if (parentOwnerId == null || actorUserId.equals(parentOwnerId)) {
                return;
            }
            String message = actorNickname + "님이 회원님의 댓글에 답글을 남겼어요";
            notificationMapper.insertNotification(parentOwnerId, "REPLY", postId, commentId, message);
        }
    }

    /** 뱃지 활성/비활성 판단용 */
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    /** 알림 목록 */
    public List<NotificationResponse> getNotifications(int page, int size, Long userId) {
        int offset = (page - 1) * size;
        return notificationMapper.getNotifications(userId, offset, size);
    }

    /** 전체 읽음 처리 - 알림 목록 화면 진입 시 호출, 뱃지 비활성화 */
    @Transactional
    public void readAll(Long userId) {
        notificationMapper.markAllRead(userId);
    }
}
