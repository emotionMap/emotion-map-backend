package com.emotionmap.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Emotion Map API")
                        .description("Emotion Map Backend API 문서<br><br>" +
                                "성공 :<br>" +
                                "{<br>" +
                                "&nbsp;&nbsp;\"data\": {<br>" +
                                "&nbsp;&nbsp;&nbsp;&nbsp;\"accessToken\": \"...\",<br>" +
                                "&nbsp;&nbsp;&nbsp;&nbsp;\"refreshToken\": \"...\",<br>" +
                                "&nbsp;&nbsp;&nbsp;&nbsp;\"status\": \"PENDING_PROFILE\"<br>" +
                                "&nbsp;&nbsp;}<br>" +
                                "}<br><br>" +

                                "실패 :<br>" +
                                "{<br>" +
                                "&nbsp;&nbsp;\"error\": {<br>" +
                                "&nbsp;&nbsp;&nbsp;&nbsp;\"code\": \"AUTH_INVALID_TOKEN\",<br>" +
                                "&nbsp;&nbsp;&nbsp;&nbsp;\"message\": \"유효하지 않은 토큰입니다.\"<br>" +
                                "&nbsp;&nbsp;}<br>" +
                                "}"
                        )
                        .version("v1.0.0")
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("local")
//                        , new Server().url("https://api.emotionmap.com").description("prod")
                ));
    }
}