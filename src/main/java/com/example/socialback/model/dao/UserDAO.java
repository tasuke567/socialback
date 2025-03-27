package com.example.socialback.model.dao;

import com.example.socialback.model.dto.UserDTO;
import com.example.socialback.model.entity.UserEntity;
import com.example.socialback.model.dto.UserProfileDTO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Array;

@Repository
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(UserDTO user) {
        String sql = "INSERT INTO users (id, first_name, last_name, profile_picture, username, email, created_at, updated_at, account_non_expired, account_non_locked, credentials_non_expired, enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getId(), user.getFirstName(), user.getLastName(), user.getProfilePicture(),
                user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt(),
                user.isAccountNonExpired(), user.isAccountNonLocked(), user.isCredentialsNonExpired(),
                user.isEnabled());
    }

    public UserDTO getUserById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
    }

    public List<UserDTO> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public UserEntity findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserEntity(
                    rs.getObject("id", UUID.class),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("profile_picture"),
                    rs.getString("roles"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getBoolean("account_non_expired"),
                    rs.getBoolean("account_non_locked"),
                    rs.getBoolean("credentials_non_expired"),
                    rs.getBoolean("enabled")), username);
        } catch (EmptyResultDataAccessException e) {
            return null; // ✅ ป้องกัน error และคืนค่า null หากไม่พบ user
        }
    }

    public UserEntity findById(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new UserEntity(
                    rs.getObject("id", UUID.class),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("profile_picture"),
                    rs.getString("roles"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getBoolean("account_non_expired"),
                    rs.getBoolean("account_non_locked"),
                    rs.getBoolean("credentials_non_expired"),
                    rs.getBoolean("enabled")), userId);
        } catch (EmptyResultDataAccessException e) {
            return null; // ✅ ป้องกัน error และคืนค่า null หากไม่พบ user
        }
    }

    public void updateUser(UserEntity user) {
        String sql = "UPDATE users SET ";
        List<Object> params = new ArrayList<>();

        if (user.getEmail() != null) {
            sql += "email = ?, ";
            params.add(user.getEmail());
        }
        if (user.getProfilePicture() != null) {
            sql += "profile_picture = ?, ";
            params.add(user.getProfilePicture());
        }
        if (user.getFirstName() != null) {
            sql += "first_name = ?, ";
            params.add(user.getFirstName());
        }
        if (user.getLastName() != null) {
            sql += "last_name = ?, ";
            params.add(user.getLastName());
        }
        if (user.getPassword() != null) {
            sql += "password = ?, ";
            params.add(user.getPassword());
        }

        // ลบ comma (,) ตัวสุดท้ายออก และเพิ่ม WHERE id
        sql = sql.substring(0, sql.length() - 2) + " WHERE id = ?";
        params.add(user.getId());

        jdbcTemplate.update(sql, params.toArray());
    }

    public List<UserEntity> searchUsers(String query) {
        String sql = "SELECT DISTINCT ON (id) * FROM users WHERE LOWER(username) LIKE LOWER(?) OR LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) ORDER BY id, created_at DESC";
        String wildcardQuery = "%" + query + "%";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            
            String rawId = rs.getString("id");
            
            UUID id = UUID.fromString(rawId);
           

            return new UserEntity(
                    id,
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("profile_picture"),
                    rs.getString("roles"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getBoolean("account_non_expired"),
                    rs.getBoolean("account_non_locked"),
                    rs.getBoolean("credentials_non_expired"),
                    rs.getBoolean("enabled"));
        }, wildcardQuery, wildcardQuery, wildcardQuery);
    }

    public void deleteUser(UUID userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private UserDTO mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new UserDTO(
                UUID.fromString(rs.getString("id")), // 🔥 แก้ตรงนี้!
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("profile_picture"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getBoolean("account_non_expired"),
                rs.getBoolean("account_non_locked"),
                rs.getBoolean("credentials_non_expired"),
                rs.getBoolean("enabled"));
    }

    public UserProfileDTO findUserWithInterestsByUsername(String username) {
        String sql = """
                SELECT
                    u.*,
                    COALESCE(array_remove(ARRAY_AGG(ui.interest), NULL), '{}') AS interests
                FROM users u
                LEFT JOIN user_interests ui ON u.id = ui.user_id
                WHERE u.username = ?
                GROUP BY u.id
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                UserProfileDTO dto = new UserProfileDTO(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("profile_picture"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("updated_at").toLocalDateTime()

                );

                Array interestsArray = rs.getArray("interests");
                if (interestsArray != null) {
                    dto.setInterests(List.of((String[]) interestsArray.getArray()));
                    dto.setPostsCount(0);
                    dto.setFollowersCount(0);

                    dto.setFollowingCount(0);
                }
                return dto;
            }, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
