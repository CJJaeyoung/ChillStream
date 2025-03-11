package com.example.chillStream.dto;

import com.example.chillStream.entity.Subtitle;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class SubtitleDto {
    private Long id;
    private String subtitleUrl; // 자막 경로
    private String subtitleName; // 자막 저장 이름
    private String oriSubtitleName; // 자막 본래 이름

    public static ModelMapper modelMapper = new ModelMapper();

    public static SubtitleDto of(Subtitle subtitle) {
        return modelMapper.map(subtitle, SubtitleDto.class);
    }
}
