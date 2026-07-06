package com.example.e_commerce.services;

import com.example.e_commerce.dto.ProductDTO;
import com.example.e_commerce.models.Category;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.payload.ProductRequest;
import com.example.e_commerce.repositories.CategoryRepository;
import com.example.e_commerce.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartService cartService;

    // create product from a request payload (resolves categoryId to a real Category)
    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());
        applyCategory(product, request.getCategoryId());
        return productRepository.save(product);
    }

    //Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Search/filter/sort/paginate products
    public Page<ProductDTO> searchProducts(String search, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.searchProducts(search, categoryId, minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    // Update product from a request payload
    public Product updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id).map(product -> {
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setImageUrl(request.getImageUrl());
            applyCategory(product, request.getCategoryId());
            return productRepository.save(product);
        }).orElse(null);
    }

    // Delete product
    public void deleteProduct(Long id) {
        // 1. Remove the product from every cart so no CartItem is left pointing at a deleted row
        cartService.removeProductFromAllCarts(id);

        // 2. Delete the product itself. If it still can't be deleted (e.g. it appears in
        //    a past order), surface a clear message instead of a raw DB error.
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Cannot delete this product: it appears in one or more past orders.");
        }
    }

    public List<Product> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    private void applyCategory(Product product, Long categoryId) {
        if (categoryId == null) {
            product.setCategory(null);
            return;
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
        product.setCategory(category);
    }

    public ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getQuantity(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getImageUrl()
        );
    }
}
