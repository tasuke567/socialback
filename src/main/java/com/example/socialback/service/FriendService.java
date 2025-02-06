package com.example.socialback.service;

import com.example.socialback.model.FriendRequestResult;
import com.example.socialback.model.User;
import com.example.socialback.model.FriendshipStatus;
import com.example.socialback.model.FriendRequest;
import com.example.socialback.repository.FriendRepository;
import com.example.socialback.repository.FriendRequestRepository;
import com.example.socialback.repository.UserRepository;
import com.example.socialback.exception.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.neo4j.core.ReactiveNeo4jClient.log;


@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("User", "id", userId));
    }

    private boolean isAlreadyFriends(UUID userId1, UUID userId2) {
        return friendRepository.areFriends(userId1, userId2);
    }

    private FriendRequest findPendingFriendRequest(UUID fromUserId, UUID toUserId) {
        return friendRequestRepository.findFriendRequest(fromUserId, toUserId)
                .orElseThrow(() -> new CustomNotFoundException("Friend Request", "fromUserId", fromUserId));
    }

    @Transactional(readOnly = true)
    public List<User> getUserFriends(UUID userId) {
        getUserById(userId);
        return friendRepository.findAllFriends(userId);
    }

    @Transactional(readOnly = true)
    public boolean checkFriendship(UUID userId1, UUID userId2) {
        getUserById(userId1);
        getUserById(userId2);
        return isAlreadyFriends(userId1, userId2);
    }

    @Transactional
    public FriendRequestResult sendFriendRequest(UUID fromUserId, UUID toUserId) {
        if (fromUserId.equals(toUserId)) {
            return FriendRequestResult.INVALID_REQUEST;
        }

        User fromUser = getUserById(fromUserId);
        User toUser = getUserById(toUserId);

        if (friendRepository.areFriends(fromUserId, toUserId)) {
            return FriendRequestResult.ALREADY_FRIENDS;
        }

        Optional<FriendRequest> existingRequest =
                friendRequestRepository.findFriendRequest(fromUserId, toUserId);
        if (existingRequest.isPresent()) {
            return FriendRequestResult.ALREADY_PENDING;
        }

        friendRequestRepository.createFriendRequest(fromUserId, toUserId);
        return FriendRequestResult.SENT;
    }


    @Transactional
    public void acceptFriendRequest(UUID requestId, UUID acceptingUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomNotFoundException("FriendRequest", "id", requestId));


        if (!request.getToUser().equals(acceptingUserId)) {
            throw new IllegalArgumentException("Not authorized to accept this request");
        }

        if (request.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }


        friendRepository.createFriendship(request.getFromUser(), request.getToUser());
        friendRequestRepository.deleteById(requestId);
    }

    @Transactional
    public void removeFriend(UUID userId1, UUID userId2) {
        getUserById(userId1);
        getUserById(userId2);
        if (!isAlreadyFriends(userId1, userId2)) {
            throw new IllegalArgumentException("Not friends.");
        }
        friendRepository.removeFriendship(userId1, userId2);
    }
}
