// Student Number: ATE/7495/14
package com.shopwave;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.mapper.ProductMapper;
import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;

import com.shopwave.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductMapper productMapper;

    @InjectMocks private ProductService productService;

    private Category category;
    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .category(category)
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .categoryId(1L)
                .categoryName("Electronics")
                .build();
    }

    @Test
    void createProduct_happyPath_returnsProductDTO() {
        CreateProductRequest request = new CreateProductRequest(
                "Laptop", "High-end laptop", new BigDecimal("999.99"), 10, 1L
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_categoryNotFound_throwsIllegalArgumentException() {
        CreateProductRequest request = new CreateProductRequest(
                "Laptop", "desc", new BigDecimal("999.99"), 10, 99L
        );

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void updateStock_resultNegative_throwsIllegalArgumentException() {
        product.setStock(2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(1L, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock cannot");
    }
}