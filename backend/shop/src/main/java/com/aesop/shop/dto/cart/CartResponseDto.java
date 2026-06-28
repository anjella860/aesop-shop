package com.aesop.shop.dto.cart;

import com.aesop.shop.entity.Cart;
import com.aesop.shop.entity.Product;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CartResponseDto {

    private Long id;
    private Long memberId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private String name;
    private Integer price;
    private String imageUrl;

    public CartResponseDto(Cart cart) {
        this.id = cart.getId();
        this.memberId = cart.getMemberId();
        this.productId = cart.getProductId();
        this.quantity = cart.getQuantity();
        this.createdAt = cart.getCreatedAt();
    }

    public CartResponseDto(Cart cart, Product product) {
        this(cart);
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
    }
}
