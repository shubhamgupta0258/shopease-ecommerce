package com.example.e_commerce.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final RazorpayClient client;
    private final String keySecret;
    private final String keyId;

    public PaymentService(@Value("${razorpay.key.id}") String keyId,
                           @Value("${razorpay.key.secret}") String keySecret) throws Exception {
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.client = new RazorpayClient(keyId, keySecret);
    }

    public String getKeyId() {
        return keyId;
    }

    public Order createOrder(int amountInPaise) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise); // e.g., 50000 for ₹500
        options.put("currency", "INR");
        options.put("payment_capture", 1);

        return client.orders.create(options);
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);

        return Utils.verifyPaymentSignature(options, keySecret);
    }
}
