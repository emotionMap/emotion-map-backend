package com.emotionmap.business.users.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.service.AuthService;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final AuthService authService;

    public void withdraw(Long userId) {
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }

        userMapper.deleteById(userId);
    }

    @Transactional
    public JWTToken setLocation(Long userId, Long locationId) {
        if (locationId == null) {
            throw new BusinessException(ErrorCode.LOCATION_REQUIRED);
        }

        UserVo user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FIND_USER_INFO);
        }

        userMapper.updateLocation(userId, locationId);
        user.setLocationId(locationId);

        // 위치 설정 완료 상태(locationSet=true)가 반영된 새 토큰을 즉시 발급
        return authService.issueAndSaveTokens(user);
    }
}
