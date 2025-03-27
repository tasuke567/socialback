package com.example.socialback.service;

import com.example.socialback.model.dto.UserProfileDTO;
import com.example.socialback.model.entity.UserEntity;
import com.example.socialback.model.dao.UserDAO;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.socialback.model.dao.UserInterestDAO;
import com.example.socialback.model.entity.UserInterestEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserDAO userDAO; // ✅ ลบ UserRepository ออก
    private final UserInterestDAO userInterestDAO;

    public UserService(UserDAO userDAO, UserInterestDAO userInterestDAO) { // ✅ แก้ constructor ให้รับแค่ UserDAO
        this.userDAO = userDAO;
        this.userInterestDAO = userInterestDAO;
    }

    // ✅ ใช้ UserDAO แทน JPA
    public UserEntity findByUsername(String username) {
        UserEntity user = userDAO.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    // ✅ ดึงข้อมูลโปรไฟล์โดยใช้ DAO
    public UserProfileDTO getProfileByUsername(String username) {
        UserProfileDTO user = userDAO.findUserWithInterestsByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserProfileDTO dto = new UserProfileDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getProfilePicture(),
            user.getInterests(), 
            user.getFirstName(), 
            user.getLastName(), 
            user.getCreatedAt(), 
            user.getUpdatedAt()
        );
        dto.setProfilePicture(  user.getProfilePicture());
        return dto;
    }

    public UserProfileDTO getProfileById(UUID userId) {
        UserEntity user = userDAO.findById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserProfileDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getProfilePicture(),
            user.getInterests(), 
            user.getFirstName(), 
            user.getLastName(), 
            user.getCreatedAt(), 
            user.getUpdatedAt()
        );
    }

    // ✅ อัปเดตโปรไฟล์โดยใช้ DAO
    public UserProfileDTO updateProfile(String username, UserProfileDTO updatedProfile) {
        UserEntity user = userDAO.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user.setEmail(updatedProfile.getEmail());
        user.setProfilePicture(updatedProfile.getProfilePicture());
        user.setFirstName(updatedProfile.getFirstName());
        user.setLastName(updatedProfile.getLastName());
        user.setCreatedAt(updatedProfile.getCreatedAt());
        user.setUpdatedAt(updatedProfile.getUpdatedAt());
        userDAO.updateUser(user);
        return new UserProfileDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getProfilePicture(),
            user.getInterests(), 
            user.getFirstName(), 
            user.getLastName(), 
            user.getCreatedAt(), 
            user.getUpdatedAt()
        );
    }

    // ✅ ค้นหาผู้ใช้โดยใช้ DAO
    public List<UserProfileDTO> searchUsers(String query) {
        return userDAO.searchUsers(query).stream()
                .map(user -> new UserProfileDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),    
                        user.getProfilePicture(),
                        user.getInterests(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                        ))
                .collect(Collectors.toList());
    }

    public List<String> getUserInterests(UUID userId) {
        return userInterestDAO.findByUserId(userId).stream()
                .map(UserInterestEntity::getInterest)
                .collect(Collectors.toList());
    }

    public UserProfileDTO updateProfile(String username, String firstName, String lastName, String profilePictureUrl) {
        UserEntity user = userDAO.findByUsername(username);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (profilePictureUrl != null) {
            user.setProfilePicture(profilePictureUrl);
        }

        userDAO.updateUser(user);
            return new UserProfileDTO(
            user.getId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getProfilePicture(),
            user.getInterests(), 
            user.getFirstName(), 
            user.getLastName(), 
            user.getCreatedAt(), 
            user.getUpdatedAt()
        );
    }

}
