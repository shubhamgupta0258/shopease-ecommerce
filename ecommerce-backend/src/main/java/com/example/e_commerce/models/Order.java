package com.example.e_commerce.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
//Specifies that this entity maps to the table named orders in the database instead
// of using the default table name (which would be the class name).
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private Double totalAmount;
    private String razorpayOrderId;
    private String razorpayPaymentId;

    // Order belongs to one User
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    // Order can have many Products
    @ManyToMany
    @JoinTable(
            name="order_products",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name="product_id")
    )
    private List<Product> products;

//    Since your use case needs a constructor without id (because id is auto-generated),
//    it's best to keep your manual constructor for now that matches the exact parameters
//    your service method uses. That avoids errors and keeps intent clear.
    public Order(LocalDateTime createdAt,Double totalAmount,User user,List<Product>products ){
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.user = user;
        this.products = products;
    }

}
