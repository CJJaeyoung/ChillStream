package com.example.chillStream.dto;

import com.example.chillStream.entity.Video;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class VideoFormDto {
    private Long id;
    
    @NotEmpty(message = "제목은 필수 입력값입니다.")
    private String title;

    private Integer episodeNumber;

    private VideoDto videoDto;
    private SubtitleDto subtitleDto;
    private Long itemId;
    
    
    public VideoFormDto() {
    }
    public VideoFormDto(Long itemId) {
        this.itemId = itemId;
    }

    private static ModelMapper modelMapper = new ModelMapper();

    public Video createVideo() {
        return modelMapper.map(this, Video.class);
    }

    public static VideoFormDto of(Video video) {
        return modelMapper.map(video, VideoFormDto.class);
    }
}
