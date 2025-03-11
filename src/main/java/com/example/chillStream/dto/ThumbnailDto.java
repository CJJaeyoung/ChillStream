package com.example.chillStream.dto;

import com.example.chillStream.entity.Thumbnail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class ThumbnailDto {
    private Long id;
    private String thumbnailUrl; // 썸네일 경로
    private String thumbnailName; // 썸네일 저장 이름
    private String oriThumbnailName; // 썸네일 본래 이름

    public static ModelMapper modelMapper = new ModelMapper();

    public static ThumbnailDto of(Thumbnail thumbnail){
        return modelMapper.map(thumbnail, ThumbnailDto.class);
    }
    
}
