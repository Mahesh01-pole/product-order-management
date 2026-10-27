package com.example.pms.service;

import com.example.pms.dto.OrderResponse;
import com.example.pms.entity.Cart;
import com.example.pms.entity.CartItem;
import com.example.pms.entity.Order;
import com.example.pms.entity.OrderItem;
import com.example.pms.entity.Product;
import com.example.pms.entity.User;
import com.example.pms.exception.BadRequestException;
import com.example.pms.exception.InsufficientStockException;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.mapper.EntityMapper;
import com.example.pms.repository.CartRepository;
import com.example.pms.repository.OrderRepository;
import com.example.pms.repository.ProductRepository;
import com.example.pms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse placeOrder(String email) {
        User user = findUser(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        List<CartItem> cartItems = new ArrayList<>(cart.getItems());
        cartItems.sort(Comparator.comparing(ci -> ci.getProduct().getId()));

        Order order = new Order(user);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProduct().getId();
            int qty = cartItem.getQuantity();

            int updated = productRepository.decreaseStock(productId, qty);
            if (updated == 0) {
                throw buildStockFailure(productId, qty);
            }

            OrderItem orderItem = new OrderItem(cartItem.getProduct(), qty);
            order.addItem(orderItem);
            total = total.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        cart.getItems().clear(); 

        return EntityMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = findUser(email);
        return orderRepository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId()).stream()
                .map(EntityMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(String email, Long orderId) {
        User user = findUser(email);
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        return EntityMapper.toResponse(order);
    }


    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private RuntimeException buildStockFailure(Long productId, int requestedQty) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        if (!product.isEnabled()) {
            return new BadRequestException("Product '" + product.getName() + "' is currently unavailable");
        }
        Integer available = productRepository.findQuantityById(productId);
        return new InsufficientStockException("Insufficient stock for '" + product.getName()
                + "': requested " + requestedQty + ", available " + (available == null ? 0 : available));
    }
}
