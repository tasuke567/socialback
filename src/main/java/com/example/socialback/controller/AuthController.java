package com.example.socialback.controller;

import com.example.socialback.model.dto.LoginRequest;
import com.example.socialback.model.dto.RegisterRequest;
import com.example.socialback.model.dto.UserProfileDTO;
import com.example.socialback.model.dto.UserProfileWithStatusDTO;
import com.example.socialback.model.entity.FriendshipStatus;
import com.example.socialback.model.entity.UserEntity;
import com.example.socialback.service.AuthService;
import com.example.socialback.service.UserService;
import com.example.socialback.service.FriendService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.example.socialback.model.dao.UserInterestDAO;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    // Inject ค่า properties
    @Value("${profile.upload.dir}")
    private String uploadDirPath;

    @Value("${profile.url.base}")
    private String profileUrlBase;

    private final AuthService authService;
    private final UserService userService;
    private final UserInterestDAO userInterestDAO;
    private final FriendService friendService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.debug("Received registration request: {}", request);
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String token = request.getHeader("Authorization").substring("Bearer ".length()).trim();
        return authService.getCurrentUser(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok().body("Logged out successfully");
    }

    // ✅ ดูโปรไฟล์ตัวเอง
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserProfileDTO profile = userService.getProfileByUsername(userDetails.getUsername());

        // แปลง path รูปภาพให้เป็น URL ที่เข้าถึงได้ โดยใช้ค่า profileUrlBase จาก
        // properties
        if (profile.getProfilePicture() != null && !profile.getProfilePicture().isEmpty()) {
            String profilePicture = profile.getProfilePicture();
            logger.info("Converting profile picture path to URL");

            if (!profilePicture.startsWith("http")) {
                String imageUrl = profileUrlBase + profilePicture;
                profile.setProfilePicture(imageUrl);
            }
        }

        return ResponseEntity.ok(profile);
    }

    // ✅ อัปเดตโปรไฟล์ตัวเอง
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("userProfile") String userProfileJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserProfileDTO userProfileDTO = objectMapper.readValue(userProfileJson, UserProfileDTO.class);

            String profilePictureUrl = userProfileDTO.getProfilePicture(); // ใช้ค่าเดิมก่อน
            if (profilePicture != null && !profilePicture.isEmpty()) {
                // สร้างชื่อไฟล์ใหม่ที่ไม่ซ้ำกัน
                String filename = UUID.randomUUID() + "-" + profilePicture.getOriginalFilename();
                Path uploadDir = Paths.get(uploadDirPath);

                // ✅ ตรวจสอบและสร้างโฟลเดอร์ ถ้ายังไม่มี
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path targetLocation = uploadDir.resolve(filename);
                Files.copy(profilePicture.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // 🔹 ใช้ URL Base + ชื่อไฟล์
                profilePictureUrl = profileUrlBase + filename;
            }

            // ✅ อัปเดตข้อมูลโปรไฟล์
            UserProfileDTO updatedProfile = userService.updateProfile(
                    userDetails.getUsername(),
                    userProfileDTO.getFirstName(),
                    userProfileDTO.getLastName(),
                    profilePictureUrl);

            return ResponseEntity.ok(updatedProfile);

        } catch (Exception e) {
            logger.error("Error updating profile", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ ดูโปรไฟล์คนอื่น เปลี่ยน Mapping ให้เป็น /profile/{userId}
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getProfileById(userId));
    }

    // ✅ เช็คว่าผู้ใช้มีความสนใจหรือไม่ โดยไม่ให้เกิด conflict กับ Mapping
    @GetMapping("/has-interests")
    public ResponseEntity<Boolean> checkUserHasInterests(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.findByUsername(userDetails.getUsername());
        // ใช้ DAO ในการ query ตาราง user_interests โดยส่ง user.getId()
        boolean hasInterests = !userInterestDAO.findByUserId(user.getId()).isEmpty();
        return ResponseEntity.ok(hasInterests);
    }

    // ✅ ค้นหาผู้ใช้
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String query, 
            @AuthenticationPrincipal UserDetails userDetails) {
    
        // 🛑 ป้องกัน `query` ที่ไม่ถูกต้อง
        if (!StringUtils.hasText(query) || query.trim().length() < 2) {
            logger.warn("⚠️ Search query is too short");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Search query must be at least 2 characters long."));
        }
    
        try {
            // ✨ ดึงข้อมูลของ user ที่กำลังล็อกอินอยู่
            var currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated."));
            }
    
            // 🔍 ค้นหา users ที่ตรงกับ query
            List<UserProfileDTO> users = userService.searchUsers(query.trim());
    
            // ✅ ตรวจสอบสถานะความสัมพันธ์กับ user ที่กำลังล็อกอิน
            List<UserProfileWithStatusDTO> usersWithStatus = users.stream()
                .map(user -> {
                    logger.info("🔍 Checking friendship status for user: {}", user);
                    FriendshipStatus friendshipStatus = friendService.getFriendshipStatus(currentUser.getId(), user.getId());
                    return new UserProfileWithStatusDTO(user, friendshipStatus);
                })
                .collect(Collectors.toList());
    
            // ✅ ส่ง JSON Response ที่มี `friendshipStatus` กลับไปด้วย
            return ResponseEntity.ok(Map.of(
                    "message", "Search successful",
                    "results", usersWithStatus
            ));
        } catch (Exception ex) {
            logger.error("❌ Error searching users", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred while searching for users."));
        }
    }

    // Define a new class for the response
    public static class SearchResponse {
        private List<UserProfileDTO> users;
        private String message;

        public SearchResponse(List<UserProfileDTO> users, String message) {
            this.users = users;
            this.message = message;
        }

        public List<UserProfileDTO> getUsers() {
            return users;
        }

        public void setUsers(List<UserProfileDTO> users) {
            this.users = users;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
