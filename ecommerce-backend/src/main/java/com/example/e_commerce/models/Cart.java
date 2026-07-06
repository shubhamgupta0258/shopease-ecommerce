package com.example.e_commerce.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // A cart belongs to a user (many carts can belong to one user)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ❗ NEW FIELD — CORRECT WAY TO STORE CART ITEMS
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    // A cart can contain many products, and products can be in many carts
    @ManyToMany
    @JoinTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;
    public Cart(User user, List<Product> products) {
        this.user = user;
        this.products = products;
    }
}

