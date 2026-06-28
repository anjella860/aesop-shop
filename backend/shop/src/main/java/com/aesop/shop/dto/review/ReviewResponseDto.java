package com.aesop.shop.dto.review;

import com.aesop.shop.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private Long id;
    private Long memberId;
    private Long productId;
    private Integer rating;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    // Entity → DTO 변환
    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.memberId = review.getMemberId();
        this.productId = review.getProductId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.imageUrl = review.getImageUrl();
        this.createdAt = review.getCreatedAt();
    }
}