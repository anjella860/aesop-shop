package com.aesop.shop.service;

import com.aesop.shop.dto.notice.NoticeRequestDto;
import com.aesop.shop.entity.Notice;
import com.aesop.shop.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지사항 목록 조회
    public List<Notice> findAll() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    // 공지사항 상세 조회
    public Notice findById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));
        notice.setHits(notice.getHits() + 1); // 조회수 증가
        return noticeRepository.save(notice);
    }

    // 공지사항 등록 (관리자)
    public Notice addNotice(NoticeRequestDto dto) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .build();
        return noticeRepository.save(notice);
    }

    // 공지사항 수정 (관리자)
    public Notice updateNotice(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setAuthor(dto.getAuthor());
        return noticeRepository.save(notice);
    }

    // 공지사항 삭제 (관리자)
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}
