package com.restapis.ecommerce.service;

import com.restapis.ecommerce.entity.Product;
import com.restapis.ecommerce.io.ProductRequest;
import com.restapis.ecommerce.io.ProductResponse;
import com.restapis.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;

    // ✅ Single constant, outside project directory
    private static final String IMAGE_DIR = "uploads/images/";

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse addProduct(ProductRequest req, MultipartFile file) {
        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = saveImage(file);
        }

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .category(req.getCategory())
                .imageUrl(imageUrl)
                .active(true)
                .build();

        Product saved = repository.save(product);
        return mapToResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProductById(String id) {
        Product p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(p);
    }

    public void deleteProduct(String id) {
        repository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .category(p.getCategory())
                .imageUrl(p.getImageUrl())
                .active(p.getActive())
                .build();
    }

    private String saveImage(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(IMAGE_DIR));
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(IMAGE_DIR, fileName);
            Files.write(path, file.getBytes());

            return "http://localhost:8080/images/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

}

