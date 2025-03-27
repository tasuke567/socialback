package com.example.socialback.model.entity;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInterestEntity {
    private UUID id;
    private UUID userId;
    private String interest;
}
