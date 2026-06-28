package com.aesop.shop.dto.qna;

import com.aesop.shop.entity.Qna;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QnaResponseDto {

    private Long id;
    private Long memberId;
    private Long productId;
    private String title;
    private String content;
    private String answer;
    private Boolean isAnswered;
    private LocalDateTime createdAt;

    // Entity → DTO 변환
    public QnaResponseDto(Qna qna) {
        this.id = qna.getId();
        this.memberId = qna.getMemberId();
        this.productId = qna.getProductId();
        this.title = qna.getTitle();
        this.content = qna.getContent();
        this.answer = qna.getAnswer();
        this.isAnswered = qna.getIsAnswered();
        this.createdAt = qna.getCreatedAt();
    }
}