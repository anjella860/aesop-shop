package com.aesop.shop.repository;

import com.aesop.shop.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // 상품별 QnA 목록 조회
    List<Qna> findByProductId(Long productId);

    // 회원이 작성한 QnA 목록 조회
    List<Qna> findByMemberId(Long memberId);

    // 답변 여부별 조회 (관리자)
    List<Qna> findByIsAnswered(Boolean isAnswered);

    // 상품별 QnA 최신순 조회
    List<Qna> findByProductIdOrderByCreatedAtDesc(Long productId);

    // 전체 QnA 최신순 조회 (관리자)
    List<Qna> findAllByOrderByCreatedAtDesc();
}
