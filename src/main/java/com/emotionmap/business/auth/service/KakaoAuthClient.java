package com.emotionmap.business.auth.service;


import com.emotionmap.business.auth.vo.KakaoUserResponse;
import com.emotionmap.business.auth.vo.SocialUserInfoVo;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
        KakaoUserResponse body;
        try {

            ResponseEntity<KakaoUserResponse> response =
                    restTemplate.exchange(
                            "https://kapi.kakao.com/v2/user/me",
                            HttpMethod.GET,
                            request,
                            KakaoUserResponse.class
                    );

            body = response.getBody();


        } catch (HttpClientErrorException e) {
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        } catch (HttpServerErrorException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

        return new SocialUserInfoVo(
                String.valueOf(body.getId())
        );
    }
}
