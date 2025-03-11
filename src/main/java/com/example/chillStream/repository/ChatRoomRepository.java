package com.example.chillStream.repository;

import com.example.chillStream.entity.ChatRoom;
import com.example.chillStream.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// ChatMessage와 ChatRoom을 따로 만든 이유 : 각각의 엔티티가 서로 다른 목적과 역활을 수행하기 때문에 두개의 클래스로 나누는게 적합하다.(DB 설계의 핵심 원칙인 SRP 원칙)
// ChatMessage 엔티티 : 개별 메시지의 정보를 저장하는 역활을 한다.
// ChatRoom 엔티티 : 채팅방 자체의 정보를 저장한다.
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByMember(Member member);

    ChatRoom findByRoomId(Long roomId);

    ChatRoom findByMember_Id(Long memberId);
}
