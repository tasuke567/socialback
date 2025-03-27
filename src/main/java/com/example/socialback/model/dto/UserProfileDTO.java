package com.example.socialback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // ✅ เพิ่ม Default Constructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {
    private UUID id;
    private String username;
    private String email;
    private String profilePicture;
    private List<String> interests;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer postsCount;
    private Integer followersCount;
    private Integer followingCount;

    public UserProfileDTO(UUID id, String username, String email, String profilePicture) {
        this.id = id; // ✅ ใช้ค่าจากฐานข้อมูล
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public UserProfileDTO(UUID id, String username, String email, String profilePicture,
            List<String> interests, String firstName, String lastName,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.interests = interests;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserProfileDTO(String username, String email, String profilePicture, String firstName, String lastName,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserProfileDTO(UUID id, String username, String email, String profilePicture, String firstName,
            String lastName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests != null ? interests : new ArrayList<>();
    }
}
