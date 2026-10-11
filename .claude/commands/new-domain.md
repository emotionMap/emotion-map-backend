---
description: emotion_app 표준 레이어 구조(controller/mapper/payload/service)로 새 도메인 뼈대를 생성한다
---

사용법: `/new-domain <도메인명(영문 소문자)> <한 줄 설명>`

## 참고할 기존 패턴

CLAUDE.md의 "Package layout", "MyBatis conventions", "Common coding patterns" 섹션을 먼저 확인한다. 가장 가까운 기존 도메인(예: `business/emotion`처럼 단순한 조회형, `business/comments`처럼 서비스가 있는 CRUD형)을 골라 그 구조를 그대로 따른다.

## 생성할 파일

- `business/<domain>/controller/<Domain>Controller.java` — `/<domain>` 베이스 경로, `@AuthenticationPrincipal JwtUser`로 인증 주체 추출, `ApiResponse<T>`로 래핑
- `business/<domain>/mapper/<Domain>Mapper.java` — MyBatis 매퍼 인터페이스
- `src/main/resources/mapper/<domain>/<Domain>Mapper.xml` — 매퍼 XML (namespace는 인터페이스 FQCN)
- `business/<domain>/payload/` — 필요한 Request/Response 클래스
- `business/<domain>/service/<Domain>Service.java` — 비즈니스 로직 (서비스 없이 mapper 직접 호출하는 단순 조회형이면 생략 가능, emotion/location 도메인 참고)

## 규칙

- 실제 비즈니스 로직/SQL은 비워두고 `// TODO` 로 표시한다. 뼈대만 생성하고 세부 구현은 별도 작업으로 넘긴다.
- 에러는 `ErrorCode` enum에 새 항목을 추가해 `BusinessException(ErrorCode.XXX)`로 던진다.
- 파일을 실제로 만들기 전에 생성할 파일 목록을 먼저 보여주고 승인을 받는다 (CLAUDE.md 작업 응답 규칙을 따른다).
- 기존 도메인 코드 스타일(네이밍, 어노테이션 순서 등)을 그대로 따른다 — 새로운 컨벤션을 만들지 않는다.
