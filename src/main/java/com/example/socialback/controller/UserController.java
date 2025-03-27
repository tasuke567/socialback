package com.example.socialback.controller;

import com.example.socialback.model.dto.UserInterestDTO;
import com.example.socialback.model.dto.UserInterestListDTO;
import com.example.socialback.model.entity.UserInterestEntity;
import com.example.socialback.service.UserInterestService;
import com.example.socialback.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.socialback.model.dto.InterestListRequest;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserInterestService userInterestService;
    private final UserService userService;

    // ตรวจสอบว่าผู้ใช้มีความสนใจหรือไม่
    @GetMapping("/{userId}/interests/check")
    public ResponseEntity<Map<String, Boolean>> checkUserInterests(@PathVariable UUID userId) {
        boolean hasInterests = userInterestService.hasUserInterests(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasInterests", hasInterests);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/interests")
    public ResponseEntity<UserInterestDTO> addUserInterest(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> payload) {

        String interest = payload.get("interest");
        if (interest == null || interest.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new UserInterestDTO(userId, interest));
        }

        // Get actual user ID from authentication
        UUID authUserId = userService.findByUsername(userDetails.getUsername()).getId();

        if (!userId.equals(authUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new UserInterestDTO(userId, interest));
        }

        if (userInterestService.existsByUserIdAndInterest(userId, interest)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new UserInterestDTO(userId, interest));
        }

        return ResponseEntity.ok(new UserInterestDTO(userId, interest));
    }

    @PostMapping("/{userId}/interests/list")
    public ResponseEntity<UserInterestListDTO> addUserInterestList(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody InterestListRequest payload) {

        List<String> interests = payload.getInterests();
        if (interests == null || interests.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new UserInterestListDTO(userId, interests, 0));
        }

        // Get actual user ID from authentication
        UUID authUserId = userService.findByUsername(userDetails.getUsername()).getId();

        if (!userId.equals(authUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new UserInterestListDTO(userId, interests, 0));
        }

        int addedCount = 0;
        for (String interest : interests) {
            if (interest == null || interest.isBlank()) {
                continue;
            }
            if (userInterestService.existsByUserIdAndInterest(userId, interest)) {
                continue;
            }
            int result = userInterestService.addUserInterest(userId, interest);
            if (result > 0) {
                addedCount++;
            }
        }

        return ResponseEntity.ok(new UserInterestListDTO(userId, interests, addedCount));
    }

    // ✅ แก้ไขความสนใจ (Update)
    @PutMapping("/{userId}/interests/{interestId}")
    public ResponseEntity<Map<String, Object>> updateUserInterest(
            @PathVariable UUID userId,
            @PathVariable UUID interestId,
            @RequestBody Map<String, String> payload) {
        // สมมุติว่า payload ส่งมาในรูปแบบ { "interest": "New Interest" }
        String newInterest = payload.get("interest");
        int result = userInterestService.updateUserInterest(interestId, newInterest);
        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("message", "Interest updated successfully");
        } else {
            response.put("message", "Failed to update interest");
        }
        return ResponseEntity.ok(response);
    }

    // ✅ ลบความสนใจ (Delete)
    @DeleteMapping("/{userId}/interests/{interestId}")
    public ResponseEntity<Map<String, Object>> deleteUserInterest(
            @PathVariable UUID userId,
            @PathVariable UUID interestId) {
        int result = userInterestService.deleteUserInterest(interestId);
        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("message", "Interest deleted successfully");
        } else {
            response.put("message", "Failed to delete interest");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/interests")
    public ResponseEntity<List<UserInterestDTO>> getUserInterests(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Authorization check
        UUID authUserId = userService.findByUsername(userDetails.getUsername()).getId();
        if (!userId.equals(authUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UserInterestEntity> interests = userInterestService.getUserInterests(userId);
        List<UserInterestDTO> dtos = interests.stream()
                .map(interest -> new UserInterestDTO(
                        interest.getUserId(),
                        interest.getInterest()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
