package com.example.e_commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
private Long id;
private String name;
private Double price;
private String description;
private Integer quantity;
private Long categoryId;
private String categoryName;
private String imageUrl;
}
