package com.restapis.ecommerce.controller;

import com.restapis.ecommerce.io.CartRequest;
import com.restapis.ecommerce.io.CartResponse;
import com.restapis.ecommerce.service.CartService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    // ADD PRODUCT TO CART
    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request) {

        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }

        return cartService.addToCart(request);
    }

    // GET USER CART
    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    // CLEAR CART
    @Transactional
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    // REMOVE PRODUCT FROM CART
    @Transactional
    @PostMapping("/remove")
    public CartResponse removeFromCart(@RequestBody CartRequest request) {

        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required");
        }

        return cartService.removeFromCart(request);
    }
}

