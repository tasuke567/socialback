package com.example.socialback.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    // ✅ ป้องกัน StackOverflowError โดยใช้ @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY) // ใช้ LAZY เพื่อไม่โหลด Post ทั้งหมด
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY) // ใช้ LAZY เพื่อไม่โหลด User ทั้งหมด
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user; // ใช้ JsonIgnore ป้องกันข้อมูลผู้ใช้รั่วไหล

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ✅ ให้สร้างเวลาอัตโนมัติเมื่อกดไลก์
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
