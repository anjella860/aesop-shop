package com.aesop.shop.service;

import com.aesop.shop.entity.Orders;
import com.aesop.shop.entity.Payment;
import com.aesop.shop.entity.PaymentStatus;
import com.aesop.shop.repository.OrdersRepository;
import com.aesop.shop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrdersRepository ordersRepository;
    private final OrderService orderService;
    private final RestTemplate restTemplate;

    @Value("${toss.secret-key}")
    private String secretKey;

    private static final String TOSS_CONFIRM_URL =
            "https://api.tosspayments.com/v1/payments/confirm";

    @Transactional
    public Payment confirmPayment(Long memberId, String paymentKey, String orderId, Integer amount) {
        Long orderIdLong = parseOrderId(orderId);
        Orders order = ordersRepository.findByIdAndMemberId(orderIdLong, memberId)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        if (!order.getTotalPrice().equals(amount)) {
            throw new RuntimeException("Payment amount does not match order amount.");
        }

        Payment existing = paymentRepository.findByOrderId(orderIdLong).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.COMPLETED) {
            return existing;
        }

        HttpHeaders headers = new HttpHeaders();
        String encoded = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                TOSS_CONFIRM_URL, request, Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Toss payment confirmation failed.");
        }

        Map<String, Object> tossResponse = response.getBody();
        String method = tossResponse != null ? (String) tossResponse.get("method") : "CARD";

        Payment payment = existing != null ? existing : Payment.builder().orderId(orderIdLong).build();
        payment.setMethod(method != null ? method : "CARD");
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setAmount(amount);
        payment.setTid(paymentKey);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        orderService.confirmPaidOrder(orderIdLong, memberId);
        return payment;
    }

    @Transactional
    public void failPayment(Long memberId, String orderId, String errorCode, String errorMessage) {
        Long orderIdLong = parseOrderId(orderId);
        ordersRepository.findByIdAndMemberId(orderIdLong, memberId)
                .orElseThrow(() -> new RuntimeException("Order not found."));

        log.warn("Payment failed - orderId: {}, code: {}, message: {}",
                orderId, errorCode, errorMessage);

        Payment existing = paymentRepository.findByOrderId(orderIdLong).orElse(null);
        Payment payment = existing != null ? existing : Payment.builder().orderId(orderIdLong).build();
        payment.setMethod("UNKNOWN");
        payment.setStatus(PaymentStatus.FAILED);
        payment.setAmount(0);
        paymentRepository.save(payment);
    }

    private Long parseOrderId(String orderId) {
        String normalized = orderId;
        int separatorIndex = orderId.indexOf("-");
        if (separatorIndex > 0) {
            normalized = orderId.substring(0, separatorIndex);
        }
        return Long.parseLong(normalized);
    }

    public Payment findByOrderId(Long memberId, Long orderId) {
        ordersRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new RuntimeException("Order not found."));
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found."));
    }
}
