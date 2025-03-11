package com.example.chillStream.dto;

import com.example.chillStream.entity.Video;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class VideoDto {
    private Long id;
    private String videoUrl; // 비디오 경로
    private String videoName; // 비디오 저장 이름
    private String oriVideoName; // 비디오 본래 이름

    public static ModelMapper modelMapper = new ModelMapper();

    public static VideoDto of(Video video){
        return modelMapper.map(video, VideoDto.class);
    }

}
