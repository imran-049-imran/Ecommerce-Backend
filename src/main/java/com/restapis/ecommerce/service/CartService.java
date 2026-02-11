package com.restapis.ecommerce.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapis.ecommerce.entity.Cart;
import com.restapis.ecommerce.io.CartRequest;
import com.restapis.ecommerce.io.CartResponse;
import com.restapis.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    // ================= ADD PRODUCT =================
    public CartResponse addToCart(CartRequest request) {

        String userId = getLoggedInUserId();

        Cart cart = cartRepository
                .findByUserId(userId)
                .orElse(new Cart(userId, "{}"));

        Map<String, Integer> items = convertToMap(cart.getItems());

        items.put(
                request.getProductId(),
                items.getOrDefault(request.getProductId(), 0) + 1
        );

        cart.setItems(convertToJson(items));
        cartRepository.save(cart);

        return convertToResponse(cart);
    }

    // ================= GET CART =================
    public CartResponse getCart() {

        String userId = getLoggedInUserId();

        Cart cart = cartRepository
                .findByUserId(userId)
                .orElse(new Cart(userId, "{}"));

        return convertToResponse(cart);
    }

    // ================= CLEAR CART =================
    public void clearCart() {
        cartRepository.deleteByUserId(getLoggedInUserId());
    }

    // ================= REMOVE PRODUCT =================
    public CartResponse removeFromCart(CartRequest request) {

        String userId = getLoggedInUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Map<String, Integer> items = convertToMap(cart.getItems());

        if (items.containsKey(request.getProductId())) {
            int qty = items.get(request.getProductId());
            if (qty <= 1) items.remove(request.getProductId());
            else items.put(request.getProductId(), qty - 1);
        }

        cart.setItems(convertToJson(items));
        cartRepository.save(cart);

        return convertToResponse(cart);
    }

    // ================= HELPERS =================
    private String getLoggedInUserId() {
        return authenticationFacade
                .getAuthentication()
                .getName();
    }

    private Map<String, Integer> convertToMap(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, Integer>>() {}
            );
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private String convertToJson(Map<String, Integer> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }

    private CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(convertToMap(cart.getItems()))
                .build();
    }
}
