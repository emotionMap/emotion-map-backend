package com.emotionmap.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FCM 푸시 발송용 Firebase Admin SDK 초기화.
 * 서비스 계정 키 파일이 아직 없는 환경(로컬 최초 세팅 전 등)에서도 앱 자체는 정상 기동해야 하므로,
 * 키가 없으면 초기화를 건너뛰고 경고만 남긴다 - 이 경우 푸시 발송만 조용히 no-op된다
 * ({@link com.emotionmap.business.notification.service.PushNotificationService} 참고).
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${fcm.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void init() {
        if (credentialsPath == null || credentialsPath.isBlank() || !Files.exists(Path.of(credentialsPath))) {
            log.warn("[Firebase] fcm.credentials-path가 비어있거나 파일이 없어 초기화를 건너뜁니다 (path: {}). 푸시 발송은 비활성화됩니다.", credentialsPath);
            return;
        }
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                        .build();
                FirebaseApp.initializeApp(options);
                log.info("[Firebase] 초기화 완료");
            }
        } catch (IOException e) {
            log.error("[Firebase] 초기화 실패 - credentialsPath: {}", credentialsPath, e);
        }
    }
}
