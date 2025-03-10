package com.example.socialback.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostShareEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    // ✅ ป้องกัน StackOverflowError โดยใช้ @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private PostEntity post;

    // ✅ ใช้ JsonIgnore ป้องกันข้อมูลผู้ใช้รั่วไหล
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ✅ ตั้งเวลาอัตโนมัติเมื่อมีการสร้างแชร์โพสต์
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PostShareEntity(PostEntity post, UserEntity user) {
        this.post = post;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
