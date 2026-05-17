package com.emotionmap.business.profile.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.service.AuthService;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.emotion.payload.EmotionResponse;
import com.emotionmap.business.profile.mapper.ProfilerMapper;
import com.emotionmap.business.profile.payload.ProfileMeResponse;
import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileResponse;
import com.emotionmap.business.profile.payload.ProfileUpdateRequest;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserMapper userMapper;
    private final AuthService authService;
    private final ProfilerMapper profilerMapper;

    @Transactional
    public ProfileResponse reg(Long userId, ProfileRequest request) {
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }
        if (user.isActive()) {
            log.warn("[Profile] 이미 등록된 유저 프로필 재등록 시도 - userId: {}", userId);
            throw new BusinessException(ErrorCode.EXAMPLE0);
        }

        List<Long> emotionTagIds = request.getEmotionTagIds();
        if (emotionTagIds == null || emotionTagIds.isEmpty()) {
            throw new BusinessException(ErrorCode.EMOTION_TAG_REQUIRED);
        }
        if (emotionTagIds.size() > 5) {
            throw new BusinessException(ErrorCode.EMOTION_TAG_LIMIT_EXCEEDED);
        }

        user.activate();
        profilerMapper.updateProfile(userId, request);
        profilerMapper.insertEmotionTags(userId, emotionTagIds);

        JWTToken token = authService.issueAndSaveTokens(user);

        return new ProfileResponse(user.getStatus(), token);
    }

    public ProfileMeResponse getMyProfile(Long userId) {
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }

        List<EmotionResponse> emotionTags = profilerMapper.findEmotionTagsByUserId(userId);

        return new ProfileMeResponse(
                user.getNickname(),
                user.getBio(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                emotionTags
        );
    }

    public void withdraw(Long userId) {
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }

        userMapper.deleteById(userId);
    }

    @Transactional
    public ProfileMeResponse updateMyProfile(Long userId, ProfileUpdateRequest request) {
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }

        List<Long> emotionTagIds = request.getEmotionTagIds();
        if (emotionTagIds != null) {
            if (emotionTagIds.isEmpty()) {
                throw new BusinessException(ErrorCode.EMOTION_TAG_REQUIRED);
            }
            if (emotionTagIds.size() > 5) {
                throw new BusinessException(ErrorCode.EMOTION_TAG_LIMIT_EXCEEDED);
            }
        }

        profilerMapper.updateProfileInfo(userId, request);

        if (emotionTagIds != null) {
            profilerMapper.deleteEmotionTagsByUserId(userId);
            profilerMapper.insertEmotionTags(userId, emotionTagIds);
        }

        List<EmotionResponse> updatedTags = profilerMapper.findEmotionTagsByUserId(userId);

        return new ProfileMeResponse(
                request.getNickname() != null ? request.getNickname() : user.getNickname(),
                request.getBio() != null ? request.getBio() : user.getBio(),
                request.getProfileImageUrl() != null ? request.getProfileImageUrl() : user.getProfileImageUrl(),
                user.getCreatedAt(),
                updatedTags
        );
    }
}
