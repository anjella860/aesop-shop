package com.aesop.shop.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String paymentMethod;
    private List<OrderItemRequestDto> items;

    @Getter
    @Setter
    public static class OrderItemRequestDto {
        private Long productId;
        private Integer quantity;
    }
}