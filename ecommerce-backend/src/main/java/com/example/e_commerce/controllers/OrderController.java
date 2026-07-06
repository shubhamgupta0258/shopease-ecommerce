package com.example.e_commerce.controllers;

import com.example.e_commerce.dto.OrderDTO;
import com.example.e_commerce.models.Order;
import com.example.e_commerce.models.User;
import com.example.e_commerce.services.OrderService;
import com.example.e_commerce.services.ProductService;
import com.example.e_commerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/place/{userId}")
    public ResponseEntity<?> placeOrder(@PathVariable Long userId) {
        return orderService.placeOrder(userId);
    }

    // Get every order across all users — admin only (enforced in SecurityConfig)
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Get orders belonging to the currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<List<OrderDTO>> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
    }

    // Get single order by orderId — only the order's owner or an admin may view it
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long orderId, Authentication authentication) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Not yet ordered");
        }

        if (!isOwnerOrAdmin(orderOpt.get(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this order");
        }

        return ResponseEntity.ok(orderService.getOrderDTOById(orderId));
    }

    // Delete an order — only the order's owner or an admin may delete it
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id, Authentication authentication) {
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("order not found or cannot be deleted");
        }

        if (!isOwnerOrAdmin(orderOpt.get(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this order");
        }

        boolean deleted = orderService.deleteOrderById(id);
        if (deleted) {
            return ResponseEntity.ok("order deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("order not found or cannot be deleted");
        }
    }

    private boolean isOwnerOrAdmin(Order order, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }

        String email = authentication.getName();
        return order.getUser() != null && email.equals(order.getUser().getEmail());
    }
}
