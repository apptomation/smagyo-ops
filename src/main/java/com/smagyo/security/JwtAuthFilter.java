package com.smagyo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs once per request. Extracts the JWT from the Authorization header,
 * validates it, sets the Spring Security context, and stores the tenantId
 * in TenantContext so all downstream services can use it without touching
 * the request directly.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (jwtUtil.isTokenValid(token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                String email    = jwtUtil.extractEmail(token);
                String role     = jwtUtil.extractRole(token);
                String tenantId = jwtUtil.extractTenantId(token);

                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                }

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {
            // Invalid token → request continues unauthenticated; Spring Security
            // will reject it at the authorization stage if the endpoint requires auth.
        } finally {
            filterChain.doFilter(request, response);
            TenantContext.clear(); // Always clean up — prevents leaking into next request
        }
    }
}
