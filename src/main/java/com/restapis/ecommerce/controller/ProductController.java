package com.restapis.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapis.ecommerce.io.ProductRequest;
import com.restapis.ecommerce.io.ProductResponse;
import com.restapis.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ProductController(ProductService productService,
                             ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    /* ================= ADD PRODUCT ================= */
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> addProduct(
            @RequestPart("product") String productJson,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // ✅ Convert JSON string → DTO
            ProductRequest productRequest =
                    objectMapper.readValue(productJson, ProductRequest.class);

            ProductResponse response =
                    productService.addProduct(productRequest, file);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid product data"
            );
        }
    }

    /* ================= GET ALL PRODUCTS ================= */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /* ================= GET PRODUCT BY ID ================= */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /* ================= DELETE PRODUCT ================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
