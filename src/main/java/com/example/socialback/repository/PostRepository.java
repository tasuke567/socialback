package com.example.socialback.repository;

import com.example.socialback.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    // สามารถเพิ่ม query method หรือ custom query ได้ถ้าจำเป็น
}
