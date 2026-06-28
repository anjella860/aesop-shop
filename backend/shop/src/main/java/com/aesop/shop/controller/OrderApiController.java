package com.aesop.shop.controller;

import com.aesop.shop.dto.order.OrderRequestDto;
import com.aesop.shop.dto.order.OrderResponseDto;
import com.aesop.shop.entity.Orders;
import com.aesop.shop.service.MemberService;
import com.aesop.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        List<OrderResponseDto> list = orderService.findByMemberId(memberId)
                .stream()
                .map(OrderResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(new OrderResponseDto(
                orderService.findByIdAndMemberId(id, memberId)));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        Orders order = orderService.createOrder(
                memberId,
                dto.getReceiverName(),
                dto.getReceiverPhone(),
                dto.getReceiverAddress(),
                dto.getPaymentMethod());
        return ResponseEntity.ok(new OrderResponseDto(order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        orderService.cancelOrder(id, memberId);
        return ResponseEntity.ok("Cancelled");
    }
}
