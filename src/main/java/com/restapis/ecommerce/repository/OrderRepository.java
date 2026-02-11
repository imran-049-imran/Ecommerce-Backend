package com.restapis.ecommerce.repository;

import com.restapis.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByStripeOrderId(String stripeOrderId);

    List<Order> findByUserId(String userId);
}
