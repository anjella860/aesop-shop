package com.aesop.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 용량 (예: 100ml)
    @Column(length = 100)
    private String volume;

    // 피부타입 (예: 건성, 지성, 복합성)
    @Column(name = "skin_type", length = 100)
    private String skinType;

    // 주요 성분
    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // 베스트셀러 여부
    @Column(name = "is_bestseller", nullable = false)
    private Boolean isBestseller = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isBestseller == null) {
            this.isBestseller = false;
        }
    }
}