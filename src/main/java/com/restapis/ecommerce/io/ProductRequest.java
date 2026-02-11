package com.restapis.ecommerce.io;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private String category;
//    private Double rating;

}
