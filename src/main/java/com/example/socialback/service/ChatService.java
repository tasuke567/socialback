package com.example.socialback.service;

import com.example.socialback.model.dao.ConversationDAO;
import com.example.socialback.model.entity.ConversationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ConversationDAO conversationDAO;

    /**
     * ค้นหา Conversation ที่มีอยู่แล้ว หรือสร้างใหม่ถ้ายังไม่มี
     *
     * @param userId    ID ของผู้ใช้ที่กำลังล็อกอิน
     * @param friendId  ID ของเพื่อนที่ต้องการคุยด้วย
     * @return conversationId ของแชทที่พบหรือที่สร้างขึ้นมาใหม่
     */
    @Transactional
    public String findOrCreateConversation(String userId, String friendId) {
        UUID userUUID = UUID.fromString(userId);
        UUID friendUUID = UUID.fromString(friendId);

        // ✅ ค้นหาแชทที่มีอยู่แล้ว
        Optional<ConversationEntity> existingConversation = conversationDAO.findConversation(userUUID, friendUUID);

        // ✅ ถ้ามีอยู่แล้วให้คืนค่า conversationId เดิม
        if (existingConversation.isPresent()) {
            return existingConversation.get().getId().toString();
        }

        // ✅ ถ้าไม่มี ให้สร้างแชทใหม่
        ConversationEntity newConversation = conversationDAO.createConversation(userUUID, friendUUID);
        return newConversation.getId().toString();
    }
}
