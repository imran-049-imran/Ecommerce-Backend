package com.restapis.ecommerce.io;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {

    private String productId;

}
