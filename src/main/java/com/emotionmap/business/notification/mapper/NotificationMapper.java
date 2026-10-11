package com.emotionmap.business.notification.mapper;

import com.emotionmap.business.notification.payload.NotificationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    // 알림 생성
    void insertNotification(@Param("userId") Long userId, @Param("type") String type,
                             @Param("postId") Long postId, @Param("commentId") Long commentId,
                             @Param("message") String message);

    // 좋아요 알림 원자적 upsert - 같은 유저/게시글에 안읽은 LIKE 알림이 이미 있으면 시간만 갱신,
    // 없으면 새로 생성 (like_dedup_key 유니크 제약으로 동시 요청에도 중복 생성 방지)
    void upsertLikeNotification(@Param("userId") Long userId, @Param("postId") Long postId, @Param("message") String message);

    // 이 게시글에 남아있는 좋아요가 하나라도 있는지 (취소 시 알림을 지울지 판단용)
    boolean existsAnyLike(Long postId);

    // 안읽은 LIKE 알림 삭제
    void deleteUnreadLikeNotification(@Param("userId") Long userId, @Param("postId") Long postId);

    // 뱃지 활성/비활성 판단용
    int countUnread(Long userId);

    // 알림 목록
    List<NotificationResponse> getNotifications(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    // 전체 읽음 처리
    void markAllRead(Long userId);
}
