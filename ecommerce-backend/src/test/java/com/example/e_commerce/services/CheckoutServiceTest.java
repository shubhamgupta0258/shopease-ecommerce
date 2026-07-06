package com.example.e_commerce.services;

import com.example.e_commerce.dto.OrderDTO;
import com.example.e_commerce.models.Cart;
import com.example.e_commerce.models.CartItem;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.CartRepository;
import com.example.e_commerce.repositories.OrderRepository;
import com.example.e_commerce.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    private CheckoutService checkoutService;

    private User user;
    private Product laptop;

    @BeforeEach
    void setUp() {
        checkoutService = new CheckoutService(userRepository, cartRepository, orderRepository);

        user = new User();
        user.setId(1L);
        user.setEmail("shopper@example.com");

        laptop = new Product();
        laptop.setId(10L);
        laptop.setName("Laptop Pro");
        laptop.setPrice(1499.99);
    }

    @Test
    void placeOrder_userNotFound_throws() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkoutService.placeOrder("ghost@example.com", "order_1", "pay_1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void placeOrder_noCart_throwsCartIsEmpty() {
        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(null);

        assertThatThrownBy(() -> checkoutService.placeOrder("shopper@example.com", "order_1", "pay_1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    void placeOrder_emptyCart_throwsCartIsEmpty() {
        Cart emptyCart = new Cart();
        emptyCart.setItems(new ArrayList<>());

        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(emptyCart);

        assertThatThrownBy(() -> checkoutService.placeOrder("shopper@example.com", "order_1", "pay_1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    void placeOrder_success_computesTotalStoresPaymentRefsAndClearsCart() {
        CartItem item = new CartItem();
        item.setProduct(laptop);
        item.setQuantity(2);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(item)));

        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderDTO dto = checkoutService.placeOrder("shopper@example.com", "order_abc", "pay_xyz");

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getTotalAmount()).isEqualTo(1499.99 * 2);
        assertThat(dto.getProductNames()).containsExactly("Laptop Pro");
        assertThat(dto.getUsername()).isEqualTo("shopper@example.com");

        // cart must be cleared and persisted after the order is placed
        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);

        // payment references must be stored on the order that gets saved
        verify(orderRepository).save(argThat(o ->
                "order_abc".equals(o.getRazorpayOrderId()) && "pay_xyz".equals(o.getRazorpayPaymentId())
        ));
    }
}
