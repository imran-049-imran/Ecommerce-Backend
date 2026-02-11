package com.restapis.ecommerce.io;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String id;

    // ⭐ User details
    private String userId;
    private String userName;
    private String userAddress;
    private String phoneNumber;
    private String email;

    private Double amount;
    private String paymentStatus;
    private String orderStatus;

    private String stripeOrderId;
    private String stripePaymentId;
    private String stripeSignature;
    private String stripeClientSecret;

    private List<OrderItem> orderedItems;
}
