package com.emotionmap.business.users.service;

import com.emotionmap.business.auth.mapper.UserMapper;
import com.emotionmap.business.auth.service.AuthService;
import com.emotionmap.business.auth.vo.JWTToken;
import com.emotionmap.business.auth.vo.UserVo;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @Test
    void setLocation_locationId가_없으면_LOCATION_REQUIRED() {
        assertThatThrownBy(() -> userService.setLocation(1L, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.LOCATION_REQUIRED);

        verifyNoInteractions(userMapper, authService);
    }

    @Test
    void setLocation_존재하지_않는_유저면_NOT_FIND_USER_INFO() {
        when(userMapper.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> userService.setLocation(1L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FIND_USER_INFO);

        verifyNoInteractions(authService);
    }

    @Test
    void setLocation_정상이면_위치_갱신후_토큰을_재발급한다() {
        UserVo user = new UserVo();
        user.setId(1L);
        when(userMapper.findById(1L)).thenReturn(user);
        JWTToken reissued = new JWTToken("access", "refresh");
        when(authService.issueAndSaveTokens(user)).thenReturn(reissued);

        JWTToken result = userService.setLocation(1L, 10L);

        verify(userMapper).updateLocation(1L, 10L);
        assertThat(user.getLocationId()).isEqualTo(10L);
        assertThat(result).isSameAs(reissued);
    }

    @Test
    void withdraw_존재하지_않는_유저면_NOT_FIND_USER_INFO() {
        when(userMapper.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> userService.withdraw(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FIND_USER_INFO);

        verify(userMapper, never()).deleteById(any());
    }

    @Test
    void withdraw_정상이면_삭제한다() {
        when(userMapper.findById(1L)).thenReturn(new UserVo());

        userService.withdraw(1L);

        verify(userMapper).deleteById(1L);
    }
}
