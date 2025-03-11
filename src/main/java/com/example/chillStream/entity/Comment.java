package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Comment extends BaseEntity{ //댓글 entity
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //해당하는 아이템(영상)

    private String content; //댓글 내용

    @Column(name = "like_count", columnDefinition = "INT") //명시적으로 INT로 정하지 않으면 int not null에러 발생
    private int like; //좋아요 수
}
