package com.example.socialback.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInterestListDTO {
    private UUID userId;
    private List<String> interests;
    private int addedCount;
} 