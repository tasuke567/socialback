package com.example.socialback.model.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    @Builder.Default
    private String profilePicture = "default-avatar.png";
    @Builder.Default
    private String roles = "ROLE_USER";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> interests;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    // ✅ Constructor อื่น ๆ ที่อาจต้องการใช้
    public UserEntity(UUID id, String username, String email, String password, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    public UserEntity(
            UUID id,
            String username,
            String password,
            String email,
            String firstName,
            String lastName,
            String profilePicture,
            String roles,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;


    }

}
