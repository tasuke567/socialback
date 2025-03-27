package com.example.socialback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    // ✅ Constructor สำหรับข้อมูลพื้นฐาน
    public UserDTO(UUID id, String username, String email, String profilePicture) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    // ✅ Constructor สำหรับการดึงข้อมูลเพื่อน (ไม่รวม security fields)
    public UserDTO(UUID id, String firstName, String lastName, String profilePicture, String username, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    
}
