package com.example.socialback.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Data
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @Size(min = 8)
    private String password;
    private String firstName;  // Add
    private String lastName;   // Add

    private String token;

    public RegisterRequest(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }



}