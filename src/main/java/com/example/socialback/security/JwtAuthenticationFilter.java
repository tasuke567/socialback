package com.example.socialback.security;

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

        String jwt = extractJwtFromRequest(request);

        if (jwt != null && jwtUtil.validateTokenStructure(jwt)) {
            String username = jwtUtil.extractUsername(jwt);


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Pass userId along with userDetails to validate the token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    setAuthenticationInContext(request, userDetails);
                }
            }
        }

        // Proceed with the request
        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request's Authorization header.
     * @param request the HTTP request
     * @return the JWT token if present, otherwise null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Sets the authentication details in the SecurityContext for the current request.
     * @param request the HTTP request
     * @param userDetails the UserDetails object to be used for authentication
     */
    private void setAuthenticationInContext(HttpServletRequest request, UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        // Adding details like remote IP address into the authentication object
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Setting the authentication object in the context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

