package com.emotionmap.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // Bearer 인증 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Emotion Map API")
                        .description("Emotion Map Backend API 문서<br><br>" +
                                "Authorization: Bearer {accessToken}<br><br>" +
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
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components().addSecuritySchemes("BearerAuth", bearerAuth));
    }
}
