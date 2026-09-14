package com.emotionmap.business.map.payload;

import com.emotionmap.business.posts.payload.Emotion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "지역별 지도 요약")
public class MapRegionResponse {

    @Schema(description = "위치 아이디")
    private Long locationId;
    @Schema(description = "시/도")
    private String siDo;
    @Schema(description = "시/군/구")
    private String siGunGu;
    @Schema(description = "이 지역에 최근 부착된 감정 태그 최대 5개 (게시글 작성 시각 기준 최신순, 같은 게시글에서 여러 개 나올 수 있음)")
    private List<Emotion> recentEmotions;
}
