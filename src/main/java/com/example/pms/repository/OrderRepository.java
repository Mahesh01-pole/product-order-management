package com.example.pms.repository;

import com.example.pms.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDescIdDesc(Long userId);

    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
