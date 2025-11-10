package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart c = Cart.builder().user(user).totalPrice(0.0).items(new java.util.ArrayList<>()).build();
            return cartRepository.save(c);
        });
    }

    @Transactional
    public Cart addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStock() < quantity) throw new RuntimeException("Not enough stock");

        Cart cart = getCartForUser(user);

        // try to find existing cart item for same product
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        CartItem item;
        if (existing.isPresent()) {
            item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setPrice(product.getPrice().doubleValue() * item.getQuantity());
        } else {
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice().doubleValue() * quantity)
                    .build();
            cart.getItems().add(item);
        }
        cartItemRepository.save(item);

        double total = cart.getItems().stream().mapToDouble(i -> i.getPrice()).sum();
        cart.setTotalPrice(total);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartItemQuantity(User user, Long cartItemId, int newQuantity) {
        Cart cart = getCartForUser(user);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("CartItem does not belong to user's cart");

        if (newQuantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            Product product = item.getProduct();
            if (product.getStock() < newQuantity) throw new RuntimeException("Not enough stock");
            item.setQuantity(newQuantity);
            item.setPrice(product.getPrice().doubleValue() * newQuantity);
            cartItemRepository.save(item);
        }

        double total = cart.getItems().stream().mapToDouble(i -> i.getPrice()).sum();
        cart.setTotalPrice(total);
        return cartRepository.save(cart);
    }

    @Transactional
    public void removeCartItem(User user, Long cartItemId) {
        Cart cart = getCartForUser(user);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        if (!item.getCart().getId().equals(cart.getId()))
            throw new RuntimeException("CartItem does not belong to user's cart");
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.setTotalPrice(cart.getItems().stream().mapToDouble(i -> i.getPrice()).sum());
        cartRepository.save(cart);
    }
}
