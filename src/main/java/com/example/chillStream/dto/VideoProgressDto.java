package com.example.chillStream.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VideoProgressDto {
    private Long memberId;
    private Long videoId;
    private int lastPlayedPosition;
}
