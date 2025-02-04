package com.example.socialback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import java.util.Date;

@Node
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private Long userId;

    // เพิ่มฟิลด์ createdAt
    private Date createdAt;

    // Constructor ที่มี createdAt
    public Post(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.createdAt = new Date(); // กำหนดให้เป็นวันที่ปัจจุบัน
    }

    public Post() {} // ต้องมี Default Constructor ด้วย

    // Getters และ Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
