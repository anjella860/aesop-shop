package com.aesop.shop.repository;

import com.aesop.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 주문별 상품 목록 조회
    List<OrderItem> findByOrderId(Long orderId);

    // 특정 상품이 포함된 주문 조회
    List<OrderItem> findByProductId(Long productId);
}