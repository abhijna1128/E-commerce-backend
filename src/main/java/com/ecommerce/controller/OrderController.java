package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@AuthenticationPrincipal User user,
                                          @RequestParam String paymentMode) {
        return ResponseEntity.ok(orderService.placeOrder(user, paymentMode));
    }

    @GetMapping
    public ResponseEntity<List<Order>> orders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getOrdersForUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
