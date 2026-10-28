package com.emotionmap.business.notification.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.notification.event.NotificationCreatedEvent;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 알림 생성 트랜잭션이 커밋된 이후에만 FCM 발송을 시도한다 (AFTER_COMMIT).
 * 발송 실패/토큰 무효는 전부 여기서 흡수하고, 알림 생성(핵심 동작)에는 절대 영향을 주지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private static final String APP_TITLE = "감정연결지도";

    private final UserMapper userMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        if (FirebaseApp.getApps().isEmpty()) {
            return;
        }

        String token = userMapper.findFcmToken(event.userId());
        if (token == null || token.isBlank()) {
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(APP_TITLE)
                        .setBody(event.message())
                        .build())
                .putData("type", event.type())
                .putData("postId", String.valueOf(event.postId()))
                .putData("commentId", event.commentId() != null ? String.valueOf(event.commentId()) : "")
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
                    || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                userMapper.clearFcmToken(event.userId());
                log.info("[Push] 무효 토큰 정리 - userId: {}", event.userId());
            } else {
                log.warn("[Push] 발송 실패 - userId: {}", event.userId(), e);
            }
        }
    }
}
