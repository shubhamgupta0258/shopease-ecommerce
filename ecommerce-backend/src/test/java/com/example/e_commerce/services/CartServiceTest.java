package com.example.e_commerce.services;

import com.example.e_commerce.dto.CartDTO;
import com.example.e_commerce.models.Cart;
import com.example.e_commerce.models.CartItem;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.CartRepository;
import com.example.e_commerce.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    private CartService cartService;

    private User user;
    private Product laptop;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, productRepository);

        user = new User();
        user.setId(1L);
        user.setEmail("shopper@example.com");

        laptop = new Product();
        laptop.setId(10L);
        laptop.setName("Laptop Pro");
        laptop.setPrice(1499.99);
    }

    @Test
    void addToCart_noExistingCart_createsOneWithQuantity1() {
        when(cartRepository.findByUserId(1L)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        cartService.addToCart(user, List.of(laptop));

        verify(cartRepository).save(argThat(cart ->
                cart.getItems().size() == 1
                        && cart.getItems().get(0).getProduct().getId().equals(10L)
                        && cart.getItems().get(0).getQuantity() == 1
        ));
    }

    @Test
    void addToCart_productAlreadyInCart_incrementsQuantityInsteadOfDuplicating() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(laptop);
        existingItem.setQuantity(1);

        Cart existingCart = new Cart();
        existingCart.setUser(user);
        existingCart.setItems(new ArrayList<>(List.of(existingItem)));

        when(cartRepository.findByUserId(1L)).thenReturn(existingCart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        cartService.addToCart(user, List.of(laptop));

        assertThat(existingCart.getItems()).hasSize(1);
        assertThat(existingCart.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void removeProductFromCart_quantityGreaterThan1_decrementsWithoutRemoving() {
        CartItem item = new CartItem();
        item.setProduct(laptop);
        item.setQuantity(3);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        boolean result = cartService.removeProductFromCart(1L, 10L);

        assertThat(result).isTrue();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void removeProductFromCart_quantityIs1_removesItemEntirely() {
        CartItem item = new CartItem();
        item.setProduct(laptop);
        item.setQuantity(1);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        boolean result = cartService.removeProductFromCart(1L, 10L);

        assertThat(result).isTrue();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void removeProductFromCart_noCart_returnsFalse() {
        when(cartRepository.findByUserId(1L)).thenReturn(null);

        boolean result = cartService.removeProductFromCart(1L, 10L);

        assertThat(result).isFalse();
        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeProductFromCart_productNotInCart_returnsFalse() {
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        boolean result = cartService.removeProductFromCart(1L, 999L);

        assertThat(result).isFalse();
    }

    @Test
    void removeProductFromAllCarts_onlySavesCartsThatActuallyChanged() {
        CartItem itemWithLaptop = new CartItem();
        itemWithLaptop.setProduct(laptop);
        itemWithLaptop.setQuantity(1);
        Cart cartContainingProduct = new Cart();
        cartContainingProduct.setItems(new ArrayList<>(List.of(itemWithLaptop)));

        Product otherProduct = new Product();
        otherProduct.setId(20L);
        CartItem itemWithOther = new CartItem();
        itemWithOther.setProduct(otherProduct);
        itemWithOther.setQuantity(1);
        Cart unrelatedCart = new Cart();
        unrelatedCart.setItems(new ArrayList<>(List.of(itemWithOther)));

        when(cartRepository.findAll()).thenReturn(List.of(cartContainingProduct, unrelatedCart));

        cartService.removeProductFromAllCarts(10L);

        assertThat(cartContainingProduct.getItems()).isEmpty();
        assertThat(unrelatedCart.getItems()).hasSize(1);
        verify(cartRepository, times(1)).save(cartContainingProduct);
        verify(cartRepository, never()).save(unrelatedCart);
    }

    @Test
    void convertToDTO_computesTotalPriceAsSumOfPriceTimesQuantity() {
        CartItem item1 = new CartItem();
        item1.setProduct(laptop); // price 1499.99
        item1.setQuantity(2);

        Product mouse = new Product();
        mouse.setId(11L);
        mouse.setName("Mouse");
        mouse.setPrice(500.0);
        CartItem item2 = new CartItem();
        item2.setProduct(mouse);
        item2.setQuantity(1);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(List.of(item1, item2));

        CartDTO dto = cartService.convertToDTO(cart);

        assertThat(dto.getTotalPrice()).isEqualTo(1499.99 * 2 + 500.0);
        assertThat(dto.getProducts()).hasSize(2);
    }
}
