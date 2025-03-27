package com.example.socialback.model.dao;

import com.example.socialback.model.entity.UserInterestEntity;
import java.util.List;
import java.util.UUID;

public interface UserInterestDAO {
    List<UserInterestEntity> findByUserId(UUID userId);
    int insert(UserInterestEntity entity);
    int update(UserInterestEntity entity);
    int delete(UUID interestId);
    boolean existsByUserIdAndInterest(UUID userId, String interest);
}
