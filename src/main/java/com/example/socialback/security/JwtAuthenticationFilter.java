package com.example.socialback.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // ข้ามการตรวจสอบ JWT สำหรับ API บางตัว เช่น /api/auth/register, /api/auth/login
        if (requestURI.startsWith("/api/auth/")) {
            logger.debug("Skipping JWT authentication for: " + requestURI);
            chain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtFromRequest(request);
        logger.debug("Extracted JWT: {}", jwt);

        if (jwt != null && jwtUtil.validateTokenStructure(jwt)) {
            logger.info("JWT token structure is valid.");
            String username = jwtUtil.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("Username extracted from token: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.info("JWT token validated successfully for user: {}", username);
                    setAuthenticationInContext(request, userDetails, jwt);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }
            } else {
                logger.warn("Username is null or authentication context is already set.");
            }
        } else {
            logger.warn("JWT token is either null or missing in request.");
        }

        chain.doFilter(request, response);
    }


    /**
     * Extracts the JWT token from the request's Authorization header.
     * @param request the HTTP request
     * @return the JWT token if present, otherwise null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        logger.debug("Extracted Bearer Token: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    /**
     * Sets the authentication details in the SecurityContext for the current request.
     * @param request the HTTP request
     * @param userDetails the UserDetails object to be used for authentication
     */
    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails, String jwtToken) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, jwtToken, authorities);

        // Adding details like remote IP address into the authentication object
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Setting the authentication object in the context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Debug log
        logger.info("Authentication set in context for user: {}", userDetails.getUsername());
    }

}
