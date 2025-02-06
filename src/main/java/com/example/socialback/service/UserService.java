package com.example.socialback.service;

import com.example.socialback.model.User;
import com.example.socialback.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds a user by their username
     *
     * @param username the username to search for
     * @return the found user
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Checks if a user exists by their ID
     *
     * @param userId the ID to check
     * @return true if the user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Finds a user by their ID
     *
     * @param userId the ID to search for
     * @return the found user
     * @throws IllegalArgumentException if no user is found with the given ID
     */
    @Transactional(readOnly = true)
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }
}