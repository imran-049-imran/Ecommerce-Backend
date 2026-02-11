package com.restapis.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    // ⭐ User details
    private String userId;
    private String userName;
    private String userAddress;
    private String phoneNumber;
    private String email;

    // ⭐ Ordered items stored as JSON string
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String orderedItems;

    // ⭐ Order details
    private Double amount;
    private String paymentStatus;
    private String orderStatus;

    // ⭐ Stripe details
    private String stripeOrderId;
    private String stripePaymentId;
    private String stripeSignature;
    private String stripeClientSecret;
}
