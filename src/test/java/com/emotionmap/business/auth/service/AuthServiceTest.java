package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.payLoad.AuthLoginResponse;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.business.jwt.provider.JwtProvider;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_deviceId가_비어있으면_INVALID_LOGIN_REQUEST() {
        assertThatThrownBy(() -> authService.login(" "))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_LOGIN_REQUEST);

        verifyNoInteractions(userMapper);
    }

    @Test
    void login_처음_보는_deviceId면_신규_유저를_생성한다() {
        when(userMapper.findByDeviceId("device-1")).thenReturn(null);
        when(jwtProvider.createAccessToken(any())).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(any())).thenReturn("refresh-token");

        AuthLoginResponse response = authService.login("device-1");

        verify(userMapper).insertUserInfo(argThat(u -> "device-1".equals(u.getDeviceId())));
        assertThat(response.isLocationSet()).isFalse();
        assertThat(response.getToken().getAccessToken()).isEqualTo("access-token");
    }

    @Test
    void login_기존_deviceId면_신규_생성하지_않고_재사용한다() {
        UserVo existing = new UserVo();
        existing.setId(7L);
        existing.setDeviceId("device-1");
        existing.setLocationId(1L);
        when(userMapper.findByDeviceId("device-1")).thenReturn(existing);
        when(jwtProvider.createAccessToken(any())).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(any())).thenReturn("refresh-token");

        AuthLoginResponse response = authService.login("device-1");

        verify(userMapper, never()).insertUserInfo(any());
        assertThat(response.isLocationSet()).isTrue();
    }

    @Test
    void refresh_토큰_파싱_실패시_INVALID_REFRESH_TOKEN() {
        when(jwtProvider.parseSubject("bad-token")).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> authService.refresh("bad-token"))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void refresh_DB에_저장된_토큰과_다르면_INVALID_REFRESH_TOKEN() {
        when(jwtProvider.parseSubject("token")).thenReturn(1L);
        UserVo user = new UserVo();
        user.setId(1L);
        user.setRefreshToken("다른-토큰");
        when(userMapper.findById(1L)).thenReturn(user);

        assertThatThrownBy(() -> authService.refresh("token"))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    @Test
    void refresh_정상이면_새_토큰쌍을_발급한다() {
        when(jwtProvider.parseSubject("token")).thenReturn(1L);
        UserVo user = new UserVo();
        user.setId(1L);
        user.setRefreshToken("token");
        when(userMapper.findById(1L)).thenReturn(user);
        when(jwtProvider.createAccessToken(any())).thenReturn("new-access");
        when(jwtProvider.createRefreshToken(any())).thenReturn("new-refresh");

        JWTToken token = authService.refresh("token");

        assertThat(token.getAccessToken()).isEqualTo("new-access");
        assertThat(token.getRefreshToken()).isEqualTo("new-refresh");
        verify(userMapper).updateRefreshToken(eq(1L), eq("new-refresh"), any());
    }
}
