package com.example.pms.service;

import com.example.pms.dto.CartItemRequest;
import com.example.pms.dto.CartItemUpdateRequest;
import com.example.pms.dto.CartResponse;
import com.example.pms.entity.Cart;
import com.example.pms.entity.CartItem;
import com.example.pms.entity.Product;
import com.example.pms.entity.User;
import com.example.pms.exception.BadRequestException;
import com.example.pms.exception.InsufficientStockException;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.mapper.EntityMapper;
import com.example.pms.repository.CartRepository;
import com.example.pms.repository.ProductRepository;
import com.example.pms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getCart(String email) {
        return EntityMapper.toResponse(getOrCreateCart(email));
    }

    @Transactional
    public CartResponse addItem(String email, CartItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id " + request.productId()));
        if (!product.isEnabled()) {
            throw new BadRequestException("Product '" + product.getName() + "' is currently unavailable");
        }

        Cart cart = getOrCreateCart(email);
        CartItem existing = findItem(cart, product.getId());
        int newQuantity = request.quantity() + (existing == null ? 0 : existing.getQuantity());
        ensureStock(product, newQuantity);

        if (existing == null) {
            cart.addItem(new CartItem(product, newQuantity));
        } else {
            existing.setQuantity(newQuantity);
        }
        cartRepository.save(cart);
        return EntityMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(String email, Long productId, CartItemUpdateRequest request) {
        Cart cart = getOrCreateCart(email);
        CartItem item = findItem(cart, productId);
        if (item == null) {
            throw new ResourceNotFoundException("Product " + productId + " is not in your cart");
        }
        Product product = item.getProduct();
        if (!product.isEnabled()) {
            throw new BadRequestException("Product '" + product.getName() + "' is currently unavailable");
        }
        ensureStock(product, request.quantity());
        item.setQuantity(request.quantity());
        return EntityMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String email, Long productId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = findItem(cart, productId);
        if (item == null) {
            throw new ResourceNotFoundException("Product " + productId + " is not in your cart");
        }
        cart.getItems().remove(item); // orphanRemoval deletes the row
        return EntityMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        return EntityMapper.toResponse(cart);
    }

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    private CartItem findItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    private void ensureStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getQuantity()) {
            throw new InsufficientStockException("Only " + product.getQuantity()
                    + " unit(s) of '" + product.getName() + "' available, but " + requestedQuantity
                    + " requested");
        }
    }
}
