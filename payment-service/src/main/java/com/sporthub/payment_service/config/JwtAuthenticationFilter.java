package com.sporthub.payment_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (token != null && validateToken(token)) {
            try {
                Claims claims = getClaimsFromToken(token);
                String username = claims.getSubject();
                
                Object userIdClaim = claims.get("userId");
                Long userId = null;
                if (userIdClaim instanceof Number) {
                    userId = ((Number) userIdClaim).longValue();
                } else if (userIdClaim instanceof String) {
                    try {
                        userId = Long.parseLong((String) userIdClaim);
                    } catch (NumberFormatException ignored) {
                        
                    }
                }

                        
                List<SimpleGrantedAuthority> authorities;
                Object rolesClaim = claims.get("roles");
                if (rolesClaim instanceof List<?>) {
                    authorities = ((List<?>) rolesClaim).stream()
                        .filter(roleObj -> roleObj != null)
                        .map(Object::toString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                } else {
                    String role = claims.get("role", String.class);
                    authorities = role != null
                        ? List.of(new SimpleGrantedAuthority(role))
                        : List.of();
                }
                
                if (userId != null) {
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.error("JWT token missing or invalid userId claim");
                }
                
                
            } catch (Exception e) {
                logger.error("Cannot set user authentication: " + e.getMessage(), e);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/webhooks/") || 
               path.startsWith("/actuator/") ||
               path.startsWith("/h2-console/");
    }
}
