package com.example.socialback.repository;

import com.example.socialback.entity.PostShareEntity;
import com.example.socialback.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface PostShareRepository extends JpaRepository<PostShareEntity, UUID> {
    // ตัวอย่าง query: หา share ทั้งหมดของโพสต์หนึ่ง ๆ
    List<PostShareEntity> findByPostId(UUID postId);

    List<PostShareEntity> findAllByUser(UserEntity user);
    List<PostShareEntity> findAllByUserId(UUID  user);

}
