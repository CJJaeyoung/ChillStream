package com.example.chillStream.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {

    private Long roomId; // 방번호
    private Long memberId; //
    private String sender; // 메시지 보낸 사람
    private String message; // 메시지
}