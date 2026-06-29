package com.aesop.shop.controller;

import com.aesop.shop.entity.Payment;
import com.aesop.shop.service.MemberService;
import com.aesop.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;
    private final MemberService memberService;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        try {
            Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
            String paymentKey = (String) body.get("paymentKey");
            String orderId = String.valueOf(body.get("orderId"));
            Integer amount = Integer.valueOf(String.valueOf(body.get("amount")));

            Payment payment = paymentService.confirmPayment(memberId, paymentKey, orderId, amount);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "paymentId", payment.getId(),
                    "orderId", payment.getOrderId(),
                    "status", payment.getStatus().name(),
                    "amount", payment.getAmount(),
                    "method", payment.getMethod(),
                    "paidAt", payment.getPaidAt() != null ? payment.getPaidAt().toString() : ""
            ));
        } catch (Exception e) {
            log.error("Payment confirmation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/fail")
    public ResponseEntity<?> failPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        try {
            Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
            String orderId = String.valueOf(body.get("orderId"));
            String errorCode = (String) body.get("errorCode");
            String errorMessage = (String) body.get("errorMessage");

            paymentService.failPayment(memberId, orderId, errorCode, errorMessage);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        try {
            Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
            Payment payment = paymentService.findByOrderId(memberId, orderId);
            return ResponseEntity.ok(Map.of(
                    "id", payment.getId(),
                    "orderId", payment.getOrderId(),
                    "method", payment.getMethod(),
                    "status", payment.getStatus().name(),
                    "amount", payment.getAmount(),
                    "tid", payment.getTid() != null ? payment.getTid() : "",
                    "paidAt", payment.getPaidAt() != null ? payment.getPaidAt().toString() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
