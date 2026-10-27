package com.example.pms.mapper;

import com.example.pms.dto.CartItemResponse;
import com.example.pms.dto.CartResponse;
import com.example.pms.dto.OrderItemResponse;
import com.example.pms.dto.OrderResponse;
import com.example.pms.dto.ProductResponse;
import com.example.pms.dto.UserResponse;
import com.example.pms.entity.Cart;
import com.example.pms.entity.CartItem;
import com.example.pms.entity.Order;
import com.example.pms.entity.OrderItem;
import com.example.pms.entity.Product;
import com.example.pms.entity.User;

import java.math.BigDecimal;
import java.util.List;

public final class EntityMapper {

    private EntityMapper() {
    }

    public static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }

    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getQuantity(), p.isEnabled());
    }

    public static CartItemResponse toResponse(CartItem ci) {
        Product p = ci.getProduct();
        return new CartItemResponse(p.getId(), p.getName(), p.getPrice(), ci.getQuantity(), ci.getSubtotal());
    }

    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(EntityMapper::toResponse)
                .toList();
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), items, total);
    }

    public static OrderItemResponse toResponse(OrderItem oi) {
        return new OrderItemResponse(oi.getProduct().getId(), oi.getProductName(),
                oi.getPrice(), oi.getQuantity(), oi.getSubtotal());
    }

    public static OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(EntityMapper::toResponse)
                .toList();
        return new OrderResponse(o.getId(), o.getStatus(), o.getTotalAmount(), o.getCreatedAt(), items);
    }
}
