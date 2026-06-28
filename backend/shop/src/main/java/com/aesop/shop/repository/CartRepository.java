package com.aesop.shop.repository;

import com.aesop.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 회원의 장바구니 목록 조회
    List<Cart> findByMemberId(Long memberId);

    // 회원의 특정 상품 장바구니 조회 (중복 담기 확인)
    Optional<Cart> findByMemberIdAndProductId(Long memberId, Long productId);

    // 회원의 장바구니 전체 삭제
    void deleteByMemberId(Long memberId);
}