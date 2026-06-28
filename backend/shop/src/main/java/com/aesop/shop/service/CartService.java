package com.aesop.shop.service;

import com.aesop.shop.entity.Cart;
import com.aesop.shop.entity.Product;
import com.aesop.shop.repository.CartRepository;
import com.aesop.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // 장바구니 목록 조회
    public List<Cart> getCartList(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    // 장바구니 담기
    public Cart addCart(Long memberId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
        if (product.getStock() < quantity) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        Optional<Cart> existCart = cartRepository.findByMemberIdAndProductId(memberId, productId);
        if (existCart.isPresent()) {
            Cart cart = existCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }
        Cart cart = Cart.builder()
                .memberId(memberId)
                .productId(productId)
                .quantity(quantity)
                .build();
        return cartRepository.save(cart);
    }

    // 수량 변경
    public Cart updateCart(Long cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("장바구니 항목이 존재하지 않습니다."));
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    // 장바구니 삭제
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    // 장바구니 전체 삭제
    public void deleteAllCart(Long memberId) {
        cartRepository.deleteByMemberId(memberId);
    }
}