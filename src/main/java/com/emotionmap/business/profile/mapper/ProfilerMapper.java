package com.emotionmap.business.profile.mapper;

import com.emotionmap.business.emotion.payload.EmotionResponse;
import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProfilerMapper {

    void updateProfile(@Param("userId") Long userId, @Param("req") ProfileRequest request);

    void updateProfileInfo(@Param("userId") Long userId, @Param("req") ProfileUpdateRequest request);

    void deleteEmotionTagsByUserId(@Param("userId") Long userId);

    void insertEmotionTags(@Param("userId") Long userId, @Param("emotionTagIds") List<Long> emotionTagIds);

    List<EmotionResponse> findEmotionTagsByUserId(@Param("userId") Long userId);
}
