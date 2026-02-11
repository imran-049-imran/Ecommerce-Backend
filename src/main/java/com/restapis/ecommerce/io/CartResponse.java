package com.restapis.ecommerce.io;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {

    private String id;
    private String userId;

    // productId -> quantity
    private Map<String, Integer> items = new HashMap<>();
}
