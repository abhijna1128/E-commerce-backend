package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @PostMapping("/process")
    public ResponseEntity<Order> process(@RequestParam Long orderId, @RequestParam boolean success) {
        return ResponseEntity.ok(paymentService.processPayment(orderId, success));
    }
}
