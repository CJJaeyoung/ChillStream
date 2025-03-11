package com.example.chillStream.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 채팅방 알람 관련 dto
public class ChatNotification {
    private Long roomId;
    private String message;
    private boolean isRead;

    public ChatNotification(Long roomId) {
        this.roomId = roomId;
        this.isRead = false;
    }
} 