package com.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long id;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;

    public CartResponse(Long id, BigDecimal totalPrice, List<CartItemResponse> items) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
}
