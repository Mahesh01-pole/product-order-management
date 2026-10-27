package com.example.pms.service;

import com.example.pms.dto.ProductRequest;
import com.example.pms.dto.ProductResponse;
import com.example.pms.dto.ProductUpdateRequest;
import com.example.pms.entity.Product;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.mapper.EntityMapper;
import com.example.pms.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(boolean isAdmin) {
        List<Product> products = isAdmin
                ? productRepository.findAllByOrderByIdAsc()
                : productRepository.findByEnabledTrueOrderByIdAsc();
        return products.stream().map(EntityMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id, boolean isAdmin) {
        Product product = findProduct(id);
        if (!isAdmin && !product.isEnabled()) {
            throw new ResourceNotFoundException("Product not found with id " + id);
        }
        return EntityMapper.toResponse(product);
    }


    @Transactional
    public ProductResponse addProduct(ProductRequest request) {
        boolean enabled = request.enabled() == null || request.enabled();
        Product product = new Product(request.name().trim(), request.description(),
                request.price(), request.quantity(), enabled);
        return EntityMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updatePriceAndQuantity(Long id, ProductUpdateRequest request) {
        Product product = findProduct(id);
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        return EntityMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse setEnabled(Long id, boolean enabled) {
        Product product = findProduct(id);
        product.setEnabled(enabled);
        return EntityMapper.toResponse(productRepository.save(product));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }
}
