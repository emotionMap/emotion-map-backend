package com.emotionmap.integration;

import com.emotionmap.business.comments.mapper.CommentsMapper;
import com.emotionmap.business.comments.payload.CommentCreateRequest;
import com.emotionmap.business.notification.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/** V8에서 post_emotion_tag 캐스케이드 누락이 실제로 터졌던 영역 - 회원 탈퇴 시 연관 데이터가 전부 같이 삭제되는지 회귀 검증. */
class UserDeleteCascadeIntegrationTest extends AbstractMapperIntegrationTest {

    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    void 회원_탈퇴시_연관된_모든_데이터가_함께_삭제된다() {
        Long userId = createTestUser();
        Long locationId = createTestLocation();
        Long postId = createTestPost(userId, locationId);

        postsMapper.insertLike(postId, userId);
        jdbcTemplate.update(
                "INSERT INTO post_images (post_id, image_url, sort_order) VALUES (?, ?, ?)",
                postId, "https://example.com/it-test.jpg", 0);
        jdbcTemplate.update(
                "INSERT INTO post_anonymous_nickname (post_id, user_id, nickname) VALUES (?, ?, ?)",
                postId, userId, "익명1");

        CommentCreateRequest comment = new CommentCreateRequest();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent("integration test comment");
        commentsMapper.insertComment(comment);

        notificationMapper.insertNotification(userId, "COMMENT", postId, comment.getCommentId(), "테스트 알림");

        userMapper.deleteById(userId);

        assertThat(count("SELECT COUNT(*) FROM posts WHERE id = ?", postId)).isZero();
        assertThat(count("SELECT COUNT(*) FROM post_emotion_tag WHERE post_id = ?", postId)).isZero();
        assertThat(count("SELECT COUNT(*) FROM post_likes WHERE post_id = ?", postId)).isZero();
        assertThat(count("SELECT COUNT(*) FROM post_images WHERE post_id = ?", postId)).isZero();
        assertThat(count("SELECT COUNT(*) FROM post_anonymous_nickname WHERE post_id = ?", postId)).isZero();
        assertThat(count("SELECT COUNT(*) FROM comments WHERE post_id = ?", postId)).isZero();
        assertThat(count("SELECT COUNT(*) FROM notifications WHERE post_id = ?", postId)).isZero();
    }

    private int count(String sql, Long value) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, value);
        return count == null ? 0 : count;
    }
}
