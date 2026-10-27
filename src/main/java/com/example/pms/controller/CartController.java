package com.example.pms.controller;

import com.example.pms.dto.CartItemRequest;
import com.example.pms.dto.CartItemUpdateRequest;
import com.example.pms.dto.CartResponse;
import com.example.pms.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Authentication authentication,
                                                @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItem(authentication.getName(), request));
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateItem(Authentication authentication,
                                   @PathVariable Long productId,
                                   @Valid @RequestBody CartItemUpdateRequest request) {
        return cartService.updateItem(authentication.getName(), productId, request);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(Authentication authentication, @PathVariable Long productId) {
        return cartService.removeItem(authentication.getName(), productId);
    }

    @DeleteMapping
    public CartResponse clearCart(Authentication authentication) {
        return cartService.clearCart(authentication.getName());
    }
}
