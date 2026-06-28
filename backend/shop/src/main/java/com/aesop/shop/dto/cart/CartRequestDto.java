package com.aesop.shop.dto.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDto {

    private Long productId;
    private Integer quantity;
}