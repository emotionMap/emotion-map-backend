package com.emotionmap.business.profile.mapper;

import com.emotionmap.business.profile.payload.ProfileRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfilerMapper {

    void updateProfile(ProfileRequest request);
}
