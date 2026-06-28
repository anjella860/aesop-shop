package com.aesop.shop.dto.cart;

import com.aesop.shop.entity.Cart;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CartResponseDto {

    private Long id;
    private Long memberId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;

    // Entity → DTO 변환
    public CartResponseDto(Cart cart) {
        this.id = cart.getId();
        this.memberId = cart.getMemberId();
        this.productId = cart.getProductId();
        this.quantity = cart.getQuantity();
        this.createdAt = cart.getCreatedAt();
    }
}