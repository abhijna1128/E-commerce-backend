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

    /**
     * Process payment for an order.
     *
     * @param orderId ID of the order
     * @param success true if payment succeeded, false if failed
     * @return updated Order
     */
    public Order processPayment(Long orderId, boolean success) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (success) {
            order.setStatus("CONFIRMED"); // payment succeeded
        } else {
            order.setStatus("CANCELLED"); // payment failed
        }

        return orderRepository.save(order);
    }
}
