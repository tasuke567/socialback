package com.example.socialback.model.dao;

import com.example.socialback.model.entity.UserInterestEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class UserInterestDaoImpl implements UserInterestDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserInterestDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SQL_FIND_BY_USER_ID = "SELECT id, user_id, interest FROM user_interests WHERE user_id = ?";

    private static final String SQL_INSERT = "INSERT INTO user_interests (id, user_id, interest) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE user_interests SET interest = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM user_interests WHERE id = ?";

    private static final String SQL_EXISTS_BY_USER_ID_AND_INTEREST = "SELECT COUNT(*) > 0 FROM user_interests WHERE user_id = ? AND interest = ?";

    @Override
    public List<UserInterestEntity> findByUserId(UUID userId) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_ID, (rs, rowNum) -> UserInterestEntity.builder()
                .id(UUID.fromString(rs.getString("id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .interest(rs.getString("interest"))
                .build(),
                userId);
    }

    @Override
    public int insert(UserInterestEntity entity) {
        return jdbcTemplate.update(SQL_INSERT, entity.getId(), entity.getUserId(), entity.getInterest());
    }

    @Override
    public int update(UserInterestEntity entity) {
        return jdbcTemplate.update(SQL_UPDATE, entity.getInterest(), entity.getId());
    }

    @Override
    public int delete(UUID interestId) {
        return jdbcTemplate.update(SQL_DELETE, interestId);
    }

    // Expose JdbcTemplate if needed elsewhere.
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public boolean existsByUserIdAndInterest(UUID userId, String interest) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                SQL_EXISTS_BY_USER_ID_AND_INTEREST,
                Boolean.class,
                userId,
                interest));
    }
}
