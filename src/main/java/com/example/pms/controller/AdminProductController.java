package com.example.pms.controller;

import com.example.pms.dto.ProductRequest;
import com.example.pms.dto.ProductResponse;
import com.example.pms.dto.ProductStatusRequest;
import com.example.pms.dto.ProductUpdateRequest;
import com.example.pms.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(request));
    }

    @PutMapping("/{id}")
    public ProductResponse updatePriceAndQuantity(@PathVariable Long id,
                                                  @Valid @RequestBody ProductUpdateRequest request) {
        return productService.updatePriceAndQuantity(id, request);
    }

    @PatchMapping("/{id}/status")
    public ProductResponse setStatus(@PathVariable Long id,
                                     @Valid @RequestBody ProductStatusRequest request) {
        return productService.setEnabled(id, request.enabled());
    }
}
