package com.example.socialback.repository;

import org.springframework.stereotype.Repository;
import com.example.socialback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    // อื่น ๆ ตามต้องการ
}