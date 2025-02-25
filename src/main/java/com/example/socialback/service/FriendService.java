package com.example.socialback.service;

import com.example.socialback.entity.FriendshipEntity;
import com.example.socialback.entity.UserEntity;
import com.example.socialback.entity.FriendRequestEntity;
import com.example.socialback.repository.FriendRequestRepository;
import com.example.socialback.repository.FriendshipRepository;
import com.example.socialback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository; // เดี๋ยวจะมีตัวอย่าง FriendRequestEntity อีกที
    private final UserRepository userRepository; // ใช้ JPA Repo ของ UserEntity

    public List<UserEntity> getUserFriends(UUID userId) {
        // เช็ค user มีอยู่จริง
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ดึง List ของ UUID friendId จาก FriendshipEntity
        List<UUID> friendIds = friendshipRepository.findAllFriendIds(userId);

        // เอา friendIds มาหา UserEntity
        return userRepository.findAllById(friendIds);
    }

    public boolean areFriends(UUID userId1, UUID userId2) {
        return friendshipRepository.areFriends(userId1, userId2);
    }

    public void createFriendship(UUID userId1, UUID userId2) {
        // Check if the friendship already exists
        if (friendshipRepository.areFriends(userId1, userId2)) {
            throw new RuntimeException("Friendship already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // Create and save the friendship entities
        FriendshipEntity friendship1 = FriendshipEntity.builder()
                .userId(userId1)
                .friendId(userId2)
                .status("ACCEPTED")
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        FriendshipEntity friendship2 = FriendshipEntity.builder()
                .userId(userId2)
                .friendId(userId1)
                .status("ACCEPTED")
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);
    }

    public void removeFriendship(UUID userId1, UUID userId2) {
        friendshipRepository.removeFriendship(userId1, userId2);
    }

    public FriendRequestResult sendFriendRequest(UUID fromUserId, UUID toUserId) {
        // Check if the friend request already exists
        if (friendRequestRepository.findPendingRequest(fromUserId, toUserId).isPresent()) {
            return FriendRequestResult.ALREADY_PENDING; // Return if a request is already pending
        }

        // Check if they are already friends
        if (friendshipRepository.areFriends(fromUserId, toUserId)) {
            return FriendRequestResult.ALREADY_FRIENDS; // Return if they are already friends
        }

        // Create and save the new friend request
        FriendRequestEntity friendRequest = FriendRequestEntity.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .status("PENDING") // Set status to PENDING
                .createdAt(LocalDateTime.now())
                .build();

        friendRequestRepository.save(friendRequest);
        return FriendRequestResult.SENT; // Return success result
    }

    public boolean cancelFriendRequest(UUID requestId, UUID userId) {
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Check if the user is authorized to cancel the request
        if (!request.getFromUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to cancel this request");
        }

        friendRequestRepository.deleteById(requestId);
        return true; // Return true if cancellation was successful
    }

    public void removeFriend(UUID userId, UUID friendId) {
        // Check if the friendship exists
        if (!friendshipRepository.areFriends(userId, friendId)) {
            throw new RuntimeException("Not friends or friendship does not exist");
        }
        
        // Remove the friendship
        friendshipRepository.removeFriendship(userId, friendId);
    }

    public boolean checkFriendship(UUID userId1, UUID userId2) {
        return friendshipRepository.areFriends(userId1, userId2);
    }

    public void acceptFriendRequest(UUID requestId, UUID userId) {
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getToUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to accept this request");
        }

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Request not pending");
        }

        // Create friendship
        LocalDateTime now = LocalDateTime.now();
        FriendshipEntity friendship1 = FriendshipEntity.builder()
                .userId(request.getFromUserId())
                .friendId(request.getToUserId())
                .status("ACCEPTED")
                .createdAt(now)
                .updatedAt(now)
                .build();

        FriendshipEntity friendship2 = FriendshipEntity.builder()
                .userId(request.getToUserId())
                .friendId(request.getFromUserId())
                .status("ACCEPTED")
                .createdAt(now)
                .updatedAt(now)
                .build();

        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);

        // Delete the friend request
        friendRequestRepository.deleteById(requestId);
    }

    // etc. สำหรับ manage FriendRequests
}
