package com.emotionmap.business.jwt.filter;

import com.emotionmap.business.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    void 토큰이_없으면_401을_반환하고_체인을_진행하지_않는다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(filterChain);
    }

    @Test
    void 위치_미설정이고_허용되지_않은_경로면_403_LOCATION_REQUIRED() throws Exception {
        MockHttpServletRequest request = authorizedRequest("GET", "/posts");
        MockHttpServletResponse response = new MockHttpServletResponse();
        mockClaims(1L, false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("LOCATION_REQUIRED");
        verifyNoInteractions(filterChain);
    }

    @Test
    void 위치_미설정이어도_위치설정_경로는_통과한다() throws Exception {
        MockHttpServletRequest request = authorizedRequest("PATCH", "/users/me/location");
        MockHttpServletResponse response = new MockHttpServletResponse();
        mockClaims(1L, false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 위치_미설정이어도_회원탈퇴_경로는_통과한다() throws Exception {
        MockHttpServletRequest request = authorizedRequest("DELETE", "/users/me");
        MockHttpServletResponse response = new MockHttpServletResponse();
        mockClaims(1L, false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 위치_설정된_토큰이면_통과한다() throws Exception {
        MockHttpServletRequest request = authorizedRequest("GET", "/posts");
        MockHttpServletResponse response = new MockHttpServletResponse();
        mockClaims(1L, true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 인증_제외_경로는_토큰_없이도_통과하고_JwtProvider를_호출하지_않는다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider);
    }

    private MockHttpServletRequest authorizedRequest(String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.addHeader("Authorization", "Bearer dummy-token");
        return request;
    }

    private void mockClaims(Long userId, boolean locationSet) {
        Claims claims = mock(Claims.class);
        when(claims.get("userId", Long.class)).thenReturn(userId);
        when(claims.get("locationSet", Boolean.class)).thenReturn(locationSet);
        when(jwtProvider.parse("dummy-token")).thenReturn(claims);
    }
}
