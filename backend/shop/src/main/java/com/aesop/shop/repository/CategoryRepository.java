package com.aesop.shop.repository;

import com.aesop.shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 상위 카테고리별 조회 (대분류면 parentId = null)
    List<Category> findByParentId(Long parentId);

    // 상위 카테고리 목록 (대분류만)
    List<Category> findByParentIdIsNull();
}