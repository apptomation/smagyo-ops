package com.smagyo.config;

import com.smagyo.user.User;
import com.smagyo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the default SUPER_ADMIN on first startup.
 * Only runs if the account doesn't already exist.
 * Change the credentials via environment variables before going to production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email    = System.getenv().getOrDefault("SUPER_ADMIN_EMAIL",    "chadi@smagyo.com");
        String password = System.getenv().getOrDefault("SUPER_ADMIN_PASSWORD", "super1234");
        String name     = System.getenv().getOrDefault("SUPER_ADMIN_NAME",     "Chadi Rahme");

        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(User.Role.SUPER_ADMIN)
                    .tenant(null)
                    .build());
            log.info("✅ Default super admin created → email: {}  password: {}", email, password);
        } else {
            log.info("ℹ️  Super admin already exists: {}", email);
        }
    }
}
