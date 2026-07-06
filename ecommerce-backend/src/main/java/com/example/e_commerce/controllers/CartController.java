package com.example.e_commerce.controllers;

import com.example.e_commerce.dto.CartDTO;
import com.example.e_commerce.models.Cart;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.User;
import com.example.e_commerce.services.CartService;
import com.example.e_commerce.services.ProductService;
import com.example.e_commerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    public CartController(CartService cartService, UserService userService, ProductService productService) {
        this.cartService = cartService;
        this.userService = userService;
        this.productService = productService;
    }


//    @PostMapping("/add")
//    public ResponseEntity<?> addToCart(@RequestParam Long userId, @RequestBody List<Long> productIds) {
//        // Check if user exists
//        Optional<User> user = userService.getUserById(userId);
//        if (user.isEmpty()) {
//            return ResponseEntity.status(404).body("User not found");
//        }
//
//        // ✅ CRITICAL: Validate all products exist before adding
//        List<Product> products = productService.getProductsByIds(productIds);
//
//        System.out.println("Requested product IDs: " + productIds);
//        System.out.println("Found products: " + products.stream().map(Product::getId).toList());
//
//        if (products.size() != productIds.size()) {
//            List<Long> foundIds = products.stream().map(Product::getId).toList();
//            List<Long> missingIds = productIds.stream()
//                    .filter(id -> !foundIds.contains(id))
//                    .toList();
//            return ResponseEntity.status(404).body("❌ Products not found in database: " + missingIds + ". Found: " + foundIds);
//        }
//
//        cartService.addToCart(user.get(), products);
//        return ResponseEntity.ok("✅ Products added to cart: " + products.stream().map(Product::getId).toList());
//    }
@PostMapping("/add")
public ResponseEntity<?> addToCart(@RequestBody List<Long> productIds,
                                   Authentication authentication) {

    // ✅ 1. Extract email from JWT (NO userId from request anymore)
    String email = authentication.getName();

    Optional<User> user = userService.getUserByEmail(email);
    if (user.isEmpty()) {
        return ResponseEntity.status(404).body("User not found");
    }

    // ✅ 2. Validate products
    List<Product> products = productService.getProductsByIds(productIds);

    System.out.println("Requested product IDs: " + productIds);
    System.out.println("Found products: " + products.stream().map(Product::getId).toList());

    if (products.size() != productIds.size()) {
        List<Long> foundIds = products.stream().map(Product::getId).toList();
        List<Long> missingIds = productIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        return ResponseEntity.status(404)
                .body("❌ Products not found in database: " + missingIds + ". Found: " + foundIds);
    }

    // ✅ 3. Add to cart using the AUTHENTICATED USER
    cartService.addToCart(user.get(), products);

    return ResponseEntity.ok("✅ Products added to cart: " +
            products.stream().map(Product::getId).toList());
}

    @GetMapping("")
    public ResponseEntity<CartDTO> getCartByUser(Authentication authentication) {

        // Extract email from JWT
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        Cart cart = cartService.getCartByUserId(user.getId());

        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        CartDTO dto = cartService.convertToDTO(cart);
        return ResponseEntity.ok(dto);
    }



    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeProductFromCart(
            @PathVariable Long productId,
            Authentication authentication) {

        // Get user from JWT
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        boolean removed = cartService.removeProductFromCart(user.getId(), productId);

        if (removed) {
            return ResponseEntity.ok("Product removed from cart");
        } else {
            return ResponseEntity.status(404).body("Product or cart not found");
        }
    }

}
