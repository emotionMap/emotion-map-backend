package com.emotionmap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class NaverTests {
    @Autowired
    RestTemplate restTemplate;

    @Test
    void 네이버_API_실제_호출_확인() {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth("토큰토큰");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        ResponseEntity<NaverUserResponse> response =
//                restTemplate.exchange(
//                        "https://openapi.naver.com/v1/nid/me",
//                        HttpMethod.GET,
//                        request,
//                        NaverUserResponse.class
//                );
//
//        NaverUserResponse body = response.getBody();

    }

}
