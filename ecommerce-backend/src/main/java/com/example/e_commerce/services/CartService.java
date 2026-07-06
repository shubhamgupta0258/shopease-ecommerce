////package com.example.e_commerce.services;
////
////import com.example.e_commerce.models.Cart;
////import com.example.e_commerce.models.Product;
////import com.example.e_commerce.models.User;
////import com.example.e_commerce.repositories.CartRepository;
////import com.example.e_commerce.repositories.ProductRepository;
////import com.example.e_commerce.repositories.UserRepository;
////import jakarta.transaction.Transactional;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Objects;
////import java.util.Optional;
////
////@Service
////public class CartService {
////    @Autowired
////    private CartRepository cartRepository;
////
////    @Autowired
////    private UserRepository userRepository;
////
////    @Autowired
////    private ProductRepository productRepository;
////
////    // Get cart by user ID
////    public Cart getCartByUserId(Long userId) {
////        return cartRepository.findByUserId(userId);
////    }
////
////    // Create a new cart
////    public Cart createCart(Long userId, List<Long> productIds) {
////        Optional<User> userOptional = userRepository.findById(userId);
////        if (userOptional.isEmpty()) return null;
////
////        List<Product> products = productRepository.findAllById(productIds);
////        Cart cart = new Cart(userOptional.get(), products);
////        return cartRepository.save(cart);
////    }
////
////    public void removeProductFromAllCarts(Long productId) {
////        List<Cart> carts = cartRepository.findAll();
////
////        for (Cart cart : carts) {
////            cart.getProducts().removeIf(p -> p.getId().equals(productId));
////            cartRepository.save(cart);
////        }
////    }
////
//////     Add a product to the cart
////    public Cart addProductToCart(Long userId, Long productId) {
////        Cart cart = cartRepository.findByUserId(userId);
////        if (cart == null) return null;
////
////        Optional<Product> product = productRepository.findById(productId);
////        if (product.isPresent()) {
////            cart.getProducts().add(product.get());
////            return cartRepository.save(cart);
////        }
////        return null;
////    }
////
////    // Remove a product from the cart
//////    public Cart removeProductFromCart(Long userId, Long productId) {
//////        Cart cart = cartRepository.findByUserId(userId);
//////        if (cart == null) return null;
//////
//////        cart.getProducts().removeIf(product -> product.getId().equals(productId));
//////        return cartRepository.save(cart);
//////    }
////
////    public Cart removeProductFromCart(Long userId, Long productId) {
////        Cart cart = cartRepository.findByUserId(userId);
////        if (cart == null) return null;
////
////        boolean removed = cart.getProducts().removeIf(product -> product.getId().equals(productId));
////
////        if (!removed) {
////            // Product not found in cart
////            return new Cart(); // 👈 Or throw exception, or return special signal
////        }
////
////        return cartRepository.save(cart);
////    }
////
////
////
//////    public void addToCart(User user, List<Product> products) {
//////        Cart cart = cartRepository.findByUserId(user.getId());
//////
//////        if (cart == null) {
//////            cart = new Cart();
//////            cart.setUser(user);
//////            cart.setProducts(new ArrayList<>(products));
//////        } else {
////////            List<Product> existingProducts = cart.getProducts();
////////            existingProducts.addAll(products);
////////            cart.setProducts(existingProducts);
//////            cart.getProducts().addAll(products);
//////        }
//////
//////        cartRepository.save(cart);
//////    }
////public void addToCart(User user, List<Product> products) {
////    Cart cart = cartRepository.findByUserId(user.getId());
////
////    if (cart == null) {
////        cart = new Cart();
////        cart.setUser(user);
////        cart.setProducts(new ArrayList<>());
////    }
////
////    // ensure no nulls or invalid products
////    List<Product> validProducts = products.stream()
////            .filter(Objects::nonNull)
////            .toList();
////
////    cart.getProducts().addAll(validProducts);
////
////    cartRepository.save(cart);
////}
////
//////@Transactional
//////public void addToCart(User user, List<Product> products) {
//////    Cart cart = cartRepository.findByUserId(user.getId())
//////            .orElseGet(() -> {
//////                Cart newCart = new Cart();
//////                newCart.setUser(user);
//////                return cartRepository.save(newCart);
//////            });
//////
//////    cart.getProducts().addAll(products); // <-- must be managed entities from DB
//////    cartRepository.save(cart);
//////}
////
////
////}
//package com.example.e_commerce.services;
//
//import com.example.e_commerce.dto.CartDTO;
//import com.example.e_commerce.dto.ProductDTO;
//import com.example.e_commerce.models.Cart;
//import com.example.e_commerce.models.Product;
//import com.example.e_commerce.models.User;
//import com.example.e_commerce.repositories.CartRepository;
//import com.example.e_commerce.repositories.ProductRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;
//
//@Service
//public class CartService {
//
//    private final CartRepository cartRepository;
//    private ProductRepository productRepository;
//
//    public CartService(CartRepository cartRepository) {
//        this.cartRepository = cartRepository;
//    }
//
//    public void ProductService(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
////    public void addToCart(User user, List<Product> products) {
////        Cart cart = cartRepository.findByUserId(user.getId());
////
////        if (cart == null) {
////            cart = new Cart();
////            cart.setUser(user);
////            cart.setProducts(new ArrayList<>(products));
////        } else {
////            List<Product> existingProducts = cart.getProducts();
////            if (existingProducts == null) {   // ✅ Fix
////                existingProducts = new ArrayList<>();
////            }
////            existingProducts.addAll(products);
////            cart.setProducts(existingProducts);
////        }
////
////        cartRepository.save(cart);
////    }
//public void addToCart(User user, List<Product> products) {
//    System.out.println("Adding products to cart for user: " + user.getId());
//    System.out.println("Products to add: " + products.stream().map(Product::getId).toList());
//
//    Cart cart = cartRepository.findByUserId(user.getId());
//
//    if (cart == null) {
//        System.out.println("Creating new cart for user: " + user.getId());
//        cart = new Cart();
//        cart.setUser(user);
//        cart.setProducts(new ArrayList<>(products));
//    } else {
//        System.out.println("Adding to existing cart: " + cart.getId());
//        List<Product> existingProducts = cart.getProducts();
//        if (existingProducts == null) {
//            existingProducts = new ArrayList<>();
//        }
//        existingProducts.addAll(products);
//        cart.setProducts(existingProducts);
//    }
//
//    Cart savedCart = cartRepository.save(cart);
//    System.out.println("Cart saved with ID: " + savedCart.getId());
//    System.out.println("Products in saved cart: " + savedCart.getProducts().size());
//}
//
//
////    public List<Product> getCartProducts(Long userId) {
////        Cart cart = cartRepository.findByUserId(userId);
////        return cart != null ? cart.getProducts() : List.of();
////    }
//public List<Product> getCartProducts(Long userId) {
//    System.out.println("🔍 Looking for cart with userId: " + userId);
//
//    Cart cart = cartRepository.findByUserId(userId);
//
//    if (cart == null) {
//        System.out.println("❌ No cart found for userId: " + userId);
//        return List.of();
//    }
//
//    System.out.println("✅ Cart found with ID: " + cart.getId());
//    System.out.println("📦 Number of products in cart: " + (cart.getProducts() != null ? cart.getProducts().size() : 0));
//
//    if (cart.getProducts() != null) {
//        cart.getProducts().forEach(p -> System.out.println("  - Product: " + p.getId() + " (" + p.getName() + ")"));
//    }
//
//    return cart != null ? cart.getProducts() : List.of();
//}
//
//public CartDTO convertToDTO(Cart cart){
//        CartDTO dto=new CartDTO();
//        dto.setId(cart.getId());
//        dto.setUsername(cart.getUser().getUsername());
//        dto.setProducts(
//                cart.getProducts().stream()
//                        .map(product -> {
//                            ProductDTO pDto=new ProductDTO();
//                            pDto.setId(product.getId());
//                            pDto.setName(product.getName());
//                            pDto.setPrice(product.getPrice());
//                            pDto.setDescription(product.getDescription());
//                            return pDto;
//                        })
//                        .collect(Collectors.toList())
//        );
//        dto.setTotalPrice(
//                cart.getProducts().stream()
//                        .mapToDouble(Product::getPrice)
//                        .sum()
//        );
//        return dto;
//}
//
//
//    public boolean removeProductFromCart(Long userId,Long productId) {
//        Cart cart = cartRepository.findByUserId(userId);
//        if (cart == null || cart.getProducts() == null) {
//            return false;
//        }
//        boolean removed = cart.getProducts().removeIf(product -> product.getId().equals(productId));
//        if (removed) {
//            cartRepository.save(cart);
//        }
//        return removed;
//        }
//    // Added missing method that ProductService was calling
//    public void removeProductFromAllCarts(Long productId) {
//        List<Cart> carts = cartRepository.findAll();
//        for (Cart cart : carts) {
//            cart.getProducts().removeIf(p -> p.getId().equals(productId));
//            cartRepository.save(cart);
//        }
//    }
//    // Additional method for getting cart by userId (used in other services)
//    public Cart getCartByUserId(Long userId) {
//        return cartRepository.findByUserId(userId);
//    }
//}
package com.example.e_commerce.services;

import com.example.e_commerce.dto.CartDTO;
import com.example.e_commerce.dto.ProductDTO;
import com.example.e_commerce.models.Cart;
import com.example.e_commerce.models.CartItem;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.CartRepository;
import com.example.e_commerce.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // ---------------------- ADD TO CART ----------------------
    public void addToCart(User user, List<Product> productsToAdd) {

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        for (Product product : productsToAdd) {

            // check if product already in cart
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(i -> i.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                // increase quantity
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + 1);
            } else {
                // create new cart item
                CartItem newItem = new CartItem();
                newItem.setProduct(product);
                newItem.setQuantity(1);
                cart.getItems().add(newItem);
            }
        }

        cartRepository.save(cart);
    }

    // ---------------------- REMOVE PRODUCT ----------------------
    public boolean removeProductFromCart(Long userId, Long productId) {

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) return false;

        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOpt.isEmpty()) return false;

        CartItem item = itemOpt.get();

        if (item.getQuantity() > 1) {
            // decrease quantity
            item.setQuantity(item.getQuantity() - 1);
        } else {
            // remove item fully
            cart.getItems().remove(item);
        }

        cartRepository.save(cart);
        return true;
    }

    // ---------------------- GET CART BY USER ----------------------
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // ---------------------- REMOVE PRODUCT FROM EVERY CART ----------------------
    // Called before deleting a product, so no cart is left pointing at a row that no longer exists.
    public void removeProductFromAllCarts(Long productId) {
        List<Cart> carts = cartRepository.findAll();
        for (Cart cart : carts) {
            boolean itemsChanged = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
            boolean legacyChanged = cart.getProducts() != null
                    && cart.getProducts().removeIf(p -> p.getId().equals(productId));
            if (itemsChanged || legacyChanged) {
                cartRepository.save(cart);
            }
        }
    }

    // ---------------------- CONVERT TO DTO ----------------------
    public CartDTO convertToDTO(Cart cart) {

        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUsername(cart.getUser().getEmail());

        // convert each CartItem → ProductDTO
        dto.setProducts(
                cart.getItems().stream()
                        .map(item -> new ProductDTO(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getProduct().getDescription(),
                                item.getQuantity(),
                                item.getProduct().getCategory() != null ? item.getProduct().getCategory().getId() : null,
                                item.getProduct().getCategory() != null ? item.getProduct().getCategory().getName() : null,
                                item.getProduct().getImageUrl()
                        ))
                        .toList()
        );

        // total price = sum(price * quantity)
        double totalPrice = cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        dto.setTotalPrice(totalPrice);

        return dto;
    }

}
