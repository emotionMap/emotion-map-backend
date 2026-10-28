package com.emotionmap.business.notification.event;

/**
 * 알림 row가 생성/갱신된 직후 발행되는 이벤트. {@link com.emotionmap.business.notification.service.PushNotificationService}가
 * 트랜잭션 커밋 이후(AFTER_COMMIT)에만 구독해서 FCM 발송을 담당한다 - 푸시 발송 실패가 알림 생성 자체에 영향을 주지 않게 분리하기 위함.
 */
public record NotificationCreatedEvent(Long userId, String type, String message, Long postId, Long commentId) {
}
