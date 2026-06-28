TRUNCATE TABLE product, category RESTART IDENTITY CASCADE;

-- =============================================
-- CATEGORY DATA
-- =============================================

INSERT INTO category (id, parent_id, name, description) VALUES
(1, NULL, '스킨케어', '얼굴 피부를 위한 케어 제품'),
(2, NULL, '헤어케어', '두피와 모발을 위한 케어 제품'),
(3, NULL, '바디케어', '몸을 위한 클렌징과 보습 제품'),
(4, NULL, '핸드케어', '손을 위한 세정과 보습 제품'),
(5, NULL, '프래그런스', '향수와 공간을 위한 향 제품');

INSERT INTO category (id, parent_id, name, description) VALUES
(6, 1, '클렌저', '세안을 위한 클렌징 제품'),
(7, 1, '토너', '피부 결을 정돈하는 토너 제품'),
(8, 1, '세럼', '피부 고민을 집중 케어하는 세럼'),
(9, 1, '모이스처라이저', '피부에 수분과 영양을 공급하는 제품'),
(10, 1, '마스크', '피부를 집중 관리하는 마스크'),
(11, 2, '샴푸', '두피와 모발을 세정하는 제품'),
(12, 2, '컨디셔너', '모발에 영양과 윤기를 더하는 제품'),
(13, 3, '바디 클렌저', '전신을 부드럽게 세정하는 제품'),
(14, 3, '바디 모이스처라이저', '전신 보습을 위한 제품'),
(15, 4, '핸드워시', '손 세정을 위한 제품'),
(16, 4, '핸드크림', '손 보습을 위한 제품'),
(17, 5, '오 드 퍼퓸', '개성을 표현하는 향수'),
(18, 5, '룸 스프레이', '공간에 향을 더하는 제품');

SELECT setval('category_id_seq', (SELECT MAX(id) FROM category));

-- =============================================
-- PRODUCT DATA
-- =============================================

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(6, '루센트 비너스 클렌징 밤', '민감한 피부를 위한 부드러운 밤 타입 클렌저. 피부 자극 없이 메이크업과 노폐물을 제거합니다.', '100ml', '민감성', '로즈 오일, 알로에 베라, 카모마일 추출물', 62000, 50, '/images/products/product-01.png', false, NOW()),
(6, '파슬리 씨드 페이셜 클렌저', '항산화 성분이 풍부한 파슬리 씨드 추출물로 피부를 깨끗하게 정돈하는 젤 타입 클렌저입니다.', '100ml', '지성/복합성', '파슬리 씨드 추출물, 비타민 B, 판테놀', 62000, 45, '/images/products/product-02.png', true, NOW()),
(6, '프리뷰트 레이어 에센스 클렌저', '가벼운 텍스처로 피부의 불순물을 산뜻하게 제거하는 에센스 오일 기반 클렌저입니다.', '200ml', '건성/복합성', '에센셜 오일 블렌드, 알로에 베라, 글리세린', 68000, 30, '/images/products/product-03.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(7, '파슬리 씨드 안티 옥시던트 토너', '피부 결을 정돈하고 항산화 성분으로 피부를 보호하는 토너입니다.', '100ml', '모든 피부', '파슬리 씨드, 비타민 C, 비타민 E', 72000, 40, '/images/products/product-04.png', true, NOW()),
(7, '베이직 리파이닝 스킨 토너', '피부 pH 균형을 맞추고 모공을 정돈하는 데일리 토너입니다.', '100ml', '지성/복합성', '히알루론산, 위치하젤, 판테놀', 58000, 55, '/images/products/product-05.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(8, '파슬리 씨드 안티 옥시던트 인텐스 세럼', '강력한 항산화 성분으로 피부 활력을 높이고 윤기를 더하는 세럼입니다.', '30ml', '모든 피부', '파슬리 씨드 추출물, 비타민 C, 나이아신아마이드', 148000, 25, '/images/products/product-06.png', true, NOW()),
(8, '레저렉션 페이셜 컨센트레이트', '피부 탄력과 광채를 높이는 고농축 페이셜 컨센트레이트입니다.', '30ml', '건성/탄력', '레티놀, 알란토인, 펩타이드', 178000, 20, '/images/products/product-07.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(9, '카멜리아 페이셜 하이드레이팅 크림', '건조한 피부에 깊은 수분을 공급하는 리치한 텍스처의 크림입니다.', '60ml', '건성', '카멜리아 추출물, 시어버터, 스쿠알란', 102000, 35, '/images/products/product-08.png', true, NOW()),
(9, '모이스처라이징 크림', '가벼운 텍스처로 빠르게 흡수되는 데일리 모이스처라이저입니다.', '60ml', '지성/복합성', '알로에 베라, 알란토인, 판테놀', 88000, 40, '/images/products/product-09.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(10, '파슬리 씨드 안티 옥시던트 마스크', '항산화 성분으로 피부 컨디션을 회복시켜 주는 클레이 마스크입니다.', '60ml', '모든 피부', '파슬리 씨드, 카올린 클레이, 비타민 E', 92000, 30, '/images/products/product-10.png', true, NOW()),
(10, '루센트 페이셜 리파이너', '각질을 부드럽게 정돈하고 피부 결을 매끈하게 가꾸는 리파이닝 마스크입니다.', '75ml', '복합성', 'AHA, BHA, 카올린 클레이', 98000, 25, '/images/products/product-11.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(11, '로즈마리 스칼프 앤 헤어 클렌저', '두피를 산뜻하게 세정하고 모발에 생기를 더하는 로즈마리 샴푸입니다.', '200ml', NULL, '로즈마리 잎 추출물, 멘톨, 판테놀', 72000, 40, '/images/products/product-12.png', true, NOW()),
(11, '샤인 리페어 헤어 세럼', '수면 중 두피와 모발을 집중 케어하는 헤어 세럼입니다.', '50ml', NULL, '아르간 오일, 케라틴, 비오틴', 98000, 20, '/images/products/product-13.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(12, '헤어 밤', '손상된 모발을 집중 케어하고 부드러움을 더하는 헤어 밤입니다.', '60ml', NULL, '시어버터, 코코넛 오일, 아르간 오일', 68000, 35, '/images/products/product-14.png', false, NOW()),
(12, '모이스처라이징 헤어 마스크', '건조하고 손상된 모발에 깊은 영양을 공급하는 헤어 마스크입니다.', '200ml', NULL, '올리브 오일, 알란토인, 판테놀', 82000, 25, '/images/products/product-15.png', true, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(13, '시트러스 리프 바디 클렌저', '상쾌한 시트러스 향이 나는 부드러운 바디 클렌저입니다.', '500ml', NULL, '시트러스 잎 추출물, 알로에 베라, 글리세린', 68000, 60, '/images/products/product-16.png', true, NOW()),
(13, '너리싱 바디 클렌저', '건조한 피부를 위한 영양 공급 바디 클렌저입니다.', '500ml', NULL, '올리브 오일, 시어버터, 알로에 베라', 72000, 45, '/images/products/product-17.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(14, '레저렉션 아로마틱 바디 밤', '피부를 촉촉하게 가꾸는 리치한 바디 밤입니다.', '180ml', NULL, '시어버터, 코코넛 오일, 비타민 E', 82000, 35, '/images/products/product-18.png', true, NOW()),
(14, '시트러스 리프 바디 로션', '가볍게 흡수되는 일상용 바디 로션입니다.', '250ml', NULL, '시트러스 오일, 알로에 베라, 글리세린', 72000, 40, '/images/products/product-19.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(15, '레저렉션 아로마틱 핸드워시', '손을 부드럽게 세정하고 향기를 남기는 핸드워시입니다.', '500ml', NULL, '로즈마리 잎 추출물, 알로에 베라, 글리세린', 58000, 70, '/images/products/product-20.png', true, NOW()),
(15, '로즈마리 앤 시더 핸드워시', '로즈마리와 시더우드 향이 조화를 이루는 핸드워시입니다.', '500ml', NULL, '로즈마리 오일, 시더우드 오일, 판테놀', 58000, 50, '/images/products/product-21.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(16, '레저렉션 아로마틱 핸드밤', '건조한 손을 집중 케어하는 리치한 핸드 밤입니다.', '75ml', NULL, '시어버터, 올리브 오일, 비타민 E', 48000, 60, '/images/products/product-22.png', true, NOW()),
(16, '로즈마리 핸드크림', '빠르게 흡수되고 끈적임이 적은 데일리 핸드크림입니다.', '75ml', NULL, '로즈마리 추출물, 알로에 베라, 글리세린', 42000, 55, '/images/products/product-23.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(17, '테싯 오 드 퍼퓸', '상쾌하고 그린한 향이 조화를 이루는 오 드 퍼퓸입니다.', '50ml', NULL, '시더우드, 베티버, 그레이프프루트 오일', 198000, 20, '/images/products/product-24.png', true, NOW()),
(17, '오르너 오 드 퍼퓸', '플로럴과 우디 향이 어우러진 우아한 오 드 퍼퓸입니다.', '50ml', NULL, '일랑일랑, 재스민, 시더우드', 198000, 15, '/images/products/product-25.png', false, NOW()),
(17, '프래그런스 스토리 컬렉션 I', '다섯 가지 향을 경험할 수 있는 프래그런스 세트입니다.', '5x10ml', NULL, '에센셜 오일 블렌드', 248000, 10, '/images/products/product-26.png', false, NOW());

INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(18, '포스트 푸 드롭스 룸 스프레이', '상쾌하고 허브 향이 가득한 룸 스프레이입니다.', '100ml', NULL, '파슬리 씨드, 로즈마리, 시더우드', 78000, 30, '/images/products/product-27.png', true, NOW()),
(18, '인센스 아로마틱 룸 스프레이', '따뜻하고 스파이시한 향의 룸 스프레이입니다.', '100ml', NULL, '클로브, 시나몬, 앰버', 78000, 25, '/images/products/product-28.png', false, NOW());

-- ADMIN TEST ACCOUNT
INSERT INTO member (name, email, phone, password, role, address, created_at) VALUES
('관리자', 'admin@test.com', '010-0000-0000', '$2a$10$w3oB4MKY8LeDC.pjj1Nz1etPwPsV72bUjjXYnPW0rbMyfPB2hbjpG', 'ADMIN', '테스트', NOW())
ON CONFLICT (email) DO UPDATE SET
  name = EXCLUDED.name,
  phone = EXCLUDED.phone,
  password = EXCLUDED.password,
  role = EXCLUDED.role,
  address = EXCLUDED.address;
