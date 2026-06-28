package com.aesop.shop.repository;

import com.aesop.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품명 키워드 검색
    List<Product> findByNameContaining(String name);

    // 카테고리별 상품 조회
    List<Product> findByCategoryId(Long categoryId);

    // 베스트셀러 상품 조회
    List<Product> findByIsBestsellerTrue();
}
