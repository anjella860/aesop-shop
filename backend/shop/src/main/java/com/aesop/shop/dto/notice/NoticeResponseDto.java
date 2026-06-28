package com.aesop.shop.dto.notice;

import com.aesop.shop.entity.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String attachment;
    private Integer hits;
    private LocalDateTime createdAt;

    // Entity → DTO 변환
    public NoticeResponseDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.author = notice.getAuthor();
        this.attachment = notice.getAttachment();
        this.hits = notice.getHits();
        this.createdAt = notice.getCreatedAt();
    }
}
