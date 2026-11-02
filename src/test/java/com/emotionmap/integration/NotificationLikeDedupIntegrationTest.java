package com.emotionmap.integration;

import com.emotionmap.business.notification.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/** V9의 like_dedup_key 유니크 제약으로 안읽은 LIKE 알림이 중복 생성되지 않는지, 읽음 처리 후엔 다시 생성되는지 검증. */
class NotificationLikeDedupIntegrationTest extends AbstractMapperIntegrationTest {

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    void 안읽은_좋아요_알림은_중복_생성되지_않고_읽음처리_후엔_다시_생성된다() {
        Long userId = createTestUser();
        Long locationId = createTestLocation();
        Long postId = createTestPost(userId, locationId);

        notificationMapper.upsertLikeNotification(userId, postId, "누군가 좋아요를 눌렀어요");
        notificationMapper.upsertLikeNotification(userId, postId, "누군가 좋아요를 눌렀어요");

        assertThat(likeNotificationCount(userId, postId)).isEqualTo(1);

        notificationMapper.markAllRead(userId);
        notificationMapper.upsertLikeNotification(userId, postId, "누군가 또 좋아요를 눌렀어요");

        assertThat(likeNotificationCount(userId, postId)).isEqualTo(2);
    }

    private int likeNotificationCount(Long userId, Long postId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND post_id = ? AND type = 'LIKE'",
                Integer.class, userId, postId);
        return count == null ? 0 : count;
    }
}
