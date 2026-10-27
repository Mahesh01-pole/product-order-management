package com.example.pms.controller;

import com.example.pms.dto.ProductResponse;
import com.example.pms.service.ProductService;
import com.example.pms.util.AuthUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getProducts(Authentication authentication) {
        return productService.getProducts(AuthUtils.isAdmin(authentication));
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id, Authentication authentication) {
        return productService.getProduct(id, AuthUtils.isAdmin(authentication));
    }
}
