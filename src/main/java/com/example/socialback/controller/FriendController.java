package com.example.socialback.controller;

import com.example.socialback.model.dto.FriendRequestDTO;
import com.example.socialback.model.entity.UserEntity;
import com.example.socialback.service.FriendRequestResult;
import com.example.socialback.service.FriendService;
import com.example.socialback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    // ✅ Get friends list  
    @GetMapping("/list")
    public ResponseEntity<List<UserEntity>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<UserEntity> friends = friendService.getFriends(user.getId());
        return ResponseEntity.ok(friends);
    }
    // ✅ Get sent friend requests (outgoing)
    @GetMapping("/sent-requests")
    public ResponseEntity<List<FriendRequestDTO>> getSentRequests(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<FriendRequestDTO> sentRequests = friendService.getSentFriendRequests(user.getId());
        return ResponseEntity.ok(sentRequests);
    }

    // ✅ Get suggestions (friends of friends)  
    @GetMapping("/suggestions")
    public ResponseEntity<List<UserEntity>> getSuggestions(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<UserEntity> suggestions = friendService.getSuggestions(user.getId());
        return ResponseEntity.ok(suggestions);
    }

    // ✅ ดึงคำขอเป็นเพื่อนที่ค้างอยู่ (incoming)
    @GetMapping("/pending-requests")
    public ResponseEntity<List<FriendRequestDTO>> getPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            var user = userService.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
            List<FriendRequestDTO> pendingRequests = friendService.getPendingRequests(user.getId());
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            logger.error("Error fetching pending friend requests", e);
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }
    // ✅ Get all friend requests (incoming and outgoing)   
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestDTO>> getRequests(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        List<FriendRequestDTO> requests = friendService.getRequests(user.getId());
        return ResponseEntity.ok(requests);
    }

    // ✅ ส่งคำขอเป็นเพื่อน
    @PostMapping("/request/{userId}")
    public ResponseEntity<Map<String, String>> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userId) {
        try {
            var fromUser = userService.findByUsername(userDetails.getUsername());
            if (fromUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }
            if (fromUser.getId().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cannot send friend request to yourself"));
            }
            FriendRequestResult result = friendService.sendFriendRequest(fromUser.getId(), userId);
            return switch (result) {
                case SENT -> ResponseEntity.ok(Map.of("message", "Friend request sent successfully"));
                case ALREADY_FRIENDS -> ResponseEntity.badRequest().body(Map.of("message", "Already friends"));
                case ALREADY_PENDING -> ResponseEntity.badRequest().body(Map.of("message", "Request already pending"));
                case ACCEPTED -> ResponseEntity.ok(Map.of("message", "Friend request accepted"));
                case DECLINED -> ResponseEntity.ok(Map.of("message", "Friend request declined"));
                case CANCELLED -> ResponseEntity.ok(Map.of("message", "Friend request cancelled"));
                case INVALID_REQUEST -> ResponseEntity.badRequest().body(Map.of("message", "User not found"));
                default -> ResponseEntity.internalServerError().body(Map.of("message", "Error processing request"));
            };
        } catch (Exception e) {
            logger.error("Error processing friend request", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error processing request"));
        }
    }

        // ✅ Accept friend request
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID requestId) {
        try {
            var user = userService.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            boolean success = friendService.acceptFriendRequest(requestId, user.getId());
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Friend request accepted"));
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "Request not found or expired"));
            }
        } catch (Exception e) {
            logger.error("Error accepting friend request", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error processing request"));
        }
    }
        // ✅ Decline friend request    
    @PutMapping("/decline/{requestId}")
    public ResponseEntity<Map<String, String>> declineFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID requestId) {
        try {
            var user = userService.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }
            friendService.declineFriendRequest(requestId, user.getId());
            return ResponseEntity.ok(Map.of("message", "Friend request declined"));
        } catch (Exception e) {
            logger.error("Error declining friend request", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error processing request"));
        }
    }   

    // ✅ Cancel friend request
    @DeleteMapping("/cancel/{userId}")
    public ResponseEntity<Map<String, String>> cancelFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userId) {
        try {
            var fromUser = userService.findByUsername(userDetails.getUsername());
            if (fromUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            if (fromUser.getId().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cannot cancel friend request to yourself"));
            }

            boolean success = friendService.cancelFriendRequest(fromUser.getId(), userId);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Friend request cancelled successfully"));
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "Friend request not found or already processed"));
            }
        } catch (Exception e) {
            logger.error("Error cancelling friend request", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error processing request"));
        }
    }

    // ✅ Remove friend
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<Map<String, String>> removeFriend(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userId) {
        try {
            var user = userService.findByUsername(userDetails.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
            }

            if (user.getId().equals(userId)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cannot remove yourself"));
            }

            friendService.removeFriend(user.getId(), userId);
            return ResponseEntity.ok(Map.of("message", "Friend removed successfully"));
        } catch (Exception e) {
            logger.error("Error removing friend", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Error processing request"));
        }
    }
}
