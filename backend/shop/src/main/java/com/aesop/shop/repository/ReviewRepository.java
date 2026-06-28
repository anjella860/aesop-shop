package com.aesop.shop.repository;

import com.aesop.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 상품별 리뷰 목록 조회
    List<Review> findByProductId(Long productId);

    // 회원이 작성한 리뷰 목록 조회
    List<Review> findByMemberId(Long memberId);

    // 상품별 리뷰 개수 조회 (비회원도 확인 가능)
    int countByProductId(Long productId);

    // 회원이 특정 상품에 리뷰 작성했는지 확인
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}