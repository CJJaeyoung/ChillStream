package com.example.chillStream.dto;

import com.example.chillStream.entity.Preview;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class PreviewDto {
    private Long id;
    private String previewUrl; // 비디오 경로
    private String previewName; // 비디오 저장 이름
    private String oriPreviewName; // 비디오 본래 이름

    public static ModelMapper modelMapper = new ModelMapper();

    public static PreviewDto of(Preview preview) {
        return modelMapper.map(preview, PreviewDto.class);
    }
}