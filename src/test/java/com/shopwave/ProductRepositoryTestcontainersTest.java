// Student Number: ATE/7495/14
package com.shopwave;

import com.shopwave.model.Product;
import com.shopwave.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTestcontainersTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("shopwave_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver"); // ADD THIS
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect"); // ADD THIS
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void seedData() {
        productRepository.deleteAll();

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
    void findByNameContainingIgnoreCase_withPostgres() {
        List<Product> results =
                productRepository.findByNameContainingIgnoreCase("wireless");

        assertThat(results).hasSize(2);
    }

    @Test
    void findByPriceLessThanEqual_withPostgres() {
        List<Product> results =
                productRepository.findByPriceLessThanEqual(new BigDecimal("30.00"));

        assertThat(results).hasSize(2);
    }
}