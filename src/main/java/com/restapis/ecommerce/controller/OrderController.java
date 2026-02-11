package com.restapis.ecommerce.controller;

import com.restapis.ecommerce.io.OrderRequest;
import com.restapis.ecommerce.io.OrderResponse;
import com.restapis.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    // ================= CREATE ORDER =================
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request) {

        return ResponseEntity.ok(
                orderService.createOrderWithPayment(request)
        );
    }

    // ================= VERIFY PAYMENT =================
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(
            @RequestBody Map<String, String> data) {

        orderService.verifyPayment(data);
        return ResponseEntity.ok(
                Map.of("status", "PAYMENT_VERIFIED")
        );
    }

    // ================= USER ORDERS =================
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        return ResponseEntity.ok(
                orderService.getUserOrders()
        );
    }

    // ================= DELETE ORDER =================
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> removeOrder(
            @PathVariable String orderId) {

        orderService.removeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // ================= ADMIN: ALL ORDERS =================
    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(
                orderService.getOrdersOfAllUsers()
        );
    }

    // ================= UPDATE STATUS =================
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable String orderId,
            @RequestParam String status) {

        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.noContent().build();
    }
}
