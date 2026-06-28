package com.aesop.shop.service;

import com.aesop.shop.dto.review.ReviewRequestDto;
import com.aesop.shop.entity.OrderItem;
import com.aesop.shop.entity.OrderStatus;
import com.aesop.shop.entity.Orders;
import com.aesop.shop.entity.Review;
import com.aesop.shop.repository.OrderItemRepository;
import com.aesop.shop.repository.OrdersRepository;
import com.aesop.shop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;

    // 리뷰 개수 확인 (비회원 가능)
    public int getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    // 리뷰 목록 조회
    public List<Review> getReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    // 관리자 리뷰 목록
    public List<Review> findAllForAdmin() {
        return reviewRepository.findAll();
    }



    // 리뷰 작성
    public Review addReview(Long memberId, Long productId, ReviewRequestDto dto) {
        if (!hasPurchasedProduct(memberId, productId)) {
            throw new RuntimeException("구매한 상품만 리뷰를 작성할 수 있습니다.");
        }
        if (reviewRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new RuntimeException("이미 리뷰를 작성한 상품입니다.");
        }
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

    private boolean hasPurchasedProduct(Long memberId, Long productId) {
        List<Orders> orders = ordersRepository.findByMemberId(memberId);
        for (Orders order : orders) {
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            for (OrderItem item : orderItemRepository.findByOrderId(order.getId())) {
                if (item.getProductId().equals(productId)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 관리자 리뷰 삭제
    public void deleteReviewByAdmin(Long id) {
        reviewRepository.deleteById(id);
    }
}
