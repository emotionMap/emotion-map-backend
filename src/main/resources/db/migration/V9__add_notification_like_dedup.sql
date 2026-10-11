-- 좋아요 알림 원자적 upsert를 위한 dedup 키 추가
-- 같은 유저/게시글에 대해 안읽은 LIKE 알림이 unique key 충돌로 자동 병합되도록 함 (동시 요청에도 중복 생성 방지)
-- 읽음 처리되면 NULL로 비워서 다음 좋아요 때 새 알림을 또 만들 수 있게 한다 (NotificationService.readAll 참고)
ALTER TABLE notifications
    ADD COLUMN like_dedup_key VARCHAR(40) NULL AFTER is_read,
    ADD UNIQUE KEY uq_notifications_like_dedup (like_dedup_key);
