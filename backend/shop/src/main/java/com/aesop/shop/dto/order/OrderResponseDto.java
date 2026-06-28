package com.aesop.shop.dto.order;

import com.aesop.shop.entity.OrderStatus;
import com.aesop.shop.entity.Orders;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {

    private Long id;
    private Long memberId;
    private OrderStatus status;
    private Integer totalPrice;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String paymentMethod;
    private String deliveryCompany;
    private String trackingNumber;
    private LocalDateTime orderedAt;

    // Entity → DTO 변환
    public OrderResponseDto(Orders orders) {
        this.id = orders.getId();
        this.memberId = orders.getMemberId();
        this.status = orders.getStatus();
        this.totalPrice = orders.getTotalPrice();
        this.receiverName = orders.getReceiverName();
        this.receiverPhone = orders.getReceiverPhone();
        this.receiverAddress = orders.getReceiverAddress();
        this.paymentMethod = orders.getPaymentMethod();
        this.deliveryCompany = orders.getDeliveryCompany();
        this.trackingNumber = orders.getTrackingNumber();
        this.orderedAt = orders.getOrderedAt();
    }
}