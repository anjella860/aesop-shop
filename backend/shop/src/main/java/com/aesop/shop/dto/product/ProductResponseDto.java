package com.aesop.shop.dto.product;

import com.aesop.shop.entity.Product;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductResponseDto {

    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String volume;
    private String skinType;
    private String ingredients;
    private Integer price;
    private Integer stock;
    private String imageUrl;
    private Boolean isBestseller;
    private LocalDateTime createdAt;

    // Entity → DTO 변환
    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.categoryId = product.getCategoryId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.volume = product.getVolume();
        this.skinType = product.getSkinType();
        this.ingredients = product.getIngredients();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.imageUrl = product.getImageUrl();
        this.isBestseller = product.getIsBestseller();
        this.createdAt = product.getCreatedAt();
    }
}