package com.emotionmap.business.map.service;

import com.emotionmap.business.map.mapper.MapMapper;
import com.emotionmap.business.map.payload.MapRegionResponse;
import com.emotionmap.business.map.vo.MapEmotionRow;
import com.emotionmap.business.posts.payload.Emotion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapMapper mapMapper;

    /**지역별 요약 - 게시글이 하나도 없는 지역은 결과에 포함하지 않는다*/
    public List<MapRegionResponse> getRegionSummaries() {
        List<MapEmotionRow> rows = mapMapper.getRecentEmotionsByLocation();

        Map<Long, MapRegionResponse> byLocation = new LinkedHashMap<>();
        for (MapEmotionRow row : rows) {
            MapRegionResponse region = byLocation.computeIfAbsent(row.getLocationId(), id -> {
                MapRegionResponse r = new MapRegionResponse();
                r.setLocationId(row.getLocationId());
                r.setSiDo(row.getSiDo());
                r.setSiGunGu(row.getSiGunGu());
                r.setRecentEmotions(new ArrayList<>());
                return r;
            });
            region.getRecentEmotions().add(new Emotion(row.getPostId(), row.getEmotionId(), row.getEmoji(), row.getName()));
        }

        return new ArrayList<>(byLocation.values());
    }
}
