package com.example.e_commerce.controllers;

import com.example.e_commerce.services.PaymentService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Public key id — safe to expose to the frontend (secret stays server-side)
    @GetMapping("/key")
    public ResponseEntity<Map<String, String>> getKey() {
        return ResponseEntity.ok(Map.of("keyId", paymentService.getKeyId()));
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestParam double amount) {
        try {
            int amountInPaise = (int) Math.round(amount * 100);
            Order order = paymentService.createOrder(amountInPaise);
            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("id").toString(),
                    "amount", order.get("amount").toString(),
                    "currency", order.get("currency").toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }
}
