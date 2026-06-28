package com.aesop.shop.repository;

import com.aesop.shop.entity.Payment;
import com.aesop.shop.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 주문별 결제 정보 조회 (1:1)
    Optional<Payment> findByOrderId(Long orderId);

    // 결제 상태별 조회
    List<Payment> findByStatus(PaymentStatus status);

    // PG사 거래 ID로 조회
    Optional<Payment> findByTid(String tid);
}