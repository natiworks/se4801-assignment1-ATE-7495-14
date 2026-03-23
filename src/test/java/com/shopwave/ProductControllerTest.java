// Student Number: ATE/7495/14
package com.shopwave;

import com.shopwave.controller.ProductController;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.GlobalExceptionHandler;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_returns200WithPage() throws Exception {
        ProductDTO dto = ProductDTO.builder()
                .id(1L)
                .name("Mouse")
                .price(new BigDecimal("19.99"))
                .stock(50)
                .build();

        Page<ProductDTO> page = new PageImpl<>(List.of(dto));

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products?page=0&size=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Mouse"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getProductById_notFound_returns404() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/products/999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}