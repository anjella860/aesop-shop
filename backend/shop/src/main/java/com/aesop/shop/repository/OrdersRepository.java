package com.aesop.shop.repository;

import com.aesop.shop.entity.OrderStatus;
import com.aesop.shop.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByMemberId(Long memberId);

    Optional<Orders> findByIdAndMemberId(Long id, Long memberId);

    List<Orders> findByStatus(OrderStatus status);

    List<Orders> findByMemberIdOrderByOrderedAtDesc(Long memberId);
}
