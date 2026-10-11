-- 좋아요/댓글/대댓글 알림 기능 도입
-- 적용 대상: 로컬 개발 DB (emotionMap)

-- 1. 알림 테이블
CREATE TABLE notifications (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,            -- 알림 수신자
    type        VARCHAR(20) NOT NULL,       -- LIKE / COMMENT / REPLY
    post_id     BIGINT NOT NULL,
    comment_id  BIGINT NULL,                -- COMMENT/REPLY일 때만
    message     VARCHAR(255) NOT NULL,
    is_read     TINYINT(1) NOT NULL DEFAULT 0,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notifications_user_unread (user_id, is_read, created_at),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. FCM 디바이스 토큰 (users 1계정 = 1토큰, refresh_token과 동일 패턴)
ALTER TABLE users
    ADD COLUMN fcm_token VARCHAR(255) NULL AFTER refresh_token_expires_at;
