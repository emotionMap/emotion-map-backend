-- 소셜 로그인(본인인증) 완전 제거 -> 기기 식별자(deviceId) 기반 익명 로그인
-- 적용 대상: 로컬 개발 DB (emotionMap)

-- 1. 기존 유니크 인덱스 제거 (provider, provider_user_id 컬럼에 걸려있음)
ALTER TABLE users DROP INDEX uq_provider_user;

-- 2. 새 컬럼 추가 (NULL 허용 상태로 우선 추가)
ALTER TABLE users ADD COLUMN device_id VARCHAR(64) NULL AFTER id;

-- 3. 기존 데이터 백필 (테스트 유저들이 갖고 있던 provider 정보는 버리고, 고유 식별자만 부여)
UPDATE users SET device_id = CONCAT('legacy-', id) WHERE device_id IS NULL;

-- 4. NOT NULL + UNIQUE 제약 부여, 옛 컬럼 제거
ALTER TABLE users
    MODIFY COLUMN device_id VARCHAR(64) NOT NULL,
    ADD UNIQUE KEY uq_device_id (device_id),
    DROP COLUMN provider,
    DROP COLUMN provider_user_id;
