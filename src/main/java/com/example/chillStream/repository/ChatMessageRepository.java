package com.example.chillStream.repository;

import com.example.chillStream.entity.ChatMessage;
import com.example.chillStream.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByIdAsc(ChatRoom chatRoom);
} 