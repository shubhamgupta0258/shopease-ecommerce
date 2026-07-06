package com.example.e_commerce.services;

import com.example.e_commerce.dto.ProductDTO;
import com.example.e_commerce.models.Category;
import com.example.e_commerce.models.Product;
import com.example.e_commerce.payload.ProductRequest;
import com.example.e_commerce.repositories.CategoryRepository;
import com.example.e_commerce.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private ProductService productService;

    private ProductRequest request;
    private Category electronics;

    @BeforeEach
    void setUp() {
        request = new ProductRequest();
        request.setName("Laptop Pro");
        request.setDescription("Upgraded gaming laptop");
        request.setPrice(1499.99);
        request.setQuantity(10);
        request.setImageUrl("https://images.pexels.com/photos/577558/pexels-photo-577558.jpeg");

        electronics = new Category("Electronics");
        electronics.setId(1L);
    }

    @Test
    void createProduct_withCategoryId_resolvesCategoryAndSaves() {
        request.setCategoryId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product saved = productService.createProduct(request);

        assertThat(saved.getName()).isEqualTo("Laptop Pro");
        assertThat(saved.getCategory()).isEqualTo(electronics);
        assertThat(saved.getImageUrl()).isEqualTo(request.getImageUrl());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_withNullCategoryId_leavesCategoryNull() {
        request.setCategoryId(null);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product saved = productService.createProduct(request);

        assertThat(saved.getCategory()).isNull();
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void createProduct_withUnknownCategoryId_throws() {
        request.setCategoryId(99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void updateProduct_notFound_returnsNull() {
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        Product result = productService.updateProduct(5L, request);

        assertThat(result).isNull();
    }

    @Test
    void updateProduct_found_updatesFieldsAndSaves() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(1L, request);

        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualTo(1499.99);
    }

    @Test
    void deleteProduct_clearsCartsBeforeDeleting() {
        doNothing().when(cartService).removeProductFromAllCarts(1L);

        productService.deleteProduct(1L);

        // cart cleanup must happen before the actual delete
        InOrder order = inOrder(cartService, productRepository);
        order.verify(cartService).removeProductFromAllCarts(1L);
        order.verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_whenReferencedByPastOrder_throwsClearIllegalStateException() {
        doNothing().when(cartService).removeProductFromAllCarts(1L);
        doThrow(new DataIntegrityViolationException("FK violation")).when(productRepository).deleteById(1L);

        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("past orders");
    }

    @Test
    void convertToDTO_mapsAllFieldsIncludingCategoryAndImage() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setPrice(1499.99);
        product.setDescription("Upgraded gaming laptop");
        product.setQuantity(10);
        product.setImageUrl("https://images.pexels.com/photos/577558/pexels-photo-577558.jpeg");
        product.setCategory(electronics);

        ProductDTO dto = productService.convertToDTO(product);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCategoryId()).isEqualTo(1L);
        assertThat(dto.getCategoryName()).isEqualTo("Electronics");
        assertThat(dto.getImageUrl()).isEqualTo(product.getImageUrl());
    }

    @Test
    void convertToDTO_withNoCategory_setsNullCategoryFields() {
        Product product = new Product();
        product.setId(2L);
        product.setName("Mystery Item");
        product.setCategory(null);

        ProductDTO dto = productService.convertToDTO(product);

        assertThat(dto.getCategoryId()).isNull();
        assertThat(dto.getCategoryName()).isNull();
    }
}
