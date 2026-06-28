package com.aesop.shop.service;

import com.aeshop.shop.entity.*;
import com.aeshop.shop.repository.*;
import com.aesop.shop.entity.*;
import com.aesop.shop.repository.CartRepository;
import com.aesop.shop.repository.OrderItemRepository;
import com.aesop.shop.repository.OrdersRepository;
import com.aesop.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // 주문하기
    @Transactional
    public Orders createOrder(Long memberId, String receiverName,
                              String receiverPhone, String receiverAddress,
                              String paymentMethod) {

        // 장바구니 목록 조회
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        if (cartList.isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        // 총 금액 계산
        int totalPrice = 0;
        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException(product.getName() + " 재고가 부족합니다.");
            }
            totalPrice += product.getPrice() * cart.getQuantity();
        }

        // 주문 생성
        Orders order = Orders.builder()
                .memberId(memberId)
                .totalPrice(totalPrice)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .paymentMethod(paymentMethod)
                .status(OrderStatus.PENDING)
                .build();
        ordersRepository.save(order);

        // 주문 상세 생성 + 재고 차감
        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProductId()).get();
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(cart.getProductId())
                    .quantity(cart.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
            // 재고 차감
            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);
        }

        // 장바구니 비우기
        cartRepository.deleteByMemberId(memberId);

        return order;
    }

    // 내 주문 목록 조회
    public List<Orders> findByMemberId(Long memberId) {
        return ordersRepository.findByMemberIdOrderByOrderedAtDesc(memberId);
    }

    // 주문 상세 조회
    public Orders findById(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
    }

    // 주문 취소
    public void cancelOrder(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("배송 중인 주문은 취소할 수 없습니다.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        ordersRepository.save(order);
    }

    // 전체 주문 목록 (관리자)
    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    // 주문 상태 변경 (관리자)
    public void changeStatus(Long id, OrderStatus status) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
        order.setStatus(status);
        ordersRepository.save(order);
    }
}