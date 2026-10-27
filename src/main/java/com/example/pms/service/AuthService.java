package com.example.pms.service;

import com.example.pms.dto.RegisterRequest;
import com.example.pms.dto.UserResponse;
import com.example.pms.entity.Cart;
import com.example.pms.entity.Role;
import com.example.pms.entity.User;
import com.example.pms.exception.DuplicateResourceException;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.mapper.EntityMapper;
import com.example.pms.repository.CartRepository;
import com.example.pms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       CartRepository cartRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered: " + email);
        }
        User user = new User(request.name().trim(), email,
                passwordEncoder.encode(request.password()), Role.USER);
        user = userRepository.save(user);
        cartRepository.save(new Cart(user));
        return EntityMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return EntityMapper.toResponse(user);
    }
}
