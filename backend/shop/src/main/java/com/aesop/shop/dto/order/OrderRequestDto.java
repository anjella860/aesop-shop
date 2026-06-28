package com.aesop.shop.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String paymentMethod;
}