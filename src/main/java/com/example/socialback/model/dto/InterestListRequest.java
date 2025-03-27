package com.example.socialback.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class InterestListRequest {
    @NotNull(message = "Interests list cannot be null")
    @NotEmpty(message = "Interests list cannot be empty")
    private List<String> interests;

    // Getters and Setters
    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
} 