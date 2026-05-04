package com.emotionmap.business.emotion.mapper;

import com.emotionmap.business.emotion.payload.EmotionResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmotionMapper {

    List<EmotionResponse> getEmotion();
}
