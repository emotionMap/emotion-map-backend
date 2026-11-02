package com.emotionmap.integration;

import com.emotionmap.business.map.mapper.MapMapper;
import com.emotionmap.business.map.vo.MapEmotionRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/** 손으로 짠 ROW_NUMBER() OVER (PARTITION BY location_id ...) 윈도우 함수 검증. */
class MapMapperIntegrationTest extends AbstractMapperIntegrationTest {

    @Autowired
    private MapMapper mapMapper;

    @Test
    void 지역별로_최대_5개까지만_반환하고_삭제된_글은_제외한다() {
        Long userId = createTestUser();
        Long busyLocationId = createTestLocation();
        Long quietLocationId = createTestLocation();

        for (int i = 0; i < 6; i++) {
            createTestPost(userId, busyLocationId);
        }
        Long deletedPostId = createTestPost(userId, quietLocationId);
        jdbcTemplate.update("UPDATE posts SET status = 'DELETED' WHERE id = ?", deletedPostId);

        List<MapEmotionRow> rows = mapMapper.getRecentEmotionsByLocation();

        Map<Long, List<MapEmotionRow>> byLocation = rows.stream()
                .collect(Collectors.groupingBy(MapEmotionRow::getLocationId));

        assertThat(byLocation.get(busyLocationId)).hasSize(5);
        assertThat(byLocation).doesNotContainKey(quietLocationId);
    }
}
