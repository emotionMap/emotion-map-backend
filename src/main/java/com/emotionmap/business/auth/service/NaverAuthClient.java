package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.vo.NaverUserResponse;
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
public class NaverAuthClient {

    private final RestTemplate restTemplate;

    public SocialUserInfoVo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<NaverUserResponse> response =
                    restTemplate.exchange(
                            "https://openapi.naver.com/v1/nid/me",
                            HttpMethod.GET,
                            request,
                            NaverUserResponse.class
                    );

            NaverUserResponse body = response.getBody();

            if (body == null ||
                    !"00".equals(body.getResultcode()) ||
                    body.getResponse() == null ||
                    body.getResponse().getId() == null) {

                throw new BusinessException(ErrorCode.NAVER_AUTH_FAILED);
            }

            return new SocialUserInfoVo(body.getResponse().getId());

        } catch (HttpClientErrorException e) {
            throw new BusinessException(ErrorCode.NAVER_AUTH_FAILED);
        } catch (HttpServerErrorException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
}
