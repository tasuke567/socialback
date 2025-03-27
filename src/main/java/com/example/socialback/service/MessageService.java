package com.example.socialback.service;

import com.example.socialback.model.dao.ConversationDAO;
import com.example.socialback.model.dao.MessageDAO;
import com.example.socialback.model.entity.ConversationEntity;
import com.example.socialback.model.entity.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageDAO messageDAO;
    private final ConversationDAO conversationDAO;

    public List<ConversationEntity> getUserConversations(UUID userId) {
        return conversationDAO.getUserConversations(userId);
    }

    public List<MessageEntity> getMessagesByConversation(UUID conversationId) {
        return messageDAO.getMessagesByConversation(conversationId);
    }

    @Transactional
    public MessageEntity sendMessage(
            UUID conversationId, 
            UUID senderId, 
            UUID receiverId, 
            String content, 
            String username 
    ) {
        ConversationEntity conversation;

        if (conversationId != null) {
            conversation = conversationDAO.findById(conversationId)
                    .orElseThrow(
                            () -> new IllegalArgumentException("Conversation not found with id: " + conversationId));
        } else {
            Optional<ConversationEntity> conversationOpt = conversationDAO.findConversation(senderId, receiverId);
            conversation = conversationOpt.orElseGet(() -> conversationDAO.createConversation(senderId, receiverId));
        }

        return messageDAO.sendMessage(senderId, receiverId, content, conversation.getId(), username);
    }

    @Transactional
    public boolean deleteMessage(UUID messageId, UUID userId) {
        return messageDAO.deleteMessage(messageId, userId);
    }
}
