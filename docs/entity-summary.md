# Entity Summary

## 테이블 관계 (ERD 요약)

```
users ──── locations (location_id)
 ├─< posts (user_id)
 │      └─< post_images      (post_id)
 │      └─< post_emotion_tag (post_id) >─ emotion_tags
 │      └─< post_likes       (post_id, user_id)
 │      └── locations         (location_id)
 └─< user_emotion_tag (user_id) >─ emotion_tags
```

- `posts.parent_id` + `posts.depth` 로 댓글 계층 표현 (별도 댓글 테이블 없음)

---

## 테이블 상세

### users

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | bigint AUTO_INCREMENT | PK |
| provider | provider | enum('kakao','naver') NOT NULL | 소셜 제공자 |
| provider_user_id | providerUserId | varchar(100) NOT NULL | 제공자 측 사용자 ID |
| nickname | nickname | varchar(50) NULL | 닉네임 |
| bio | bio | varchar(255) NULL | 자기소개 |
| profile_image_url | profileImageUrl | varchar(300) NULL | 프로필 이미지 URL |
| status | status | enum('REGISTERED','UNREGISTERED','pending_profile') NULL | 회원 상태 (MVP: REGISTERED/UNREGISTERED만 사용) |
| location_id | locationId | int NULL | FK → locations.id (프로필 등록 시 설정, 설정에서 변경 가능) |
| last_login_at | lastLoginAt | datetime NULL | 마지막 로그인 일시 |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |
| updated_at | updatedAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 수정 일시 |

> (provider, provider_user_id) UNIQUE — 동일 제공자의 중복 가입 방지

---

### posts

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | postId | bigint AUTO_INCREMENT | PK |
| parent_id | parentId | bigint NULL | FK → posts.id (self, ON DELETE CASCADE), null이면 루트 게시글 |
| depth | depth | tinyint NOT NULL DEFAULT 0 | 댓글 깊이 (0: 게시글, 1+: 댓글) |
| user_id | userId | bigint NOT NULL | FK → users.id (ON DELETE CASCADE) |
| location_id | locationId | int NULL | FK → locations.id |
| content | content | text NULL | 본문 |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |
| updated_at | updatedAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 수정 일시 |
| status | status | varchar(20) NOT NULL DEFAULT 'ACTIVE' | ACTIVE / DELETED |

---

### locations

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | locationId | int AUTO_INCREMENT | PK |
| si_do | siDo | varchar(100) NOT NULL | 시/도 (서울특별시, 경기도 등) |
| si_gun_gu | siGunGu | varchar(100) NOT NULL | 시/군/구 (강남구, 수원시 등) |

---

### post_images

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | bigint AUTO_INCREMENT | PK |
| post_id | postId | bigint NOT NULL | FK → posts.id (ON DELETE CASCADE) |
| image_url | url | varchar(300) NOT NULL | S3 이미지 URL |
| sort_order | sortOrder | int DEFAULT 0 | 정렬 순서 |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |

---

### emotion_tags

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | int AUTO_INCREMENT | PK |
| name | name | varchar(50) NOT NULL | 감정 이름 (UNIQUE) |
| emoji | emoji | varchar(50) NULL | 이모지 문자 |

---

### post_emotion_tag

| 컬럼 | DB 타입 | 설명 |
|---|---|---|
| post_id | bigint NOT NULL | PK (복합), FK → posts.id |
| emotion_id | int NOT NULL | PK (복합), FK → emotion_tags.id |

---

### post_likes

| 컬럼 | Java 필드 | DB 타입 | 설명 |
|---|---|---|---|
| id | id | bigint AUTO_INCREMENT | PK |
| post_id | postId | bigint NOT NULL | FK → posts.id (ON DELETE CASCADE) |
| user_id | userId | bigint NOT NULL | FK → users.id (ON DELETE CASCADE) |
| created_at | createdAt | datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성 일시 |

> (post_id, user_id) UNIQUE — 동일 사용자의 중복 좋아요 방지

---

### user_emotion_tag

유저 프로필에 표시할 감정 상태 태그 (최대 5개). 5개 제한은 애플리케이션 레이어에서 검증한다.

| 컬럼 | DB 타입 | 설명 |
|---|---|---|
| user_id | bigint NOT NULL | PK (복합), FK → users.id (ON DELETE CASCADE) |
| emotion_id | int NOT NULL | PK (복합), FK → emotion_tags.id (ON DELETE RESTRICT) |

---

## Java 클래스 ↔ 테이블 대응

| 클래스 | 위치 | 대응 테이블 |
|---|---|---|
| `UserVo` | `auth/vo/` | users |
| `JwtUser` | `jwt/vo/` | users (JWT 클레임) |
| `PostListResponse` | `posts/payload/` | posts + join |
| `PostDetailResponse` | `posts/payload/` | posts + join (댓글 포함) |
| `Image` | `posts/payload/` | post_images |
| `Emotion` | `posts/payload/` | post_emotion_tag + emotion_tags |
| `EmotionResponse` | `emotion/payload/` | emotion_tags |
| `SigunguResponse` | `location/payload/` | locations |
| `ProfileMeResponse` | `profile/payload/` | users + user_emotion_tag + locations |
