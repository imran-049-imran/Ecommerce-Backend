package com.restapis.ecommerce.io;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Boolean active;


}
