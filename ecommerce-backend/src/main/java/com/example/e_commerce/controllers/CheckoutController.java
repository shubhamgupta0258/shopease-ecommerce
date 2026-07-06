package com.example.e_commerce.controllers;

import com.example.e_commerce.payload.PaymentVerificationRequest;
import com.example.e_commerce.services.CheckoutService;
import com.example.e_commerce.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final PaymentService paymentService;

    public CheckoutController(CheckoutService checkoutService, PaymentService paymentService) {
        this.checkoutService = checkoutService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody PaymentVerificationRequest request, Authentication authentication) {

        try {
            boolean valid = paymentService.verifySignature(
                    request.getRazorpayOrderId(),
                    request.getRazorpayPaymentId(),
                    request.getRazorpaySignature()
            );

            if (!valid) {
                return ResponseEntity.status(400).body("Payment verification failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Payment verification error: " + e.getMessage());
        }

        // email comes from JWT
        String email = authentication.getName();

        return ResponseEntity.ok(checkoutService.placeOrder(
                email,
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId()
        ));
    }
}
