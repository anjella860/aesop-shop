TRUNCATE TABLE product, category RESTART IDENTITY CASCADE;
-- =============================================
-- CATEGORY 데이터 (부모 카테고리 먼저)
-- =============================================

-- 부모 카테고리
INSERT INTO category (id, parent_id, name, description) VALUES
(1, NULL, '스킨케어', '얼굴 피부를 위한 케어 제품'),
(2, NULL, '헤어케어', '두피와 모발을 위한 케어 제품'),
(3, NULL, '바디케어', '몸을 위한 케어 제품'),
(4, NULL, '핸드케어', '손을 위한 케어 제품'),
(5, NULL, '프래그런스', '향수 및 룸 프래그런스 제품');

-- 자식 카테고리
INSERT INTO category (id, parent_id, name, description) VALUES
(6,  1, '클렌저', '세안을 위한 클렌징 제품'),
(7,  1, '토너', '피부 결을 정돈하는 토너 제품'),
(8,  1, '세럼', '피부 집중 케어 세럼'),
(9,  1, '모이스처라이저', '피부 수분 공급 제품'),
(10, 1, '마스크', '피부 집중 케어 마스크'),
(11, 2, '샴푸', '두피 및 모발 클렌징'),
(12, 2, '컨디셔너', '모발 영양 및 보호'),
(13, 3, '바디 클렌저', '전신 세정 제품'),
(14, 3, '바디 모이스처라이저', '전신 보습 제품'),
(15, 4, '핸드워시', '손 세정 제품'),
(16, 4, '핸드크림', '손 보습 제품'),
(17, 5, '오 드 퍼퓸', '향수'),
(18, 5, '룸 스프레이', '공간을 위한 프래그런스');

-- 시퀀스 동기화 (PostgreSQL)
SELECT setval('category_id_seq', (SELECT MAX(id) FROM category));


-- =============================================
-- PRODUCT 데이터
-- =============================================

-- 스킨케어 - 클렌저 (category_id: 6)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(6, '루스 비너스 클렌징 밀크', '민감한 피부를 위한 부드러운 밀크 타입 클렌저. 피부 자극 없이 메이크업과 불순물을 제거합니다.', '100ml', '민감성', '로즈힙 오일, 알로에 베라, 카모마일 추출물', 62000, 50, '/images/products/product-01.png', false, NOW()),
(6, '파슬리 씨드 페이셜 클렌저', '항산화 성분이 풍부한 파슬리 씨드 추출물로 피부를 깨끗하게 정돈하는 젤 타입 클렌저.', '100ml', '지성/복합성', '파슬리 씨드 추출물, 비타민 B, 판테놀', 62000, 45, '/images/products/product-02.png', true, NOW()),
(6, '임비브 라이트 에센셜 클렌저', '가벼운 텍스처로 피부 깊숙이 불순물을 제거하는 에센셜 오일 기반 클렌저.', '200ml', '건성/복합성', '에센셜 오일 블렌드, 알로에 베라, 글리세린', 68000, 30, '/images/products/product-03.png', false, NOW());

-- 스킨케어 - 토너 (category_id: 7)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(7, '파슬리 씨드 안티 옥시던트 토너', '피부 결을 정돈하고 항산화 성분으로 피부를 보호하는 토너.', '100ml', '모든 피부', '파슬리 씨드, 비타민 C, 비타민 E', 72000, 40, '/images/products/product-04.png', true, NOW()),
(7, '베이식 리파이닝 스킨 토너', '피부 pH 균형을 맞추고 모공을 정돈하는 기본 토너.', '100ml', '지성/복합성', '살리실산, 위치하젤, 알란토인', 58000, 55, '/images/products/product-05.png', false, NOW());

-- 스킨케어 - 세럼 (category_id: 8)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(8, '파슬리 씨드 안티 옥시던트 인텐스 세럼', '강력한 항산화 성분으로 피부 노화를 예방하고 윤기를 더하는 세럼.', '30ml', '모든 피부', '파슬리 씨드 추출물, 비타민 C, 나이아신아마이드', 148000, 25, '/images/products/product-06.png', true, NOW()),
(8, '레졸루트 페이셜 컨센트레이트', '피부 탄력과 광채를 높이는 고농축 페이셜 컨센트레이트.', '30ml', '건성/노화', '레티놀, 히알루론산, 펩타이드', 178000, 20, '/images/products/product-07.png', false, NOW());

-- 스킨케어 - 모이스처라이저 (category_id: 9)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(9, '카밀 페이셜 하이드레이팅 크림', '건조한 피부에 깊은 수분을 공급하는 리치한 텍스처의 크림.', '60ml', '건성', '카밀 추출물, 시어버터, 스쿠알란', 102000, 35, '/images/products/product-08.png', true, NOW()),
(9, '모이스처라이징 크림', '가벼운 텍스처로 빠르게 흡수되는 데일리 모이스처라이저.', '60ml', '지성/복합성', '알로에 베라, 히알루론산, 판테놀', 88000, 40, '/images/products/product-09.png', false, NOW());

-- 스킨케어 - 마스크 (category_id: 10)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(10, '파슬리 씨드 안티 옥시던트 마스크', '항산화 성분으로 피부 활력을 되찾아주는 클레이 마스크.', '60ml', '모든 피부', '파슬리 씨드, 카올린 클레이, 비타민 E', 92000, 30, '/images/products/product-10.png', true, NOW()),
(10, '루센트 페이셜 리파이너', '각질을 제거하고 피부 결을 매끄럽게 정돈하는 리파이닝 마스크.', '75ml', '복합성', 'AHA, BHA, 카올린 클레이', 98000, 25, '/images/products/product-11.png', false, NOW());

-- 헤어케어 - 샴푸 (category_id: 11)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(11, '로즈마리 스칼프 & 헤어 클렌저', '두피 건강을 케어하고 모발에 윤기를 더하는 로즈마리 샴푸.', '200ml', NULL, '로즈마리 잎 추출물, 멘톨, 판테놀', 72000, 40, '/images/products/product-12.png', true, NOW()),
(11, '나이트 라이드 나이트 리페어링 세럼', '수면 중 두피와 모발을 집중 케어하는 나이트 세럼.', '50ml', NULL, '아르간 오일, 케라틴, 비오틴', 98000, 20, '/images/products/product-13.png', false, NOW());

-- 헤어케어 - 컨디셔너 (category_id: 12)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(12, '헤어 발름', '손상된 모발을 집중 케어하고 부드러움을 더하는 헤어 밤.', '60ml', NULL, '시어버터, 코코넛 오일, 아르간 오일', 68000, 35, '/images/products/product-14.png', false, NOW()),
(12, '모이스처라이징 헤어 마스크', '건조하고 손상된 모발에 깊은 영양을 공급하는 헤어 마스크.', '200ml', NULL, '올리브 오일, 히알루론산, 판테놀', 82000, 25, '/images/products/product-15.png', true, NOW());

-- 바디케어 - 바디 클렌저 (category_id: 13)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(13, '제라늄 리프 바디 클렌저', '상쾌한 제라늄 향이 나는 부드러운 바디 클렌저.', '500ml', NULL, '제라늄 잎 추출물, 알로에 베라, 글리세린', 68000, 60, '/images/products/product-16.png', true, NOW()),
(13, '엘레오스 너리싱 바디 클렌저', '건조한 피부를 위한 영양 공급 바디 클렌저.', '500ml', NULL, '올리브 오일, 시어버터, 알로에 베라', 72000, 45, '/images/products/product-17.png', false, NOW());

-- 바디케어 - 바디 모이스처라이저 (category_id: 14)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(14, '레버런스 아르보레센스 바디 밤', '피부를 촉촉하게 가꾸는 리치한 바디 밤.', '180ml', NULL, '시어버터, 코코넛 오일, 비타민 E', 82000, 35, '/images/products/product-18.png', true, NOW()),
(14, '제라늄 리프 바디 로션', '가볍게 흡수되는 일상용 바디 로션.', '250ml', NULL, '제라늄 오일, 알로에 베라, 글리세린', 72000, 40, '/images/products/product-19.png', false, NOW());

-- 핸드케어 - 핸드워시 (category_id: 15)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(15, '레버런스 아르보레센스 핸드워시', '손을 부드럽게 세정하고 향기를 남기는 핸드워시.', '500ml', NULL, '로즈마리 잎 추출물, 알로에 베라, 글리세린', 58000, 70, '/images/products/product-20.png', true, NOW()),
(15, '로즈마리 &amp; 세다 핸드워시', '로즈마리와 삼나무 향이 조화를 이루는 핸드워시.', '500ml', NULL, '로즈마리 오일, 삼나무 오일, 판테놀', 58000, 50, '/images/products/product-21.png', false, NOW());

-- 핸드케어 - 핸드크림 (category_id: 16)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(16, '레버런스 아르보레센스 핸드밤', '건조한 손을 집중 케어하는 리치한 핸드 밤.', '75ml', NULL, '시어버터, 올리브 오일, 비타민 E', 48000, 60, '/images/products/product-22.png', true, NOW()),
(16, '로즈마리 핸드크림', '빠르게 흡수되고 끈적임 없는 데일리 핸드크림.', '75ml', NULL, '로즈마리 추출물, 알로에 베라, 글리세린', 42000, 55, '/images/products/product-23.png', false, NOW());

-- 프래그런스 - 오 드 퍼퓸 (category_id: 17)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(17, '테싯 오 드 퍼퓸', '신선하고 우디한 향이 조화를 이루는 퍼퓸.', '50ml', NULL, '시더우드, 베티베르, 앰브레트 씨드', 198000, 20, '/images/products/product-24.png', true, NOW()),
(17, '오르너 오 드 퍼퓸', '플로럴과 우디가 어우러진 우아한 퍼퓸.', '50ml', NULL, '일랑일랑, 재스민, 시더우드', 198000, 15, '/images/products/product-25.png', false, NOW()),
(17, '프래그런스 앤솔러지 볼륨 I', '아이솝의 대표 향수 5종이 담긴 앤솔러지 세트.', '5x10ml', NULL, '다양한 에센셜 오일 블렌드', 248000, 10, '/images/products/product-26.png', false, NOW());

-- 프래그런스 - 룸 스프레이 (category_id: 18)
INSERT INTO product (category_id, name, description, volume, skin_type, ingredients, price, stock, image_url, is_bestseller, created_at) VALUES
(18, '올루스 아로마틱 룸 스프레이', '신선하고 허브향이 가득한 룸 스프레이.', '100ml', NULL, '파슬리 씨드, 로즈마리, 시더우드', 78000, 30, '/images/products/product-27.png', true, NOW()),
(18, '아크네 아로마틱 룸 스프레이', '따뜻하고 스파이시한 향의 룸 스프레이.', '100ml', NULL, '클로브, 시나몬, 앰버', 78000, 25, '/images/products/product-28.png', false, NOW());
