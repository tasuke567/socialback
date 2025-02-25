package com.example.socialback.service;

import com.example.socialback.entity.FriendRequestEntity;
import com.example.socialback.entity.FriendshipEntity;
import com.example.socialback.repository.FriendRequestRepository;
import com.example.socialback.repository.FriendshipRepository;
import com.example.socialback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository; // ถ้าอยากรับแอดแล้วสร้าง friendship
    private final UserRepository userRepository;

    public FriendRequestEntity sendFriendRequest(UUID fromUserId, UUID toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("Cannot friend yourself");
        }

        // เช็คว่าผู้ใช้สองคนนี้มีอยู่
        userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + fromUserId));
        userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + toUserId));

        // เช็คว่าเคยเป็นเพื่อนกันแล้วหรือยัง
        if (friendshipRepository.areFriends(fromUserId, toUserId)) {
            throw new RuntimeException("Already friends");
        }

        // เช็คว่ามี pending request ไหม
        Optional<FriendRequestEntity> existing = friendRequestRepository.findPendingRequest(fromUserId, toUserId);
        if (existing.isPresent()) {
            throw new RuntimeException("Request already pending");
        }

        FriendRequestEntity request = FriendRequestEntity.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return friendRequestRepository.save(request);
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

    public void rejectFriendRequest(UUID requestId, UUID userId) {
        // คล้าย ๆ accept, แต่ไม่สร้าง friendship
        friendRequestRepository.deleteById(requestId);
    }

    public void cancelFriendRequest(UUID requestId, UUID fromUserId) {
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getFromUserId().equals(fromUserId)) {
            throw new RuntimeException("Not authorized to cancel this request");
        }
        friendRequestRepository.deleteById(requestId);
    }
}