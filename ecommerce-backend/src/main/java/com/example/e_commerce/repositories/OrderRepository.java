package com.example.e_commerce.repositories;

import com.example.e_commerce.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Custom query to get all orders for a user
        List<Order> findByUserId(Long userId);
    }


