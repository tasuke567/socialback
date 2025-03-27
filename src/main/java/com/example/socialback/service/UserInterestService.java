package com.example.socialback.service;

import com.example.socialback.model.dao.UserInterestDAO;
import com.example.socialback.model.dao.UserInterestDaoImpl;
import com.example.socialback.model.entity.UserInterestEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserInterestService {

    private final UserInterestDAO userInterestDAO;

    public UserInterestService(UserInterestDAO userInterestDAO) {
        this.userInterestDAO = userInterestDAO;
    }

    /**
     * ตรวจสอบว่าผู้ใช้มี record ในตาราง user_interests หรือไม่
     *
     * @param userId UUID ของผู้ใช้
     * @return true ถ้ามี interest อย่างน้อย 1 รายการ, false ถ้าไม่มี
     */
    public boolean hasUserInterests(UUID userId) {
        List<UserInterestEntity> interests = userInterestDAO.findByUserId(userId);
        return interests != null && !interests.isEmpty();
    }

    /**
     * เพิ่มความสนใจใหม่ให้กับผู้ใช้ (Insert)
     *
     * @param userId UUID ของผู้ใช้
     * @param interest ความสนใจที่ต้องการเพิ่ม
     * @return จำนวนแถวที่ถูกเพิ่ม (ควรเป็น 1 ถ้าสำเร็จ)
     */
    public int addUserInterest(UUID userId, String interest) {
        UUID interestId = UUID.randomUUID();
        return userInterestDAO.insert(new UserInterestEntity(interestId, userId, interest));
    }

    /**
     * แก้ไขความสนใจของผู้ใช้ (Update)
     *
     * @param interestId UUID ของ record ที่ต้องการแก้ไข
     * @param newInterest ค่าใหม่ของความสนใจ
     * @return จำนวนแถวที่ถูกแก้ไข (ควรเป็น 1 ถ้าสำเร็จ)
     */
    public int updateUserInterest(UUID interestId, String newInterest) {
        String sql = "UPDATE user_interests SET interest = ? WHERE id = ?";
        if (userInterestDAO instanceof UserInterestDaoImpl) {
            return ((UserInterestDaoImpl) userInterestDAO).getJdbcTemplate().update(sql, newInterest, interestId);
        }
        return 0;
    }

    /**
     * ลบความสนใจของผู้ใช้ (Delete)
     *
     * @param interestId UUID ของ record ที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ (ควรเป็น 1 ถ้าสำเร็จ)
     */
    public int deleteUserInterest(UUID interestId) {
        String sql = "DELETE FROM user_interests WHERE id = ?";
        if (userInterestDAO instanceof UserInterestDaoImpl) {
            return ((UserInterestDaoImpl) userInterestDAO).getJdbcTemplate().update(sql, interestId);
        }
        return 0;
    }

    public List<UserInterestEntity> getUserInterests(UUID userId) {
        return userInterestDAO.findByUserId(userId);
    }
    public boolean existsByUserIdAndInterest(UUID userId, String interest) {
        return userInterestDAO.existsByUserIdAndInterest(userId, interest);
    }
}
