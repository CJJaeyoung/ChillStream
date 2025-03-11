package com.example.chillStream.dto;

import com.example.chillStream.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class CommentCrawlDto {
    private Long id; //댓글 id
    private String memberEmail; //댓글 작성자 id(닉네임으로 변경 요망)
    private Integer tmdbId; //tmdb id
    private String content; //댓글 내용
    private int like; //좋아요 숫자
    private LocalDateTime regTime; //작성 날짜

    public static ModelMapper modelMapper = new ModelMapper();

    public static CommentDto of(Comment comment){
        return modelMapper.map(comment, CommentDto.class);
    }

}