package com.example.pms.config;

import com.example.pms.entity.Product;
import com.example.pms.entity.Role;
import com.example.pms.entity.User;
import com.example.pms.repository.ProductRepository;
import com.example.pms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Seeds one ADMIN account and a few sample products on first start. */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name:Admin}")
    private String adminName;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String email = adminEmail.trim().toLowerCase();
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(new User(adminName, email, passwordEncoder.encode(adminPassword), Role.ADMIN));
            log.info("Seeded ADMIN account: {} (change the password in application.properties)", email);
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product("Wireless Mouse", "2.4 GHz ergonomic wireless mouse",
                    new BigDecimal("599.00"), 50, true));
            productRepository.save(new Product("Mechanical Keyboard", "Blue switch, backlit keyboard",
                    new BigDecimal("2499.00"), 25, true));
            productRepository.save(new Product("USB-C Hub", "7-in-1 USB-C multiport adapter",
                    new BigDecimal("1299.00"), 30, true));
            productRepository.save(new Product("Laptop Stand", "Adjustable aluminium laptop stand",
                    new BigDecimal("899.00"), 15, true));
            productRepository.save(new Product("Webcam 1080p", "Full HD webcam with microphone",
                    new BigDecimal("1799.00"), 3, true));
            log.info("Seeded sample products");
        }
    }
}
