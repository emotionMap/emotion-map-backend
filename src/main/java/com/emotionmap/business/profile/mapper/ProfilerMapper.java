package com.emotionmap.business.profile.mapper;

import com.emotionmap.business.profile.payload.ProfileRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfilerMapper {

    void updateProfile(@Param("userId") Long userId, @Param("req") ProfileRequest request);
}
