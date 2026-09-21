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
│   ├── auth/          # 기기 식별자(deviceId) 기반 익명 로그인 + JWT 발급 (본인인증 없음)
│   │   ├── controller/    # AuthController → /auth
│   │   ├── mapper/        # UserMapper
│   │   ├── payload/       # AuthLoginRequest/Response, AuthRefreshRequest
│   │   ├── service/       # AuthService
│   │   └── vo/            # UserVo, JWTToken
│   ├── jwt/               # JwtProvider, JwtAuthenticationFilter, JwtUser
│   ├── posts/             # 게시글 CRUD + 좋아요 + 게시글 단위 익명 닉네임 (게시글은 항상 최상위, 댓글은 별도 도메인)
│   │   ├── controller/    # PostsController → /posts
│   │   ├── mapper/        # PostsMapper
│   │   ├── payload/       # PostListResponse, PostDetailResponse, PostCreateRequest, PostUpdateRequest, Image, Emotion, Status
│   │   └── service/       # PostsService, AnonymousNicknameService
│   ├── comments/          # 댓글 (대댓글 무제한 중첩)
│   │   ├── controller/    # CommentsController → POST /posts/{postId}/comments, /comments/{commentId}
│   │   ├── mapper/        # CommentsMapper
│   │   ├── payload/       # CommentCreateRequest/UpdateRequest/Response
│   │   ├── service/       # CommentsService (평면 조회 → Java에서 트리 조립)
│   │   └── vo/            # CommentRow (DB 평면 조회용)
│   ├── emotion/           # 감정 태그 목록 조회
│   │   ├── controller/    # EmotionController → /emotion  ← 서비스 없이 mapper 직접 호출
│   │   ├── mapper/        # EmotionMapper
│   │   └── payload/       # EmotionResponse
│   ├── location/          # 위치(시/도, 시/군/구) 조회
│   │   ├── controller/    # LocationController → /location  ← 서비스 없이 mapper 직접 호출
│   │   ├── mapper/        # LocationMapper
│   │   └── payload/       # SigunguResponse
│   ├── users/             # 사용자 관리 (가입 시 위치 설정 + 탈퇴)
│   │   ├── controller/    # UserController → /users
│   │   ├── payload/       # LocationUpdateRequest
│   │   └── service/       # UserService
│   └── map/               # 지역별 지도 요약 (순수 조회/집계, 스키마 변경 없음)
│       ├── controller/    # MapController → /map
│       ├── mapper/        # MapMapper (윈도우 함수로 지역별 최근 5개 조회)
│       ├── payload/       # MapRegionResponse
│       ├── service/       # MapService (평면 조회 → Java에서 지역별 그룹핑)
│       └── vo/            # MapEmotionRow
├── common/
│   ├── config/        # SecurityConfig, S3Config, SwaggerConfig
│   ├── code/          # ErrorCode enum
│   ├── exception/     # GlobalExceptionHandler, BusinessException
│   └── payload/       # ApiResponse<T>, ErrorResponse
└── test/              # S3Service test stub, TestController
```

### API Endpoints

| 메서드 | URL | 설명 | 인증 |
|---|---|---|---|
| POST | /auth/login | 익명 로그인 (deviceId 기반, 본인인증 없음) | 불필요 |
| POST | /auth/refresh | 액세스 토큰 갱신 (리프레시 토큰 사용) | 불필요 |
| POST | /auth/logout | 로그아웃 (리프레시 토큰 무효화) | 필요 |
| PATCH | /users/me/location | 가입 시 위치 설정 (필수, 최초 1회 / 이후 변경 가능) | 필요 |
| DELETE | /users/me | 회원 탈퇴 | 필요 |
| GET | /map | 지역별 지도 요약 (지역마다 최근 부착된 감정 태그 최대 5개, 게시글 없는 지역은 제외) | 필요 |
| GET | /posts | 게시글 목록 (`locationId` 생략 시 전체 피드, 지정 시 그 지역으로 필터링 - 지도에서 지역 선택 시 사용) | 필요 |
| GET | /posts/me | 내 게시글 목록 | 필요 |
| GET | /posts/me/emotion-stats?days= | 마이페이지 개인 감정 통계 (최근 N일간 감정 태그별 사용 횟수, 많이 쓴 순, 기본 7일) | 필요 |
| GET | /posts/{postId} | 게시글 상세 (댓글을 대댓글까지 중첩 트리로 한 번에 포함) | 필요 |
| POST | /posts | 게시글 생성 | 필요 |
| PATCH | /posts/{postId} | 게시글 수정 | 필요 |
| DELETE | /posts/{postId} | 게시글 삭제 (soft delete) | 필요 |
| POST | /posts/{postId}/like | 좋아요 토글 | 필요 |
| POST | /posts/{postId}/comments | 댓글/대댓글 작성 (`parentCommentId`로 무제한 중첩) | 필요 |
| PATCH | /comments/{commentId} | 댓글 수정 | 필요 |
| DELETE | /comments/{commentId} | 댓글 삭제 (soft delete, 대댓글은 유지) | 필요 |
| GET | /emotion | 감정 태그 목록 | 필요 |
| GET | /location/sido | 시/도 목록 | 불필요 |
| GET | /location/sigungu?siDo= | 시/군/구 목록 | 불필요 |

### Request lifecycle

1. `JwtAuthenticationFilter`가 `Authorization: Bearer <token>`을 검증하고, `JwtUser` (userId, locationSet)를 Spring Security principal로 설정한다.
   - 필터 제외 경로: `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/test/**`
   - `SecurityConfig`는 `.anyRequest().permitAll()`로 설정되어 있으나, 실질적 인증은 JwtAuthenticationFilter가 담당한다. 필터를 통과하지 못하면 컨트롤러에서 `@AuthenticationPrincipal`이 null이 되어 NPE가 발생한다.
   - `locationSet=false`인 토큰은 `PATCH /users/me/location`, `DELETE /users/me` 외의 모든 경로에서 `403 LOCATION_REQUIRED`로 막힌다.
2. Controllers extract the principal via `@AuthenticationPrincipal JwtUser jwtUser`.
3. Services call MyBatis mapper interfaces; mapper XML lives under `src/main/resources/mapper/`.
4. All responses are wrapped in `ApiResponse<T>` (`{ "data": ... }`); errors go through `GlobalExceptionHandler` → `ErrorResponse`.

### Auth flow

- 본인인증 없는 익명 로그인이다. 클라이언트가 기기별로 생성해 보관하는 `deviceId`를 `POST /auth/login`으로 보내면, `AuthService.login`이 `UserMapper.findByDeviceId`로 기존 계정을 찾거나 `UserVo.newAnonymousUser`로 새 계정을 만든다. 소셜 제공자 검증 단계가 전혀 없다 — 같은 deviceId면 항상 같은 계정으로 로그인된다.
- `JwtProvider`가 access token (30 min, claims: `userId`, `locationSet`)과 refresh token (90 days, subject = userId)을 발급한다.
- 리프레시 토큰은 DB (`users.refresh_token`, `users.refresh_token_expires_at`)에 저장된다. 갱신 요청 시 DB 값과 대조 검증.
- 신규 유저는 `users.location_id`가 NULL이라 토큰의 `locationSet`이 `false`로 발급되며, `PATCH /users/me/location` 호출 전까지는 위치 설정/탈퇴 외의 API를 쓸 수 없다. 위치를 설정하면 `locationSet=true`가 반영된 새 토큰을 즉시 재발급한다 (`UserService.setLocation` → `AuthService.issueAndSaveTokens` 재사용).
- 프로필(닉네임/자기소개/사진) 개념 자체가 없다 — 완전 익명 게시판이며, 게시글 작성 시 부여되는 닉네임은 계정에 저장되지 않고 스레드 단위로만 존재한다 (아래 Business rules 참고).
- 로그아웃 시 DB의 `refresh_token`, `refresh_token_expires_at`을 NULL로 초기화한다.

### MyBatis conventions

- Mapper interfaces live alongside domain classes (`business/<domain>/mapper/`); XML files are at `src/main/resources/mapper/<domain>/`.
- `application.yml` enables `map-underscore-to-camel-case: true`, so DB column `created_at` maps to `createdAt` automatically.
- MyBatis debug logging is on (`logging.level.com.emotionmap: DEBUG`).
- **Post 조회 패턴 (N+1 회피)**: 게시글 목록/상세 조회 시 images와 emotions를 별도 쿼리로 일괄 조회한 뒤 Java 코드에서 postId 기준으로 조립한다.
  ```
  getPosts() → postIdList 추출 → getPostsImage(postIdList), getPostsEmotion(postIdList) → 각 post에 set
  ```

### Business rules

- **게시글 생성 필수값**: `locationId`, `emotionIds` (비어있으면 `INVALID_POST_REQUEST`)
- **게시글/댓글은 완전히 분리된 개념**이다. `posts`는 항상 최상위 글이며 다른 글의 하위가 될 수 없다. 댓글은 별도 `comments` 테이블에서 `parent_comment_id`로 자기참조하며 **댓글 → 대댓글 → 대대댓글 → ...로 깊이 제한 없이** 중첩된다.
- **댓글/대댓글 작성**: `POST /posts/{postId}/comments`, body의 `parentCommentId`가 없으면 게시글에 바로 다는 최상위 댓글, 있으면 그 댓글의 대댓글. 게시글 상세 조회(`GET /posts/{postId}`) 1번으로 그 글의 **전체 댓글 트리(대댓글 포함)를 한 번에** 내려준다 — `CommentsMapper.getComments`로 평면 조회 후 `CommentsService.getCommentTree`가 Java에서 2-pass로 트리를 조립한다 (부모가 항상 자식보다 먼저 생성되므로 재귀 쿼리 불필요).
- **댓글 삭제**: soft delete — 삭제된 댓글도 트리에는 남고(`status='DELETED'`), 그 아래 대댓글은 그대로 유지된다.
- **게시글 삭제**: soft delete — `status = 'DELETED'` 업데이트, DB에서 실제 삭제하지 않음.
- **좋아요 토글**: 반환값 `"Y"` (좋아요 추가) / `"N"` (취소). 댓글에는 좋아요 없음.
- **개인 감정 통계**: `GET /posts/me/emotion-stats?days=`로 최근 N일간 본인이 작성한 게시글의 감정 태그별 사용 횟수를 많이 쓴 순으로 보여준다 (마이페이지 상단 버튼용). 댓글은 감정 태그가 없어 집계 대상이 아니다.
- **피드 조회 위치 필터**: `GET /posts`는 기본적으로 위치 필터 없이 전체 피드를 보여준다. `locationId`를 지정하면 그 지역으로 필터링 — 지도에서 다른 지역을 선택해 둘러볼 때 쓰인다.
- **지도 지역별 요약**: `GET /map`은 지역(시/군/구)마다 "최근 부착된 감정 태그" 최대 5개를 보여준다. 게시글 단위가 아니라 태그 부착 기록 단위라 한 게시글에서 여러 개가 나올 수 있다 (`MapMapper`가 윈도우 함수로 지역별 상위 5개를 뽑고 `MapService`가 Java에서 그룹핑). 게시글이 없는 지역은 응답에서 제외된다. 스키마 변경 없는 순수 조회 기능이다.
- **익명 닉네임 (완전 익명 게시판)**: 프로필/타 사용자 조회 개념이 없다. 게시글이든 그 아래 몇 단계 대댓글이든, 작성 시 `AnonymousNicknameService`가 **게시글(postId) 단위**로 닉네임을 자동 배정해 `post_anonymous_nickname`에 저장한다. **같은 게시글 안에서는 같은 유저 = 항상 같은 닉네임**, **다른 게시글에서는 같은 유저라도 다른 닉네임**이 나온다. 응답에는 `userId`/`profileImageUrl`을 내려주지 않는다 (계정 식별자가 노출되면 게시글 간 익명성이 클라이언트에서 깨질 수 있음).
- **권한 검사**: 게시글/댓글 수정·삭제 시 각각 `posts.user_id`/`comments.user_id`와 요청자 userId 비교. 불일치 시 `FORBIDDEN`.
- **회원 탈퇴**: `users` 레코드 완전 삭제 (ON DELETE CASCADE로 관련 데이터 자동 삭제).

### Common coding patterns

**에러 발생**
```java
throw new BusinessException(ErrorCode.POST_NOT_FOUND);
```

**응답 래핑**
```java
return ResponseEntity.ok(ApiResponse.of(data));  // 데이터 있는 경우
return ResponseEntity.ok(ApiResponse.of(null));  // void
```

**페이지네이션** (게시글 목록)
- 파라미터: `page` (1-based, default=1), `size` (default=20)
- offset 계산: `offset = (page - 1) * size`

**소유권 검증 패턴**
```java
Long ownerId = postsMapper.selectPostUserId(postId);
if (ownerId == null) throw new BusinessException(ErrorCode.POST_NOT_FOUND);
if (!ownerId.equals(userId)) throw new BusinessException(ErrorCode.FORBIDDEN);
```

### Key domain relationships

- `posts` → `post_images` (one-to-many), `post_emotion_tag` → `emotion_tags` (many-to-many), `post_likes`, `comments` (모두 ON DELETE CASCADE).
- `comments.parent_comment_id`: 자기참조로 무제한 중첩 (댓글/대댓글/대대댓글/...). `NULL`이면 게시글에 바로 단 최상위 댓글.
- `post_anonymous_nickname` (post_id, user_id) → nickname: 게시글별 익명 닉네임 배정 테이블. `post_anonymous_nickname.post_id`, `.user_id`가 복합 PK. 게시글 작성자든 그 아래 어느 깊이의 댓글 작성자든 같은 `post_id` 기준으로 조회/배정된다.
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
- 본인인증(소셜 로그인 등) 없이, 클라이언트가 기기별로 생성해 보관하는 `device_id`로만 식별하는 완전 익명 계정이다. 앱을 지우고 새 deviceId로 다시 로그인하면 이전 계정과의 연결은 끊어진다.
- 프로필(닉네임/자기소개/사진) 개념이 없는 완전 익명 게시판이다. 가입 직후 필요한 건 위치(location) 설정 하나뿐이며, 설정 전에는 `PATCH /users/me/location`과 회원 탈퇴 외의 API를 쓸 수 없다.
- 타 사용자 프로필을 조회하는 API 자체가 없다. 본인 게시글은 `GET /posts/me`(마이페이지)로만 확인 가능.

### Post
- 본문(content), 감정 태그(N개), 이미지(N개), 위치(1개)를 포함한다. 항상 최상위 글이며 다른 글의 하위가 될 수 없다.
- 작성자 표시는 실제 계정이 아니라 게시글 단위로 배정되는 익명 닉네임이다 — 같은 게시글(그 아래 댓글 포함) 안에서는 같은 유저가 항상 같은 닉네임으로 보이고, 다른 게시글에서는 다른 닉네임으로 보인다.

### Comment
- 게시글에 종속되며(`comments.post_id`), `parent_comment_id`로 자기참조해 **댓글 → 대댓글 → 대대댓글 → ...로 깊이 제한 없이** 중첩된다.
- 위치/감정 태그가 없다 — `content`만 필수.
- 게시글 상세 조회 1번으로 전체 댓글 트리가 한 번에 내려온다 (화면 전환 없이 한눈에 보이는 구조).
- soft delete — 삭제돼도 대댓글은 트리에 그대로 남는다.

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
- `GET /map`으로 지역(시/군/구)별 요약을 제공한다: 지역마다 최근 부착된 감정 태그 최대 5개(게시글 단위 아님, 태그 부착 기록 단위). 게시글이 하나도 없는 지역은 제외된다.
- 지역을 선택했을 때의 "그 지역 피드"는 별도 API가 아니라 `GET /posts?locationId=`로 기존 피드를 재사용한다.
- DB 스키마 변경 없이 `posts`/`post_emotion_tag`/`locations`를 조합한 순수 조회 기능이다.
