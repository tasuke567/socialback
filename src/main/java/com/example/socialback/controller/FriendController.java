package com.example.socialback.controller;

import com.example.socialback.dto.FriendRequestDTO;
import com.example.socialback.service.FriendRequestResult;
import com.example.socialback.entity.UserEntity;
import com.example.socialback.service.FriendService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FriendController {
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);


    private final FriendService friendService;
    private final UserService userService; // Add this

    @GetMapping
    public ResponseEntity<List<UserEntity>> getCurrentUserFriends(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity currentUser = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(friendService.getUserFriends(currentUser.getId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserEntity>> getUserFriends(@PathVariable UUID userId) {
        return ResponseEntity.ok(friendService.getUserFriends(userId));
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FriendRequestDTO request) {
        // Logging request receipt
        logger.debug("Received POST /api/friends/request with payload: {}", request);

        UserEntity currentUser = userService.findByUsername(userDetails.getUsername());
        try {
            UUID fromUserId = currentUser.getId();  // จากการตรวจสอบ Username และดึงข้อมูลผู้ใช้
            UUID toUserId = UUID.fromString(request.getToUserId());
            FriendRequestResult result = friendService.sendFriendRequest(fromUserId, toUserId);

            return switch (result) {
                case SENT -> ResponseEntity.ok("Friend request sent successfully!");
                case ALREADY_FRIENDS -> ResponseEntity.badRequest().body("Already friends");
                case ALREADY_PENDING -> ResponseEntity.badRequest().body("Request already pending");
                case INVALID_REQUEST -> ResponseEntity.badRequest().body("Invalid request");
            };
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format in request: {}", request.getToUserId());
            return ResponseEntity.badRequest().body("Invalid UUID format");
        }
    }

    @DeleteMapping("/request/{requestId}")
    public ResponseEntity<?> cancelFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID requestId) {
        UserEntity currentUser = userService.findByUsername(userDetails.getUsername());

        logger.info("User {} is trying to cancel friend request {}", currentUser.getId(), requestId);



        boolean success = friendService.cancelFriendRequest(requestId, currentUser.getId());

        if (success) {
            return ResponseEntity.ok("Friend request canceled successfully");
        } else {
            return ResponseEntity.badRequest().body("Friend request not found or not allowed to cancel");
        }
    }




    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID friendId
    ) {
        UserEntity currentUser = userService.findByUsername(userDetails.getUsername());
        friendService.removeFriend(currentUser.getId(), friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{friendId}")
    public ResponseEntity<Boolean> checkFriendship(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID friendId
    ) {
        UserEntity currentUser = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(friendService.checkFriendship(currentUser.getId(), friendId));
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID requestId) {
        UserEntity currentUser = userService.findByUsername(userDetails.getUsername());

        logger.info(String.format("User %s is trying to accept request %s", currentUser.getId(), requestId));


        friendService.acceptFriendRequest(requestId, currentUser.getId());
        return ResponseEntity.ok("Friend request accepted");
    }

}