package com.aesop.shop.repository;

import com.aesop.shop.entity.Orders;
import com.aesop.shop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 회원의 주문 목록 조회
    List<Orders> findByMemberId(Long memberId);

    // 주문 상태별 조회 (관리자)
    List<Orders> findByStatus(OrderStatus status);

    // 회원의 주문 목록 최신순 조회
    List<Orders> findByMemberIdOrderByOrderedAtDesc(Long memberId);
}