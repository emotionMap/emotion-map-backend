package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.vo.NaverUserResponse;
import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverAuthClient {

    private final RestTemplate restTemplate;

    public SocialUserInfoVo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange(
                        "https://openapi.naver.com/v1/nid/me",
                        HttpMethod.GET,
                        request,
                        NaverUserResponse.class
                );

        NaverUserResponse body = response.getBody();

        return new SocialUserInfoVo(
                body.getResponse().getId(),
                body.getResponse().getEmail(),
                body.getResponse().getNickname(),
                body.getResponse().getProfileImage()
        );
    }
}
