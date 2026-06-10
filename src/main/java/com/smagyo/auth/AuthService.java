package com.smagyo.auth;

import com.smagyo.security.JwtUtil;
import com.smagyo.tenant.Tenant;
import com.smagyo.tenant.TenantRepository;
import com.smagyo.user.User;
import com.smagyo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository     userRepository;
    private final TenantRepository   tenantRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtUtil            jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String tenantId   = user.getTenant() != null ? user.getTenant().getId()   : null;
        String tenantName = user.getTenant() != null ? user.getTenant().getName() : "Smagyo Platform";

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                tenantId,
                tenantName,
                user.getId()
        );

        return new LoginResponse(token, user.getRole().name(), tenantId, tenantName,
                user.getName(), buildAvatar(user.getName()));
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        if (tenantRepository.existsBySubdomain(request.subdomain().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subdomain already taken");
        }

        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name(request.storeName())
                .subdomain(request.subdomain().toLowerCase())
                .build());

        userRepository.save(User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .role(User.Role.TENANT_ADMIN)
                .tenant(tenant)
                .build());
    }

    private String buildAvatar(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
    }
}
