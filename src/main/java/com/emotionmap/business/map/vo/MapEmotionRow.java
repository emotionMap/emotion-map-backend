package com.emotionmap.business.map.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 지역별 "최근 부착된 감정 태그" 평면 조회 1행. {@link com.emotionmap.business.map.service.MapService}가
 * locationId 기준으로 이 행들을 묶어 MapRegionResponse 리스트로 조립한다.
 */
@Getter
@Setter
public class MapEmotionRow {
    private Long locationId;
    private String siDo;
    private String siGunGu;
    private Long postId;
    private Long emotionId;
    private String emoji;
    private String name;
}
