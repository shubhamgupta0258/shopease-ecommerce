package com.example.e_commerce.controllers;

import com.example.e_commerce.dto.ProductDTO;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.payload.ProductRequest;
import com.example.e_commerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Add a new product
    @PostMapping
    public ProductDTO addProduct(@RequestBody ProductRequest request) {
        return productService.convertToDTO(productService.createProduct(request));
    }

    // Get all products — supports search, category/price filters, sorting and pagination
    // e.g. /api/products?search=laptop&categoryId=2&minPrice=500&maxPrice=2000&page=0&size=12&sort=price,asc
    @GetMapping
    public Page<ProductDTO> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 12, sort = "id") Pageable pageable) {
        return productService.searchProducts(search, categoryId, minPrice, maxPrice, pageable);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(productService.convertToDTO(product.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
    }

    // Update a product
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product updated = productService.updateProduct(id, request);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(productService.convertToDTO(updated));
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
