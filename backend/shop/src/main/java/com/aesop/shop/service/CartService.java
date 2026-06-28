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

    public List<Cart> getCartList(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    public Cart addCart(Long memberId, Long productId, int quantity) {
        validateQuantity(quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found."));
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock.");
        }
        Optional<Cart> existCart = cartRepository.findByMemberIdAndProductId(memberId, productId);
        if (existCart.isPresent()) {
            Cart cart = existCart.get();
            int nextQuantity = cart.getQuantity() + quantity;
            if (product.getStock() < nextQuantity) {
                throw new RuntimeException("Not enough stock.");
            }
            cart.setQuantity(nextQuantity);
            return cartRepository.save(cart);
        }
        Cart cart = Cart.builder()
                .memberId(memberId)
                .productId(productId)
                .quantity(quantity)
                .build();
        return cartRepository.save(cart);
    }

    public Cart updateCart(Long memberId, Long cartId, int quantity) {
        validateQuantity(quantity);
        Cart cart = cartRepository.findByIdAndMemberId(cartId, memberId)
                .orElseThrow(() -> new RuntimeException("Cart item not found."));
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found."));
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock.");
        }
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    public void deleteCart(Long memberId, Long cartId) {
        Cart cart = cartRepository.findByIdAndMemberId(cartId, memberId)
                .orElseThrow(() -> new RuntimeException("Cart item not found."));
        cartRepository.delete(cart);
    }

    public void deleteAllCart(Long memberId) {
        cartRepository.deleteByMemberId(memberId);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero.");
        }
    }
}
