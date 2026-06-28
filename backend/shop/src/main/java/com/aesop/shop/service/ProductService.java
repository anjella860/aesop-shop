package com.aesop.shop.service;

import com.aesop.shop.entity.Product;
import com.aesop.shop.repository.ProductRepository;
import com.aesop.shop.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final String uploadDir = "uploads/products/";

    // 전체 상품 목록
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 카테고리별 상품 목록
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // 상품 상세 조회
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
    }

    // 상품 검색
    public List<Product> search(String name) {
        return productRepository.findByNameContaining(name);
    }

    // 베스트셀러 상품 조회
    public List<Product> findBestsellers() {
        return productRepository.findByIsBestsellerTrue();
    }

    // 상품 등록 (관리자)
    public Product addProduct(Product product, MultipartFile image) throws Exception {
        if (image != null && !image.isEmpty()) {
            String fileName = FileUploadUtil.upload(image, uploadDir);
            product.setImageUrl(fileName);
        }
        return productRepository.save(product);
    }

    // 상품 수정 (관리자)
    public Product updateProduct(Long id, Product updateProduct, MultipartFile image) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
        product.setName(updateProduct.getName());
        product.setDescription(updateProduct.getDescription());
        product.setPrice(updateProduct.getPrice());
        product.setStock(updateProduct.getStock());
        product.setCategoryId(updateProduct.getCategoryId());
        product.setVolume(updateProduct.getVolume());
        product.setSkinType(updateProduct.getSkinType());
        product.setIngredients(updateProduct.getIngredients());
        product.setIsBestseller(updateProduct.getIsBestseller());
        if (image != null && !image.isEmpty()) {
            String fileName = FileUploadUtil.upload(image, uploadDir);
            product.setImageUrl(fileName);
        }
        return productRepository.save(product);
    }

    // 상품 삭제 (관리자)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 연관 상품 추천 (같은 카테고리 4건)
    public List<Product> findRelatedProducts(Long categoryId, Long excludeId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .filter(p -> !p.getId().equals(excludeId))
                .limit(4)
                .toList();
    }
}