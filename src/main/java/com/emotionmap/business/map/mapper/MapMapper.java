package com.emotionmap.business.map.mapper;

import com.emotionmap.business.map.vo.MapEmotionRow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MapMapper {

    // 지역별 최근 부착된 감정 태그 최대 5개씩, 평면으로 한 번에 조회 (Java에서 locationId 기준으로 그룹핑)
    List<MapEmotionRow> getRecentEmotionsByLocation();
}
