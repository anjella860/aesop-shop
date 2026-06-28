package com.aesop.shop.repository;

import com.aesop.shop.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 제목 키워드 검색
    List<Notice> findByTitleContaining(String title);

    // 최신순 목록 조회
    List<Notice> findAllByOrderByCreatedAtDesc();
}
