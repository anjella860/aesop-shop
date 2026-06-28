package com.aesop.shop.service;

import com.aesop.shop.entity.Cart;
import com.aesop.shop.entity.OrderItem;
import com.aesop.shop.entity.OrderStatus;
import com.aesop.shop.entity.Orders;
import com.aesop.shop.entity.Product;
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

    private static final int FREE_SHIPPING_THRESHOLD = 50000;
    private static final int SHIPPING_FEE = 3000;

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Orders createOrder(Long memberId, String receiverName,
                              String receiverPhone, String receiverAddress,
                              String paymentMethod) {

        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        if (cartList.isEmpty()) {
            throw new RuntimeException("Cart is empty.");
        }

        int productTotal = 0;
        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found."));
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException(product.getName() + " is out of stock.");
            }
            productTotal += product.getPrice() * cart.getQuantity();
        }

        int totalPrice = productTotal + calculateShippingFee(productTotal);
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

        for (Cart cart : cartList) {
            Product product = productRepository.findById(cart.getProductId()).get();
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(cart.getProductId())
                    .quantity(cart.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        if ("무통장입금".equals(paymentMethod)) {
            cartRepository.deleteByMemberId(memberId);
        }

        return order;
    }

    public List<Orders> findByMemberId(Long memberId) {
        return ordersRepository.findByMemberIdOrderByOrderedAtDesc(memberId);
    }

    public Orders findById(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found."));
    }

    public Orders findByIdAndMemberId(Long id, Long memberId) {
        return ordersRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new RuntimeException("Order not found."));
    }

    @Transactional
    public void confirmPaidOrder(Long orderId, Long memberId) {
        Orders order = findByIdAndMemberId(orderId, memberId);
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return;
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be confirmed.");
        }

        deductStock(orderId);

        order.setStatus(OrderStatus.CONFIRMED);
        ordersRepository.save(order);
        cartRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void cancelOrder(Long id, Long memberId) {
        Orders order = findByIdAndMemberId(id, memberId);
        cancel(order);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Orders order = findById(id);
        cancel(order);
    }

    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    @Transactional
    public void changeStatus(Long id, OrderStatus status) {
        Orders order = findById(id);
        if (order.getStatus() == OrderStatus.PENDING && status == OrderStatus.CONFIRMED) {
            deductStock(order.getId());
            cartRepository.deleteByMemberId(order.getMemberId());
        }
        if (order.getStatus() == OrderStatus.CONFIRMED && status == OrderStatus.CANCELLED) {
            restoreStock(order.getId());
        }
        order.setStatus(status);
        ordersRepository.save(order);
    }

    public Orders updateDelivery(Long id, String deliveryCompany, String trackingNumber) {
        Orders order = findById(id);
        order.setDeliveryCompany(deliveryCompany);
        order.setTrackingNumber(trackingNumber);
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.SHIPPED);
        }
        return ordersRepository.save(order);
    }

    private void cancel(Orders order) {
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Order cannot be cancelled.");
        }
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            restoreStock(order.getId());
        }
        order.setStatus(OrderStatus.CANCELLED);
        ordersRepository.save(order);
    }

    private void deductStock(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found."));
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(product.getName() + " is out of stock.");
            }
        }

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).get();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
    }

    private void restoreStock(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).get();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private int calculateShippingFee(int productTotal) {
        return productTotal >= FREE_SHIPPING_THRESHOLD ? 0 : SHIPPING_FEE;
    }
}
