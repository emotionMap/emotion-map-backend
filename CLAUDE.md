# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run (uses application-local.yml by default)
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.emotionmap.EmotionMapApplicationTests"

# Clean build
./gradlew clean build
```

The active Spring profile defaults to `local`. To switch: `./gradlew bootRun --args='--spring.profiles.active=prod'`

## Architecture

**Spring Boot 3.5.7 / Java 17** REST API for an emotion-tagged social map app. Uses MyBatis for SQL (no JPA), MySQL as primary database.

### Package layout

```
com.emotionmap
├── business/
│   ├── auth/          # Social login (Kakao, Naver, Apple) + JWT issuance
│   ├── jwt/           # JwtProvider, JwtAuthenticationFilter, JwtUser
│   ├── posts/         # Post CRUD with images, emotions, likes
│   ├── profile/       # Profile registration (post-social-login step)
│   ├── emotion/       # Emotion tag listing
│   ├── users/         # User domain VO
│   └── map/           # Location features
├── common/
│   ├── config/        # SecurityConfig, S3Config, SwaggerConfig, RestTemplateConfig
│   ├── code/          # ErrorCode enum
│   ├── exception/     # GlobalExceptionHandler, BusinessException
│   └── payload/       # ApiResponse<T>, ErrorResponse
└── test/              # S3Service test stub, TestController
```

### Request lifecycle

1. `JwtAuthenticationFilter` validates `Authorization: Bearer <token>` and sets a `JwtUser` (userId, status) as the Spring Security principal — skip filter for `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/test/**`.
2. Controllers extract the principal via `@AuthenticationPrincipal JwtUser jwtUser`.
3. Services call MyBatis mapper interfaces; mapper XML lives under `src/main/resources/mapper/`.
4. All responses are wrapped in `ApiResponse<T>` (`{ "data": ... }`); errors go through `GlobalExceptionHandler` → `ErrorResponse`.

### Auth flow

- Social login hits `AuthController` → `SocialAuthService` delegates to provider-specific client (Kakao/Naver/Apple).
- On success, `JwtProvider` issues an access token (30 min, claims: `userId`, `status`) and a refresh token (14 days, subject = userId).
- New users have `status = UNREGISTERED` and must complete profile registration via `ProfileController` before accessing other features.

### MyBatis conventions

- Mapper interfaces live alongside domain classes (`business/<domain>/mapper/`); XML files are at `src/main/resources/mapper/<domain>/`.
- `application.yml` enables `map-underscore-to-camel-case: true`, so DB column `created_at` maps to `createdAt` automatically.
- MyBatis debug logging is on (`logging.level.com.emotionmap: DEBUG`).

### Key domain relationships

- `posts` → `post_images` (one-to-many), `post_emotion_tag` → `emotion_tags` (many-to-many), `post_likes`
- Post list queries join images and emotions into `PostListResponse.imageList` and `PostListResponse.emotionList`.

### Environment config

| File | Purpose |
|---|---|
| `application.yml` | Base (MyBatis paths, active profile selector) |
| `application-local.yml` | Local MySQL, JWT secret, AWS credentials |
| `application-prod.yml` | Production overrides |

AWS S3 is used for image storage; credentials and bucket name are set per-profile.

### Swagger UI

Available at `http://localhost:8080/swagger-ui/index.html` when running locally. All public endpoints are documented with `@Tag` / `@Operation` annotations.

## Security Rules

- Do not print DB passwords, access tokens, API keys, or secret values.
- If environment files are needed, summarize only key names and never expose values.
- Never modify production-like configuration without explicit approval.

## 작업 응답 규칙

- 사용자의 질문과 작업 지시는 한국어로 해석하고, 답변도 한국어로 작성한다.
- 파일 수정 전에는 먼저 수정 계획을 설명한다.
- 사용자가 명시적으로 승인하기 전까지 파일을 수정하지 않는다.
- 한 번에 여러 영역을 수정하지 않는다.
- 백엔드, 프론트엔드, DB 변경은 가능한 한 분리해서 진행한다.
- 기존 코드 스타일을 우선 따른다.
- 대규모 리팩토링보다 최소 수정 방식을 우선한다.
- 새 라이브러리 추가가 필요하면 먼저 이유를 설명하고 확인을 받는다.
- 수정 후에는 변경된 파일 목록, 변경 이유, 확인해야 할 테스트를 요약한다.

## 작업 제한 규칙

- 요청 범위 밖의 파일은 수정하지 않는다.
- 보안 정보, 환경 변수, DB 접속 정보, 토큰 값은 출력하지 않는다.
- 불확실한 부분은 추측으로 수정하지 말고 먼저 질문하거나 가정 사항을 명시한다.
- 코드 삭제가 필요한 경우 삭제 이유를 먼저 설명한다.

## Domain Entity Summary

> 상세 테이블 컬럼 및 관계 다이어그램: [docs/entity-summary.md](docs/entity-summary.md)

### User
- 소셜 로그인(Kakao / Naver / Apple)으로 생성되며, provider + provider_user_id로 식별한다.
- status: UNREGISTERED → REGISTERED (프로필 등록 시 전환)

### Post
- 본문(content), 감정 태그(N개), 이미지(N개), 위치(1개)를 포함한다.
- 댓글은 별도 테이블 없이 posts.parent_id + depth 로 계층 표현한다.

### Emotion
- 게시글에 선택되는 감정 태그 (emotion_tags 테이블)
- 감정 목록 조회 API로 제공한다.

### Image
- 게시글 첨부 이미지; S3 URL, 정렬 순서(sort_order) 보유
- 추후 개발 예정

### Location
- 게시글 1개에 위치 1개 연결 (locations 테이블)
- si_do(시/도) + si_gun_gu(시/군/구) 계층 구조로 전국 단위 확장 가능
- 사용자가 시/도 선택 후 시/군/구를 선택하는 2단계 UI 흐름에 대응한다.
- posts.location_id는 구 단위 locations.id를 참조한다.

### Map
- 위치 데이터 기반으로 게시글을 지도 위에 표시한다.

