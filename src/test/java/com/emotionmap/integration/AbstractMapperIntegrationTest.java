package com.emotionmap.integration;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.posts.mapper.PostsMapper;
import com.emotionmap.business.posts.payload.PostCreateRequest;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

/**
 * 로컬 개발 MySQL(application-local.yml)에 직접 붙어서 매퍼 XML의 실제 SQL을 검증하는 통합 테스트 베이스.
 * 각 테스트는 @Transactional로 끝나면 자동 롤백되어 로컬 dev 데이터를 건드리지 않는다.
 * 로컬 MySQL이 떠있어야 실행 가능 (기본 `test` 태스크에서는 제외, `integrationTest` 태스크로만 실행).
 */
@Tag("integration")
@SpringBootTest
@ActiveProfiles("local")
@Transactional
abstract class AbstractMapperIntegrationTest {

    protected static final Long STABLE_EMOTION_ID = 1L;

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected PostsMapper postsMapper;

    protected Long createTestUser() {
        UserVo user = UserVo.newAnonymousUser("it-test-device-" + UUID.randomUUID());
        userMapper.insertUserInfo(user);
        return user.getId();
    }

    protected Long createTestLocation() {
        String suffix = UUID.randomUUID().toString();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO locations (si_do, si_gun_gu) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "it-테스트시도-" + suffix);
            ps.setString(2, "it-테스트시군구-" + suffix);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    protected Long createTestPost(Long userId, Long locationId) {
        PostCreateRequest request = new PostCreateRequest();
        request.setUserId(userId);
        request.setLocationId(locationId);
        request.setContent("integration test post");
        postsMapper.insertPost(request);
        postsMapper.insertPostEmotionTags(request.getPostId(), List.of(STABLE_EMOTION_ID));
        return request.getPostId();
    }
}
