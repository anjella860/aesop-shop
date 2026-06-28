<template>
  <footer class="footer">
    <div class="container">
      <!-- 상단 -->
      <div class="footer__top">
        <!-- 로고 & 소개 -->
        <div class="footer__brand">
          <RouterLink to="/" class="footer__logo">AESOP</RouterLink>
          <p class="footer__desc">
            자연에서 얻은 성분과 과학적 연구를 바탕으로<br />
            피부와 감각을 위한 제품을 만듭니다.
          </p>
        </div>

        <!-- 링크 -->
        <div class="footer__links">
          <div class="footer__col">
            <h4 class="footer__col-title">제품</h4>
            <ul>
              <li><RouterLink to="/skincare">스킨케어</RouterLink></li>
              <li><RouterLink to="/hand-body">핸드 & 바디</RouterLink></li>
              <li><RouterLink to="/fragrance">향수</RouterLink></li>
              <li><RouterLink to="/hair">헤어</RouterLink></li>
              <li><RouterLink to="/gifts">기프트</RouterLink></li>
            </ul>
          </div>

          <div class="footer__col">
            <h4 class="footer__col-title">고객지원</h4>
            <ul>
              <li><RouterLink to="/qna">문의하기</RouterLink></li>
              <li><RouterLink to="/notice">공지사항</RouterLink></li>
              <li><RouterLink to="/mypage">마이페이지</RouterLink></li>
            </ul>
          </div>

          <div class="footer__col">
            <h4 class="footer__col-title">회원</h4>
            <ul>
              <li><RouterLink to="/login">로그인</RouterLink></li>
              <li><RouterLink to="/signup">회원가입</RouterLink></li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 구분선 -->
      <div class="footer__divider"></div>

      <!-- 하단 -->
      <div class="footer__bottom">
        <p class="footer__copy">© 2026 Aesop Korea. All rights reserved.</p>
        <p class="footer__info">포트폴리오 클론코딩 프로젝트</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: var(--color-olive);
  color: var(--color-border-light);
  padding: 60px 0 30px;
  margin-top: auto;
}

.footer__top {
  display: flex;
  gap: 80px;
  margin-bottom: 40px;
}

/* 브랜드 */
.footer__brand {
  flex-shrink: 0;
}

.footer__logo {
  font-family: var(--font-en);
  font-size: 20px;
  letter-spacing: 6px;
  color: var(--color-gold-light);
  display: block;
  margin-bottom: 20px;
  transition: color 0.3s ease;
}

.footer__logo:hover {
  color: var(--color-gold);
}

.footer__desc {
  font-size: 13px;
  line-height: 1.8;
  color: var(--color-text-light);
  max-width: 240px;
}

/* 링크 */
.footer__links {
  display: flex;
  gap: 60px;
  flex: 1;
}

.footer__col h4.footer__col-title {
  font-family: var(--font-en);
  font-size: 12px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--color-gold-light);
  margin-bottom: 20px;
}

.footer__col ul {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer__col ul li a {
  font-size: 13px;
  color: var(--color-text-light);
  transition: color 0.3s ease;
}

.footer__col ul li a:hover {
  color: var(--color-gold-light);
}

/* 구분선 */
.footer__divider {
  height: 1px;
  background-color: rgba(212, 217, 200, 0.2);
  margin-bottom: 24px;
}

/* 하단 */
.footer__bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer__copy,
.footer__info {
  font-size: 12px;
  color: var(--color-text-light);
  letter-spacing: 0.5px;
}

/* 반응형 */
@media (max-width: 1024px) {
  .footer__top {
    flex-direction: column;
    gap: 40px;
  }

  .footer__links {
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .footer__links {
    flex-wrap: wrap;
    gap: 30px;
  }

  .footer__bottom {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>
