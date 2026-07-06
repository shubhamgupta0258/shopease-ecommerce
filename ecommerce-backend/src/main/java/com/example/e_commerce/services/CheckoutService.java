package com.example.e_commerce.services;

import com.example.e_commerce.dto.OrderDTO;
import com.example.e_commerce.models.Cart;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.CartRepository;
import com.example.e_commerce.repositories.OrderRepository;
import com.example.e_commerce.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public CheckoutService(UserRepository userRepository,
                           CartRepository cartRepository,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    public OrderDTO placeOrder(String email, String razorpayOrderId, String razorpayPaymentId) {

        // 1️⃣ Get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Get cart
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3️⃣ Create order
        Order order = new Order(
                LocalDateTime.now(),
                cart.getItems().stream()
                        .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                        .sum(),   // same as CartDTO totalPrice
                user,
                cart.getItems().stream()
                        .map(i -> i.getProduct())
                        .toList()
        );
        order.setRazorpayOrderId(razorpayOrderId);
        order.setRazorpayPaymentId(razorpayPaymentId);

        Order savedOrder = orderRepository.save(order);

        // 4️⃣ Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return convertToDTO(savedOrder);
    }

    private OrderDTO convertToDTO(Order order) {
        List<String> productNames = order.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getUser().getUsername(),
                productNames
        );
    }
}
