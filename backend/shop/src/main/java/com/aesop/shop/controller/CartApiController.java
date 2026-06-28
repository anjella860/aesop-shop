package com.aesop.shop.controller;

import com.aesop.shop.dto.cart.CartRequestDto;
import com.aesop.shop.dto.cart.CartResponseDto;
import com.aesop.shop.service.CartService;
import com.aesop.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;
    private final MemberService memberService;

    // 장바구니 목록 조회
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        List<CartResponseDto> list = cartService.getCartList(memberId)
                .stream()
                .map(CartResponseDto::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 장바구니 담기
    @PostMapping
    public ResponseEntity<CartResponseDto> addCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(new CartResponseDto(
                cartService.addCart(memberId, dto.getProductId(), dto.getQuantity())));
    }

    // 수량 변경
    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDto> updateCart(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(new CartResponseDto(cartService.updateCart(id, quantity)));
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok("삭제 완료");
    }

    // 장바구니 전체 삭제
    @DeleteMapping
    public ResponseEntity<String> deleteAllCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        cartService.deleteAllCart(memberId);
        return ResponseEntity.ok("전체 삭제 완료");
    }
}