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

    // Helper method to convert Cart -> CartResponse DTO
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(i -> new CartItemResponse(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        BigDecimal.valueOf(i.getPrice()), // convert double -> BigDecimal safely
                        i.getQuantity()))
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                BigDecimal.valueOf(cart.getTotalPrice()), // convert double -> BigDecimal
                items
        );
    }
}
