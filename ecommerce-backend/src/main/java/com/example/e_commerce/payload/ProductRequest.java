package com.example.e_commerce.payload;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Long categoryId;
    private String imageUrl;
}
