package com.example.socialback.model.dao;

import com.example.socialback.model.dto.FriendRequestDTO;
import com.example.socialback.model.dto.UserDTO;
import com.example.socialback.model.entity.FriendshipStatus;
import com.example.socialback.model.entity.UserEntity;
import com.example.socialback.service.FriendRequestResult;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

import com.example.socialback.model.entity.FriendRequestEntity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FriendDAO {
    private static final Logger logger = LoggerFactory.getLogger(FriendDAO.class);
    private final JdbcTemplate jdbcTemplate;

    public List<UserEntity> getFriends(UUID userId) {
        String sql = """
                    SELECT DISTINCT ON (u.id) u.* FROM users u
                    JOIN friendships f ON
                        (u.id = f.friend_id AND f.user_id = ?)
                        OR (u.id = f.user_id AND f.friend_id = ?)
                    WHERE f.status = 'ACCEPTED'
                """;

        try {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserEntity.class), userId, userId);
        } catch (Exception e) {
            logger.error("❌ Error fetching friends for user {}: {}", userId, e.getMessage());
            return Collections.emptyList(); // 🔄 คืนลิสต์ว่างแทน null
        }
    }

    public List<UserEntity> getSuggestions(UUID userId) {

        // ใช้ WITH + DISTINCT ON (u.id) ใน subquery (base)
        // แล้วชั้นนอกค่อย ORDER BY RANDOM() + LIMIT 10
        String sql = """
                WITH base AS (
                    SELECT DISTINCT ON (u.id)
                        u.id,
                        u.username,
                        u.password,
                        u.email,
                        u.first_name,
                        u.last_name,
                        u.profile_picture,
                        u.roles,
                        u.created_at,
                        u.updated_at,
                        u.account_non_expired,
                        u.account_non_locked,
                        u.credentials_non_expired,
                        u.enabled
                
                    FROM users u
                
                    LEFT JOIN friendships f
                        ON (u.id = f.friend_id OR u.id = f.user_id)
                       AND f.status = 'ACCEPTED'
                
                    LEFT JOIN user_interests ui        ON u.id = ui.user_id
                    LEFT JOIN user_interests my_i      ON my_i.user_id       = ?
                
                    LEFT JOIN post_likes pl           ON u.id = pl.user_id
                    LEFT JOIN post_likes my_likes     ON my_likes.user_id   = ? AND my_likes.post_id = pl.post_id
                
                    LEFT JOIN comments c              ON u.id = c.user_id
                    LEFT JOIN comments my_comments    ON my_comments.user_id= ? AND my_comments.post_id = c.post_id
                
                    LEFT JOIN post_shares ps          ON u.id = ps.user_id
                    LEFT JOIN post_shares my_shares   ON my_shares.user_id  = ? AND my_shares.post_id = ps.post_id
                
                    WHERE u.id <> ?       -- ไม่แนะนำตัวเอง
                      AND u.id NOT IN (
                          SELECT friend_id FROM friendships WHERE user_id = ?
                          UNION
                          SELECT user_id   FROM friendships WHERE friend_id = ?
                      )
                      AND (
                          my_i.interest IS NOT NULL
                          OR my_likes.post_id   IS NOT NULL
                          OR my_comments.post_id IS NOT NULL
                          OR my_shares.post_id   IS NOT NULL
                      )
                
                    -- สำคัญ: ต้อง ORDER BY (u.id) ตรงกับ DISTINCT ON (u.id)
                    ORDER BY u.id
                )
                SELECT *
                FROM base
                ORDER BY RANDOM()
                LIMIT 10
                """;

        // เรียง parameter ให้ตรงกับจำนวน ? ทั้งหมด
        Object[] params = new Object[]{
                userId, // my_i.user_id = ?
                userId, // my_likes.user_id = ?
                userId, // my_comments.user_id = ?
                userId, // my_shares.user_id = ?
                userId, // WHERE u.id <> ?
                userId, // SELECT friend_id FROM friendships WHERE user_id = ?
                userId // SELECT user_id FROM friendships WHERE friend_id = ?
        };

        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserEntity(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("profile_picture"),
                rs.getString("roles"),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getBoolean("account_non_expired"),
                rs.getBoolean("account_non_locked"),
                rs.getBoolean("credentials_non_expired"),
                rs.getBoolean("enabled")), params);
    }

    // ดึงคำขอเป็นเพื่อน
    public List<FriendRequestDTO> getRequests(UUID userId) {
        String sql = """
                SELECT * FROM friend_requests
                JOIN users ON friend_requests.requester_id = users.id
                WHERE receiver_id = ? AND status = 'PENDING'
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new FriendRequestDTO(
                rs.getObject("id", UUID.class),
                rs.getObject("requester_id", UUID.class),
                rs.getObject("receiver_id", UUID.class),
                FriendshipStatus.valueOf(rs.getString("status")),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("profile_picture")), userId);
    }

    // ตรวจสอบว่าเป็นเพื่อนกันอยู่แล้วหรือไม่
    public boolean areAlreadyFriends(UUID user1, UUID user2) {
        String sql = """
                SELECT COUNT(*) FROM friendships
                WHERE (user_id = ? AND friend_id = ?)
                   OR (user_id = ? AND friend_id = ?)
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, user1, user2, user2, user1);
        return count != null && count > 0;
    }

    // ตรวจสอบว่ามีคำขอเป็นเพื่อนที่ค้างอยู่หรือไม่
    public boolean isRequestPending(UUID requesterId, UUID receiverId) {
        String sql = """
                SELECT COUNT(*) FROM friend_requests
                WHERE ((requester_id = ? AND receiver_id = ?)
                   OR (requester_id = ? AND receiver_id = ?))
                   AND status = 'PENDING'
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, requesterId, receiverId, receiverId, requesterId);
        return count != null && count > 0;
    }

    // ส่งคำขอเป็นเพื่อน


    public FriendRequestResult sendFriendRequest(UUID requesterId, UUID receiverId) {
        if (requesterId.equals(receiverId)) {
            return FriendRequestResult.INVALID_REQUEST;
        }

        if (areAlreadyFriends(requesterId, receiverId)) {
            return FriendRequestResult.ALREADY_FRIENDS;
        }

        if (isRequestPending(requesterId, receiverId)) {
            return FriendRequestResult.ALREADY_PENDING;
        }

        // Insert new friend request
        String sql = "INSERT INTO friend_requests (id, requester_id, receiver_id, status, created_at) \n" +
                "VALUES (?, ?, ?, ?::friendship_status, ?);\n";
        jdbcTemplate.update(sql,
                UUID.randomUUID(),
                requesterId,
                receiverId,
                FriendshipStatus.PENDING.name(),  // Ensures correct ENUM value
                Timestamp.valueOf(LocalDateTime.now())
        );


        return FriendRequestResult.SENT;
    }


    // ดึง `receiver_id` ของคำขอเพื่อนที่ยังค้างอยู่
    public Optional<UUID> getRequestReceiver(UUID requestId) {
        String sql = "SELECT receiver_id FROM friend_requests WHERE id = ? AND status = 'PENDING'";
        try {
            UUID toUserId = jdbcTemplate.queryForObject(sql, UUID.class, requestId);
            return Optional.ofNullable(toUserId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // ยอมรับคำขอเป็นเพื่อน (เฉพาะ `receiver_id` เท่านั้น)
    public boolean acceptFriendRequest(UUID requestId, UUID currentUserId) {
        Optional<UUID> receiverIdOpt = getRequestReceiver(requestId);

        // ถ้า requestId ไม่มี หรือ currentUserId ไม่ตรงกับ receiver_id → ปฏิเสธ
        if (receiverIdOpt.isEmpty() || !receiverIdOpt.get().equals(currentUserId)) {
            return false;
        }

        // ดึง requester_id ของคำขอ
        String getRequesterSql = "SELECT requester_id FROM friend_requests WHERE id = ?";
        UUID fromUserId = jdbcTemplate.queryForObject(getRequesterSql, UUID.class, requestId);

        if (fromUserId != null) {
            // อัปเดตคำขอเป็น ACCEPTED
            String updateRequestSql = "UPDATE friend_requests SET status = ?::friendship_status WHERE id = ?";
            jdbcTemplate.update(updateRequestSql, FriendshipStatus.ACCEPTED.name(), requestId);

            // เพิ่มเป็นเพื่อนในตาราง friendships (เพิ่มทั้งสองด้าน)
            LocalDateTime now = LocalDateTime.now();
            String insertFriendshipSql = "INSERT INTO friendships (id, user_id, friend_id, status, created_at, updated_at) VALUES (?, ?, ?, ?::friendship_status, ?, ?)";

            UUID friendshipId1 = UUID.randomUUID();
            UUID friendshipId2 = UUID.randomUUID();

            // Insert friendship for both sides
            jdbcTemplate.update(insertFriendshipSql, friendshipId1, currentUserId, fromUserId, FriendshipStatus.ACCEPTED.name(), now, now);
            jdbcTemplate.update(insertFriendshipSql, friendshipId2, fromUserId, currentUserId, FriendshipStatus.ACCEPTED.name(), now, now);

            return true;
        }

        return false;
    }


    // ปฏิเสธคำขอเป็นเพื่อน
    public boolean declineFriendRequest(UUID requestId, UUID currentUserId) {
        Optional<UUID> receiverIdOpt = getRequestReceiver(requestId);
        if (receiverIdOpt.isEmpty() || !receiverIdOpt.get().equals(currentUserId)) {
            return false;
        }
        String updateSql = "UPDATE friend_requests SET status = 'DECLINED' WHERE id = ?";
        int updated = jdbcTemplate.update(updateSql, requestId);
        return updated > 0;
    }

    // ยกเลิกคำขอเป็นเพื่อน (สำหรับผู้ส่งคำขอ)
    public boolean cancelFriendRequest(UUID fromUserId, UUID toUserId) {
        String sql = """
                DELETE FROM friend_requests
                WHERE requester_id = ?
                AND receiver_id = ?
                AND status = 'PENDING'
                """;
        int deleted = jdbcTemplate.update(sql, fromUserId, toUserId);
        return deleted > 0;
    }

    // ลบเพื่อน
    public void removeFriend(UUID userId, UUID friendId) {
        String sqld = "Delete From Friend_Requests Where (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)";
        jdbcTemplate.update(sqld, userId, friendId, friendId, userId);
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    // ดึงรายชื่อเพื่อนของผู้ใช้
    public List<UserDTO> getUserFriends(UUID userId) {
        String sql = """
                SELECT DISTINCT u.id, u.username, u.first_name, u.last_name, u.email, u.profile_picture,
                                u.created_at, u.updated_at
                FROM users u
                JOIN friendships f ON (u.id = f.user_id OR u.id = f.friend_id)
                WHERE f.status = 'ACCEPTED'
                AND (f.user_id = ? OR f.friend_id = ?)
                AND u.id != ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserDTO(
                        rs.getObject("id", UUID.class),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("profile_picture"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null), userId,
                userId, userId);
    }

    // ดึงคำขอเป็นเพื่อนที่ค้างอยู่
    // ดึงคำขอเป็นเพื่อนที่ค้างอยู่ พร้อมข้อมูลผู้ส่งคำขอ (from_user)

    /**
     * ดึงข้อมูล friend request ที่ยังค้างอยู่ (pending) สำหรับ user ที่รับคำขอ
     *
     * @param userId รหัสของ user ที่จะเช็คคำขอเพื่อน
     * @return รายการ FriendRequestDTO ที่มีสถานะ PENDING
     */
    public List<FriendRequestDTO> getPendingRequests(UUID userId) {
        // SQL คิวรีเพื่อดึงข้อมูล friend request ค้างอยู่ โดย join กับตาราง users
        // เพื่อเอา info ของผู้ส่ง
        String sql = """
                SELECT fr.id, fr.requester_id, fr.receiver_id, fr.status, fr.created_at,
                       u.username, u.first_name, u.last_name, u.profile_picture
                FROM friend_requests fr
                JOIN users u ON fr.receiver_id = u.id
                WHERE fr.requester_id = ? AND fr.status = 'PENDING'
                """;

        // ใช้ jdbcTemplate query และ map ข้อมูลจาก ResultSet ไปเป็น FriendRequestDTO
        return jdbcTemplate.query(sql, (rs, rowNum) -> new FriendRequestDTO(
                rs.getObject("id", UUID.class),
                rs.getObject("requester_id", UUID.class),
                rs.getObject("receiver_id", UUID.class),
                FriendshipStatus.valueOf(rs.getString("status")),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("profile_picture")), userId);
    }

    // แนะนำเพื่อน (Friends of Friends - FoF)
    public List<UserDTO> getFriendSuggestions(UUID userId) {
        String sql = """
                SELECT DISTINCT u.id, u.username, u.email, u.profile_picture
                FROM users u
                JOIN friendships f1 ON (u.id = f1.user_id OR u.id = f1.friend_id)
                JOIN friendships f2 ON (f1.user_id = f2.friend_id OR f1.friend_id = f2.user_id)
                WHERE f2.user_id = ?
                AND u.id != ?
                AND u.id NOT IN (
                    SELECT friend_id FROM friendships WHERE user_id = ?
                    UNION
                    SELECT user_id FROM friendships WHERE friend_id = ?
                )
                LIMIT 5
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserDTO(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("profile_picture")), userId, userId, userId, userId);
    }

    // ✅ เช็คว่าเป็นเพื่อนกันหรือยัง
    public boolean areFriends(UUID userId, UUID targetUserId) {
        String sql = """
                SELECT COUNT(*) FROM friendships
                WHERE ((user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?))
                AND status = 'ACCEPTED'
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, targetUserId, targetUserId, userId);
        return count != null && count > 0;
    }

    // ✅ เช็คว่ามีคำขอเป็นเพื่อนที่รอการอนุมัติหรือไม่
    public boolean hasPendingRequest(UUID userId, UUID targetUserId) {
        String sql = """
                SELECT COUNT(*) FROM friend_requests
                WHERE (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)
                AND status = 'PENDING'
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, targetUserId, targetUserId, userId);
        return count != null && count > 0;
    }

    // ดึงคำขอเป็นเพื่อนที่ส่งออกไป (จากผู้ส่ง) พร้อมข้อมูลของผู้รับคำขอ
    public List<FriendRequestDTO> getSentFriendRequests(UUID userId) {
        String sql = """
                SELECT fr.id, fr.requester_id, fr.receiver_id, fr.status, fr.created_at,
                       u.username, u.first_name, u.last_name, u.profile_picture
                FROM friend_requests fr
                JOIN users u ON fr.requester_id = u.id
                WHERE fr.receiver_id = ? AND fr.status = 'PENDING'
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new FriendRequestDTO(
                rs.getObject("id", UUID.class),
                rs.getObject("requester_id", UUID.class),
                rs.getObject("receiver_id", UUID.class),
                FriendshipStatus.valueOf(rs.getString("status")),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("profile_picture")), userId);
    }

}
