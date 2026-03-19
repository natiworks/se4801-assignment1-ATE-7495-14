// Student Number: ATE/7495/14

package com.shopwave.shopwavestarter.mapper;

import com.shopwave.shopwavestarter.dto.ProductDTO;
import com.shopwave.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {

        if (product == null) return null;

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ?
                        product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ?
                        product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}