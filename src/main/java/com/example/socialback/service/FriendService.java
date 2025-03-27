package com.example.socialback.service;

import com.example.socialback.model.dao.FriendDAO;
import com.example.socialback.model.dto.FriendRequestDTO;
import com.example.socialback.model.dto.UserDTO;
import com.example.socialback.model.entity.FriendshipStatus;
import com.example.socialback.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendDAO friendDAO;
    private static final Logger logger = LoggerFactory.getLogger(FriendService.class);

    // ดึงเพื่อนของ user
    public List<UserEntity> getFriends(UUID userId) {
        return friendDAO.getFriends(userId);
    }

    // ดึงเพื่อนของ user
    public List<UserEntity> getSuggestions(UUID userId) {
        return friendDAO.getSuggestions(userId);
    }

    // ดึงคำขอเป็นเพื่อน
    public List<FriendRequestDTO> getRequests(UUID userId) {
        return friendDAO.getRequests(userId);
    }

    // ดึงเพื่อนของ user
    public List<UserDTO> getUserFriends(UUID userId) {
        return friendDAO.getUserFriends(userId);
    }

    // ดึงรายการคำขอเป็นเพื่อนที่ยังรอการตอบรับ (incoming)
    public List<FriendRequestDTO> getPendingRequests(UUID userId) {
        return friendDAO.getPendingRequests(userId);
    }

    // ดึงรายการคำขอเป็นเพื่อนที่ส่งออกไป (outgoing) โดย user ที่เป็นผู้ส่ง
    public List<FriendRequestDTO> getSentFriendRequests(UUID userId) {
        return friendDAO.getSentFriendRequests(userId);
    }

    // ดึงรายชื่อเพื่อนแนะนำ
    public List<UserDTO> getFriendSuggestions(UUID userId) {
        return friendDAO.getFriendSuggestions(userId);
    }

    @Transactional
    public FriendRequestResult sendFriendRequest(UUID fromUserId, UUID toUserId) {
        // 🛑 Validation: ป้องกันการส่งคำขอไปหา null user
        Objects.requireNonNull(fromUserId, "fromUserId must not be null");
        Objects.requireNonNull(toUserId, "toUserId must not be null");

        // ❌ ป้องกันการส่งคำขอหาตัวเอง
        if (fromUserId.equals(toUserId)) {
            logger.warn("⚠️ User [{}] tried to send friend request to themselves!", fromUserId);
            return FriendRequestResult.INVALID_REQUEST;
        }

        logger.info("📨 User [{}] is sending a friend request to [{}]", fromUserId, toUserId);

        // 🎯 เรียก DAO และจัดการ Response
        Optional<FriendRequestResult> result = Optional.ofNullable(friendDAO.sendFriendRequest(fromUserId, toUserId));

        if (result.isEmpty()) {
            logger.error("❌ Unexpected error while sending friend request from [{}] to [{}]", fromUserId, toUserId);
            return FriendRequestResult.INVALID_REQUEST;
        }

        // 🟢 Log result และ return ค่าที่ได้
        logger.info("✅ Friend request result: [{}]", result.get());
        return result.get();
    }

    // ยอมรับคำขอเป็นเพื่อน
    @Transactional
    public boolean acceptFriendRequest(UUID requestId, UUID currentUserId) {
        return friendDAO.acceptFriendRequest(requestId, currentUserId);
    }

    // ปฏิเสธคำขอเป็นเพื่อน
    @Transactional
    public boolean declineFriendRequest(UUID requestId, UUID currentUserId) {
        return friendDAO.declineFriendRequest(requestId, currentUserId);
    }

    // ยกเลิกคำขอเป็นเพื่อนที่ส่งออกไป (โดยผู้ส่ง)
    @Transactional
    public boolean cancelFriendRequest(UUID fromUserId, UUID toUserId) {
        return friendDAO.cancelFriendRequest(fromUserId, toUserId);
    }

    // ลบเพื่อนออกจากรายชื่อ
    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        friendDAO.removeFriend(userId, friendId);
    }

    // 🆕 ✅ เพิ่มฟังก์ชันเช็คสถานะความสัมพันธ์
    public FriendshipStatus getFriendshipStatus(UUID userId, UUID targetUserId) {
        if (friendDAO.areFriends(userId, targetUserId)) {
            return FriendshipStatus.ACCEPTED; // ✅ เป็นเพื่อนกันแล้ว
        }
        if (friendDAO.hasPendingRequest(userId, targetUserId)) {
            return FriendshipStatus.PENDING; // 📩 มีคำขอเป็นเพื่อนอยู่
        }
        return FriendshipStatus.NONE; // ❌ ยังไม่ได้เป็นเพื่อนกัน
    }

}
