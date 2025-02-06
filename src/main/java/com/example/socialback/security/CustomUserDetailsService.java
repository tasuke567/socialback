package com.example.socialback.security;


import com.example.socialback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 Loading user: " + username);
        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("✅ User loaded: " + user.getUsername());
                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            user.getRoles().stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                    );
                })
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: " + username);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
