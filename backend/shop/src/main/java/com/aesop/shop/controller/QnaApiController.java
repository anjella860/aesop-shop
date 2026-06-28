package com.aesop.shop.controller;

import com.aesop.shop.dto.qna.QnaRequestDto;
import com.aesop.shop.dto.qna.QnaResponseDto;
import com.aesop.shop.service.MemberService;
import com.aesop.shop.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaApiController {

    private final QnaService qnaService;
    private final MemberService memberService;

    // QnA 목록 조회
    @GetMapping("/{productId}")
    public ResponseEntity<List<QnaResponseDto>> getQnaList(
            @PathVariable Long productId) {
        List<QnaResponseDto> list = qnaService.getQnaList(productId)
                .stream()
                .map(QnaResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // QnA 작성
    @PostMapping("/{productId}")
    public ResponseEntity<QnaResponseDto> addQna(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody QnaRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(new QnaResponseDto(
                qnaService.addQna(memberId, productId, dto)));
    }

    // QnA 수정
    @PutMapping("/{id}")
    public ResponseEntity<QnaResponseDto> updateQna(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody QnaRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(new QnaResponseDto(
                qnaService.updateQna(id, memberId, dto)));
    }

    // QnA 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQna(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        qnaService.deleteQna(id, memberId);
        return ResponseEntity.ok("삭제 완료");
    }
}