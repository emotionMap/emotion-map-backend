package com.emotionmap.business.profile.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.service.AuthService;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.profile.mapper.ProfilerMapper;
import com.emotionmap.business.profile.payload.ProfileRequest;
import com.emotionmap.business.profile.payload.ProfileResponse;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserMapper userMapper;
    private final AuthService authService;
    private final ProfilerMapper profilerMapper;

    public ProfileResponse reg(Long userId, ProfileRequest request) {
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }
        if (user.isActive()) {
            throw new BusinessException(ErrorCode.EXAMPLE0);
        }

        user.activate();
        profilerMapper.updateProfile(userId, request);

        JWTToken token = authService.issueAndSaveTokens(user);

        return new ProfileResponse(user.getStatus(), token);
    }
}
