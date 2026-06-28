package com.aesop.shop.controller;

import com.aesop.shop.dto.notice.NoticeResponseDto;
import com.aesop.shop.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회 (비회원 가능)
    @GetMapping
    public ResponseEntity<List<NoticeResponseDto>> getNotices() {
        List<NoticeResponseDto> list = noticeService.findAll()
                .stream()
                .map(NoticeResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 공지사항 상세 조회 (비회원 가능)
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long id) {
        return ResponseEntity.ok(new NoticeResponseDto(noticeService.findById(id)));
    }
}