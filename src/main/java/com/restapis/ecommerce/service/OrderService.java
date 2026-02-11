package com.restapis.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapis.ecommerce.entity.Order;
import com.restapis.ecommerce.io.OrderItem;
import com.restapis.ecommerce.io.OrderRequest;
import com.restapis.ecommerce.io.OrderResponse;
import com.restapis.ecommerce.repository.CartRepository;
import com.restapis.ecommerce.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private static final long MIN_AMOUNT_PAISE = 5000; // ₹50

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        UserService userService,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    // ================= CREATE ORDER =================
    public OrderResponse createOrderWithPayment(OrderRequest request) {
        try {
            Stripe.apiKey = stripeSecretKey;

            String userId = userService.getCurrentUserId();
            String orderId = UUID.randomUUID().toString();

            long amountInPaise = BigDecimal
                    .valueOf(request.getAmount())
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValue();

            if (amountInPaise < MIN_AMOUNT_PAISE) {
                throw new IllegalArgumentException("Minimum order amount is ₹50");
            }

            String itemsJson = objectMapper.writeValueAsString(request.getOrderedItems());

            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInPaise)
                            .setCurrency("inr")
                            .setDescription("Order " + orderId)
                            .putMetadata("orderId", orderId)
                            .putMetadata("userId", userId)
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Order order = Order.builder()
                    .id(orderId)
                    .userId(userId)
                    .userName(request.getUserName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .userAddress(request.getUserAddress())
                    .amount(request.getAmount())
                    .paymentStatus("PENDING")
                    .orderStatus("CREATED")
                    .stripeOrderId(paymentIntent.getId())
                    .stripeClientSecret(paymentIntent.getClientSecret())
                    .orderedItems(itemsJson)
                    .build();

            orderRepository.save(order);
            return mapToResponse(order);

        } catch (Exception e) {
            log.error("Order creation failed", e);
            throw new RuntimeException("Order creation failed");
        }
    }

    // ================= VERIFY PAYMENT =================
    @Transactional
    public void verifyPayment(Map<String, String> data) {
        try {
            Stripe.apiKey = stripeSecretKey;

            String stripeOrderId = data.get("stripeOrderId");
            PaymentIntent intent = PaymentIntent.retrieve(stripeOrderId);

            Order order = orderRepository.findByStripeOrderId(stripeOrderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setPaymentStatus(intent.getStatus());
            order.setStripePaymentId(intent.getId());

            if ("succeeded".equalsIgnoreCase(intent.getStatus())) {
                order.setOrderStatus("CONFIRMED");
                cartRepository.deleteByUserId(order.getUserId());
            }

            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Payment verification failed", e);
            throw new RuntimeException("Payment verification failed");
        }
    }

    // ================= USER ORDERS =================
    public List<OrderResponse> getUserOrders() {
        String userId = userService.getCurrentUserId(); // ✅ fixed method call
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= ADMIN ORDERS =================
    public List<OrderResponse> getOrdersOfAllUsers() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= UPDATE STATUS =================
    public void updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    // ================= DELETE ORDER =================
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    // ================= MAPPER =================
    private OrderResponse mapToResponse(Order order) {
        try {
            List<OrderItem> items = objectMapper.readValue(
                    order.getOrderedItems(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class)
            );

            return OrderResponse.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .userName(order.getUserName())
                    .userAddress(order.getUserAddress())
                    .phoneNumber(order.getPhoneNumber())
                    .email(order.getEmail())
                    .amount(order.getAmount())
                    .paymentStatus(order.getPaymentStatus())
                    .orderStatus(order.getOrderStatus())
                    .stripeOrderId(order.getStripeOrderId())
                    .stripePaymentId(order.getStripePaymentId())
                    .stripeClientSecret(order.getStripeClientSecret())
                    .orderedItems(items)
                    .build();

        } catch (Exception e) {
            log.error("Order mapping failed", e);
            throw new RuntimeException("Order mapping failed");
        }
    }
}
