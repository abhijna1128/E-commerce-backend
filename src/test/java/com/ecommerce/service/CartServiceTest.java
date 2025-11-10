package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private CartService cartService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void testAddToCart() {
        Cart cart = cartService.addToCart(user, 1L, 2);
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(BigDecimal.valueOf(200), cart.getTotalPrice());
    }
}
