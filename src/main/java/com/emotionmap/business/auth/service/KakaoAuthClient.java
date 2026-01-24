package com.emotionmap.business.auth.service;


import com.emotionmap.business.auth.vo.KakaoUserResponse;
import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoAuthClient {

    private final RestTemplate restTemplate;

    public SocialUserInfoVo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response =
                restTemplate.exchange(
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.GET,
                        request,
                        KakaoUserResponse.class
                );

        KakaoUserResponse body = response.getBody();

        return new SocialUserInfoVo(
                String.valueOf(body.getId()),
                body.getKakaoAccount().getEmail(),
                body.getKakaoAccount().getProfile().getNickname(),
                body.getKakaoAccount().getProfile().getProfileImageUrl()
        );
    }
}
