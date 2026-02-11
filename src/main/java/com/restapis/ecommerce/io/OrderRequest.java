package com.restapis.ecommerce.io;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private List<OrderItem> orderedItems;

    private String userId;
    private String userName;
    private String userAddress;
    private String phoneNumber;
    private String email;

    private Double amount;
    private String orderStatus;
}
