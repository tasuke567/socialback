package com.example.socialback.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class LoginRequest {
    // Setters
    // Getters
    private String email;
    private String password;
    private String username;

}