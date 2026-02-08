package com.emotionmap.business.auth.service;

import com.emotionmap.business.auth.payLoad.AppleTokenPayload;
import com.emotionmap.business.auth.vo.ApplePublicKeysResponse;
import com.emotionmap.common.code.ErrorCode;
import com.emotionmap.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleJwtValidator {

    private static final String APPLE_ISS = "https://appleid.apple.com";
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AppleTokenPayload validate(String identityToken) {

        try {
            // 1. JWT header 파싱
            String headerJson = decode(identityToken.split("\\.")[0]);
            Map<String, String> header =
                    objectMapper.readValue(headerJson, Map.class);

            String kid = header.get("kid");
            String alg = header.get("alg");

            // 2. Apple 공개키 조회
            ApplePublicKeysResponse keys =
                    restTemplate.getForObject(
                            APPLE_KEYS_URL,
                            ApplePublicKeysResponse.class
                    );

            ApplePublicKeysResponse.Key matchedKey =
                    keys.getKeys().stream()
                            .filter(k -> k.getKid().equals(kid) && k.getAlg().equals(alg))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(ErrorCode.APPLE_AUTH_FAILED));

            // 3. 공개키 생성
            PublicKey publicKey = generatePublicKey(matchedKey);

            // 4. JWT 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken)
                    .getBody();

            // 5. payload 검증
            if (!APPLE_ISS.equals(claims.getIssuer())) {
                throw new BusinessException(ErrorCode.APPLE_AUTH_FAILED);
            }

            return objectMapper.convertValue(claims, AppleTokenPayload.class);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.APPLE_AUTH_FAILED);
        }
    }

    private String decode(String tokenPart) {
        return new String(Base64.getUrlDecoder().decode(tokenPart));
    }

    private PublicKey generatePublicKey(ApplePublicKeysResponse.Key key)
            throws Exception {

        byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(spec);
    }
}
