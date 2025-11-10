package com.ecommerce.controller;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.dto.CartResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal User user,
                                                  @PathVariable Long productId,
                                                  @RequestBody CartItemRequest request) {
        Cart cart = cartService.addToCart(user, productId, request.getQuantity());
        return ResponseEntity.ok(mapToCartResponse(cart));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(@AuthenticationPrincipal User user,
                                                   @PathVariable Long cartItemId,
                                                   @RequestBody CartItemRequest request) {
        Cart cart = cartService.updateCartItemQuantity(user, cartItemId, request.getQuantity());
        return ResponseEntity.ok(mapToCartResponse(cart));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(@AuthenticationPrincipal User user,
                                                   @PathVariable Long cartItemId) {
        cartService.removeCartItem(user, cartItemId);
        Cart cart = cartService.getCartForUser(user);
        return ResponseEntity.ok(mapToCartResponse(cart));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal User user) {
        Cart cart = cartService.getCartForUser(user);
        return ResponseEntity.ok(mapToCartResponse(cart));
    }

    // Safe helper method to convert Cart -> CartResponse
    private CartResponse mapToCartResponse(Cart cart) {
        if (cart == null || cart.getItems() == null) {
            return new CartResponse(null, BigDecimal.ZERO, Collections.emptyList());
        }

        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                BigDecimal.valueOf(cart.getTotalPrice()), // convert double -> BigDecimal
                items
        );
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                BigDecimal.valueOf(item.getPrice()), // convert double -> BigDecimal
                item.getQuantity()
        );
    }
}
