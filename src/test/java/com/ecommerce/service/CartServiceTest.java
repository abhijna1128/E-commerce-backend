package com.ecommerce.service;

import java.math.BigDecimal;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Abhijna");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.0")); // Price 100 for simplicity
        product.setStock(10);

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .totalPrice(0.0)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void testGetCartForUser_WhenCartExists() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartForUser(user);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void testGetCartForUser_WhenCartDoesNotExist() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getCartForUser(user);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    void testAddToCart_NewItem() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        // Add 2 quantity
        Cart result = cartService.addToCart(user, 1L, 2);

        assertEquals(1, result.getItems().size());
        assertEquals(200.0, result.getTotalPrice()); // 100 × 2
        assertEquals(2, result.getItems().get(0).getQuantity());
        verify(cartItemRepository, times(1)).save(any());
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    void testUpdateCartItemQuantity() {
        CartItem item = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .price(product.getPrice().doubleValue() * 2) // 100 × 2 = 200
                .cart(cart)
                .build();
        cart.getItems().add(item);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        // Update quantity to 3
        Cart result = cartService.updateCartItemQuantity(user, 1L, 3);

        assertEquals(3, result.getItems().get(0).getQuantity());
        assertEquals(300.0, result.getTotalPrice()); // 100 × 3
    }

    @Test
    void testRemoveCartItem() {
        CartItem item = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .price(product.getPrice().doubleValue() * 2) // 100 × 2 = 200
                .cart(cart)
                .build();
        cart.getItems().add(item);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        cartService.removeCartItem(user, 1L);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalPrice());
        verify(cartItemRepository, times(1)).delete(item);
        verify(cartRepository, times(1)).save(cart);
    }
}
