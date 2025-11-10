package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(User user, String paymentMode) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) throw new RuntimeException("Cart is empty");

        // verify stock first
        for (CartItem ci : cart.getItems()) {
            Product p = productRepository.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (p.getStock() < ci.getQuantity()) throw new RuntimeException("Insufficient stock for product: " + p.getName());
        }

        Order order = Order.builder()
                .user(user)
                .totalAmount(cart.getTotalPrice())
                .orderDate(LocalDateTime.now())
                .paymentStatus("PENDING")
                .orderStatus("PLACED")
                .build();
        order = orderRepository.save(order);

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(ci.getProduct())
                    .quantity(ci.getQuantity())
                    .price(ci.getPrice())
                    .build();
            orderItemRepository.save(oi);

            // decrement stock
            Product p = productRepository.findById(ci.getProduct().getId()).get();
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);
        }

        // clear cart
        cartItemRepository.deleteAll(cart.getItems());
        cart.setItems(new java.util.ArrayList<>());
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return order;
    }

    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
