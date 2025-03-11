package com.example.chillStream.dto;

import org.modelmapper.ModelMapper;

import com.example.chillStream.entity.BannerImg;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class BannerImgDto {
    private Long id;
    private String imgName; // 배너 이미지 이름
    private String oriImgName; // 배너 이미지 원본 이름
    private String imgUrl;  // 배너 이미지 경로

    public static ModelMapper modelMapper = new ModelMapper();


    public static BannerImgDto of(BannerImg bannerImg) {
        return modelMapper.map(bannerImg, BannerImgDto.class);
    }

}
