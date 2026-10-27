package com.example.pms.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int quantity,
        boolean enabled) {
}
