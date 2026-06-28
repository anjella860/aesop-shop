package com.aesop.shop.controller;

import com.aesop.shop.dto.cart.CartRequestDto;
import com.aesop.shop.dto.cart.CartResponseDto;
import com.aesop.shop.entity.Cart;
import com.aesop.shop.entity.Product;
import com.aesop.shop.service.CartService;
import com.aesop.shop.service.MemberService;
import com.aesop.shop.service.ProductService;
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
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        List<CartResponseDto> list = cartService.getCartList(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<CartResponseDto> addCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(toResponse(
                cartService.addCart(memberId, dto.getProductId(), dto.getQuantity())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponseDto> updateCart(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CartRequestDto dto) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(toResponse(
                cartService.updateCart(memberId, id, dto.getQuantity())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCart(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        cartService.deleteCart(memberId, id);
        return ResponseEntity.ok("Deleted");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = memberService.findByEmail(userDetails.getUsername()).getId();
        cartService.deleteAllCart(memberId);
        return ResponseEntity.ok("Deleted all");
    }

    private CartResponseDto toResponse(Cart cart) {
        Product product = productService.findById(cart.getProductId());
        return new CartResponseDto(cart, product);
    }
}
