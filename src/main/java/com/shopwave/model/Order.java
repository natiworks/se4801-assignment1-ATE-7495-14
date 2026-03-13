// Student Number: ATE/7495/14
package com.shopwave.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String orderNumber;@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    /**
     * Convenience method — adds an OrderItem and updates totalAmount.
     */
    public void addItem(Product product, int quantity) {
        OrderItem item = OrderItem.builder()
                .order(this)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .build();
        items.add(item);
        BigDecimal lineTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));
        this.totalAmount = (this.totalAmount == null)
                ? lineTotal
                : this.totalAmount.add(lineTotal);
    }
}