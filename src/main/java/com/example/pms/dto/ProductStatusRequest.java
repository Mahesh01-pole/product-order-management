package com.example.pms.dto;

import jakarta.validation.constraints.NotNull;

public record ProductStatusRequest(
        @NotNull(message = "enabled flag is required")
        Boolean enabled) {
}
