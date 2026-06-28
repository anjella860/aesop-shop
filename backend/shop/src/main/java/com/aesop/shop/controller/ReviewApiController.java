package com.aesop.shop.controller;

import com.aesop.shop.dto.review.ReviewRequestDto;
import com.aesop.shop.dto.review.ReviewResponseDto;
import com.aesop.shop.service.MemberService;
import com.aesop.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    // 리뷰 개수 확인 (비회원 가능)
    @GetMapping("/{productId}/count")
    public ResponseEntity<Integer> getReviewCount(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewCount(productId));
    }

    // 리뷰 목록 조회 (로그인 필요)
    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(
            @PathVariable Long productId) {
        List<ReviewResponseDto> list = reviewService.getReviews(productId)
                .stream()
                .map(ReviewResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 리뷰 작성
    @PostMapping("/{productId}")
    public ResponseEntity<ReviewResponseDto> addReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReviewRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(new ReviewResponseDto(
                reviewService.addReview(memberId, productId, dto)));
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReviewRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(new ReviewResponseDto(
                reviewService.updateReview(id, memberId, dto)));
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        reviewService.deleteReview(id, memberId);
        return ResponseEntity.ok("리뷰 삭제 완료");
    }
}