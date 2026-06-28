package com.aesop.shop.service;

import com.aesop.shop.dto.review.ReviewRequestDto;
import com.aesop.shop.entity.Review;
import com.aesop.shop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 개수 확인 (비회원 가능)
    public int getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    // 리뷰 목록 조회
    public List<Review> getReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // 리뷰 작성
    public Review addReview(Long memberId, Long productId, ReviewRequestDto dto) {
        Review review = Review.builder()
                .memberId(memberId)
                .productId(productId)
                .rating(dto.getRating())
                .content(dto.getContent())
                .build();
        return reviewRepository.save(review);
    }

    // 리뷰 수정
    public Review updateReview(Long id, Long memberId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));
        if (!review.getMemberId().equals(memberId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        return reviewRepository.save(review);
    }

    // 리뷰 삭제
    public void deleteReview(Long id, Long memberId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));
        if (!review.getMemberId().equals(memberId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        reviewRepository.deleteById(id);
    }

    // 관리자 리뷰 삭제
    public void deleteReviewByAdmin(Long id) {
        reviewRepository.deleteById(id);
    }
}