package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order processPayment(Long orderId, boolean success) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (success) {
            order.setPaymentStatus("SUCCESS");
            order.setOrderStatus("CONFIRMED");
        } else {
            order.setPaymentStatus("FAILED");
            order.setOrderStatus("CANCELLED");
        }
        return orderRepository.save(order);
    }
}
