package com.example.socialback.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Date;
import java.util.UUID;

@Node
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Post {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String content;
    private UUID userId;

    // เพิ่มฟิลด์ใหม่ตาม frontend
    private String username;
    private String profilePicture;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    // Constructor สำหรับการสร้าง Post ใหม่
    public Post(String title, String content, UUID userId, String username, String profilePicture) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.profilePicture = profilePicture;
        this.createdAt = new Date();
    }

    public Post() {
    }
}