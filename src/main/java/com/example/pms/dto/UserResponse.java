package com.example.pms.dto;

import com.example.pms.entity.Role;

public record UserResponse(Long id, String name, String email, Role role) {
}
