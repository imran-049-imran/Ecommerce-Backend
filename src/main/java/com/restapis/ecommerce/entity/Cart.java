package com.restapis.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    // productId -> quantity (JSON string)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String items;

    public Cart(String userId, String items) {
        this.userId = userId;
        this.items = items;
    }
}
