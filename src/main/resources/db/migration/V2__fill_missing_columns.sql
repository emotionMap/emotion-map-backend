-- EC2 운영 DB가 로컬 개발 스키마보다 오래되어 있어 누락된 컬럼/구조를 보강
-- 적용 대상: EC2 운영 DB (emotionMap) -- 다른 3개 마이그레이션보다 먼저 실행

-- 1. users: location 기반 위치 서비스 + refresh token 컬럼 (로컬엔 이미 있었으나 운영 DB엔 누락)
ALTER TABLE users
    ADD COLUMN location_id BIGINT NULL,
    ADD COLUMN refresh_token VARCHAR(512) NULL,
    ADD COLUMN refresh_token_expires_at DATETIME NULL;

-- 2. locations: 단일 location_name -> si_do/si_gun_gu 2단계 구조로 재편
--    기존 25개 행은 전부 서울특별시 소속 구 데이터이므로 si_do는 일괄 '서울특별시'로 채운다.
ALTER TABLE locations
    ADD COLUMN si_do VARCHAR(100) NOT NULL DEFAULT '서울특별시' AFTER id,
    CHANGE COLUMN location_name si_gun_gu VARCHAR(100) NOT NULL;

ALTER TABLE locations ALTER COLUMN si_do DROP DEFAULT;
