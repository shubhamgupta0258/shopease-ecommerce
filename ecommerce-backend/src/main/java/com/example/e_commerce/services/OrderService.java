package com.example.e_commerce.services;

import com.example.e_commerce.dto.OrderDTO;
import com.example.e_commerce.models.Cart;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.CartRepository;
import com.example.e_commerce.repositories.OrderRepository;
import com.example.e_commerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    // Place order for a user from their cart

    public ResponseEntity<?> placeOrder(Long userId) {
        Optional<User> user = userRepository.findById(userId);
//                .orElseThrow(() -> new RuntimeException("User not found"));
        if(user.isEmpty()){return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User not found"));
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getProducts().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cart is empty, unable to place order"));
        }

//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));

        double totalAmount = cart.getProducts()
                .stream()
                .mapToDouble(Product::getPrice)
                .sum();

        Order order = new Order(
                LocalDateTime.now(),
                totalAmount,
                user.orElse(null),
                new ArrayList<>(cart.getProducts())
        );

        Order savedOrder = orderRepository.save(order);

        cart.getProducts().clear();
        cartRepository.save(cart);

        return ResponseEntity.ok(convertToDTO(savedOrder));
    }



    // Get all orders by userId
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get every order across all users (admin only, enforced at the controller/security layer)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
// Get order by orderId
public Optional<Order> getOrderById(Long orderId) {
    return orderRepository.findById(orderId);
}
public boolean deleteOrderById(Long id){
        Optional<Order> orderOpt=orderRepository.findById(id);
        if(orderOpt.isPresent()){
            Order order=orderOpt.get();
            orderRepository.delete(order);
            return true;
        }
        return false;
}

    // Optional: Return DTO directly (if preferred)
    public OrderDTO getOrderDTOById(Long orderId) {
        return getOrderById(orderId)
                .map(this::convertToDTO)
                .orElse(null);  // or throw exception as needed
    }

    // Conversion helper
    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getUser().getUsername(), // take username from user
                order.getProducts()
                        .stream()
                        .map(Product::getName)   // only product names
                        .collect(Collectors.toList())
        );
    }
}

