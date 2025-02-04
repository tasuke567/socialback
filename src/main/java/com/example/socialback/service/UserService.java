package com.example.socialback.service;

import com.example.socialback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void someMethod() {
        // ใช้ Neo4j Operations หรือ Repository ได้ตรงนี้
        // เช่น userRepository.findByUsername("john");
    }
}