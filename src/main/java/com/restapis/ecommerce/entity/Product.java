package com.restapis.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    @Column(length = 10000)
    private String description;
    private Double price;
    private String category;

    private String imageUrl;
    private Boolean active;

}
