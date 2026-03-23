// Student Number: ATE/7495/14
package com.shopwave;

import com.shopwave.model.Product;

import com.shopwave.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void seedData() {
        productRepository.save(Product.builder()
                .name("Wireless Keyboard")
                .price(new BigDecimal("49.99"))
                .stock(30)
                .build());

        productRepository.save(Product.builder()
                .name("Wireless Mouse")
                .price(new BigDecimal("29.99"))
                .stock(50)
                .build());

        productRepository.save(Product.builder()
                .name("HDMI Cable")
                .price(new BigDecimal("9.99"))
                .stock(200)
                .build());
    }

    @Test
    void findByNameContainingIgnoreCase_matchesSubstring() {
        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("wireless");

        assertThat(results).hasSize(2);
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("HDMI");

        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("tablet");

        assertThat(results).isEmpty();
    }

    @Test
    void findByPriceLessThanEqual_returnsCorrectProducts() {
        List<Product> results =
                productRepository.findByPriceLessThanEqual(new BigDecimal("30.00"));

        assertThat(results).hasSize(2);
    }
}