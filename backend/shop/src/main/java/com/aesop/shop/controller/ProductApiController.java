package com.aesop.shop.controller;

import com.aesop.shop.dto.product.ProductResponseDto;
import com.aesop.shop.entity.Product;
import com.aesop.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    // 전체 상품 목록
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        List<ProductResponseDto> list = productService.findAll()
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 카테고리별 상품 목록
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(
            @PathVariable Long categoryId) {
        List<ProductResponseDto> list = productService.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(new ProductResponseDto(productService.findById(id)));
    }

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> search(@RequestParam String name) {
        List<ProductResponseDto> list = productService.search(name)
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 베스트셀러 상품
    @GetMapping("/best")
    public ResponseEntity<List<ProductResponseDto>> getBestsellers() {
        List<ProductResponseDto> list = productService.findBestsellers()
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 연관 상품 추천
    @GetMapping("/{id}/related")
    public ResponseEntity<List<ProductResponseDto>> getRelatedProducts(
            @PathVariable Long id) {
        Product product = productService.findById(id);
        List<ProductResponseDto> list = productService
                .findRelatedProducts(product.getCategoryId(), id)
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }
}