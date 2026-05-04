package com.emotionmap.business.jwt.filter;

import com.emotionmap.business.jwt.provider.JwtProvider;
import com.emotionmap.business.jwt.vo.JwtUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 모든 HTTP 요청마다 딱한번 Controller 가기전에 무조건 실행되는 코드*/
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        try {
            String path = request.getRequestURI();

            // 🔥 인증이 필요 없는 URL 추가
            if (path.startsWith("/auth/test-login") || path.startsWith("/auth/login") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 요청 헤더에 Authorization: Bearer xxx 있는지
            String token = resolveToken(request);

            // 토큰 검증 시작
            if (token != null) {
                Claims claims = jwtProvider.parse(token);

                Long userId = claims.get("userId", Long.class);
                String status = claims.get("status", String.class);

                if (!"REGISTERED".equals(status)) {
                    sendUnauthorized(response);
                    return;
                }

                JwtUser principal = new JwtUser(userId, status);

                // Spring Security에 사용자 등록
                Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendUnauthorized(response);
        }
    }


    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"AUTH_REQUIRED\",\"message\":\"인증이 필요합니다.\"}");
    }
}
