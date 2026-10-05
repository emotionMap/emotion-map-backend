-- emotion_tags를 긍정/부정 순서로 재구성 (id 1~8=긍정, id 9~16=부정)
-- 적용 대상: 로컬 개발 DB (emotionMap)
-- post_emotion_tag는 전부 테스트 데이터라 삭제하고 emotion_tags를 깨끗한 순서로 새로 삽입한다.

DELETE FROM post_emotion_tag;
DELETE FROM emotion_tags;
ALTER TABLE emotion_tags AUTO_INCREMENT = 1;

INSERT INTO emotion_tags (name, emoji) VALUES
('평온', '😌'),
('즐거움', '😆'),
('행복', '😊'),
('사랑', '😍'),
('설렘', '🤩'),
('감사', '🙏'),
('뿌듯함', '😎'),
('희망', '🌱'),
('슬픔', '😢'),
('불안', '😰'),
('분노', '😡'),
('외로움', '🍂'),
('서운함', '🥺'),
('속상함', '😞'),
('짜증', '😒'),
('답답함', '😤');
