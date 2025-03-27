package com.example.socialback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.socialback.security.JwtUtil;

@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService; // ให้แน่ใจว่ามี bean นี้ใน context

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // ดึง username จาก token
                String username = jwtUtil.extractUsername(token);
                if (username == null) {
                    throw new IllegalArgumentException("Token does not contain a valid username");
                }
                // โหลด user details ด้วย username ที่ได้จาก token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // ตรวจสอบ token โดยส่ง userDetails ไปด้วย
                if (!jwtUtil.validateToken(token, userDetails)) {
                    throw new IllegalArgumentException("Invalid token");
                }
                // หากต้องการ set authentication object ให้กับ accessor
                // accessor.setUser(new UsernamePasswordAuthenticationToken(userDetails, null,
                // userDetails.getAuthorities()));
            } else {
                throw new IllegalArgumentException("Missing Authorization header");
            }
        }
        return message;
    }
}
