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

    // ?꾩껜 ?곹뭹 紐⑸줉
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        List<ProductResponseDto> list = productService.findAll()
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 移댄뀒怨좊━蹂??곹뭹 紐⑸줉
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(
            @PathVariable Long categoryId) {
        List<ProductResponseDto> list = productService.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // ?곹뭹 ?곸꽭 議고쉶
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(new ProductResponseDto(productService.findById(id)));
    }

    // ?곹뭹 寃??
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> search(@RequestParam String name) {
        List<ProductResponseDto> list = productService.search(name)
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 踰좎뒪?몄????곹뭹
    @GetMapping({"/best", "/bestsellers"})
    public ResponseEntity<List<ProductResponseDto>> getBestsellers() {
        List<ProductResponseDto> list = productService.findBestsellers()
                .stream()
                .map(ProductResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // ?곌? ?곹뭹 異붿쿇
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
