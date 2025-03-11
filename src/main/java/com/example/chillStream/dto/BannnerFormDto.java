package com.example.chillStream.dto;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

import com.example.chillStream.entity.Banners;
import com.example.chillStream.entity.BannerImg;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BannnerFormDto {
    
    private Long id;
    @NotEmpty(message = "배너 제목은 필수 입력 값입니다.")
    private String title;
    @NotEmpty(message = "설명은 필수 입력 값입니다.")
    private String description;
    @NotNull(message = "아이템 ID는 필수 입력 값입니다.")
    private Long itemId;
    
    private BannerImgDto bannerImgDto;
    
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
    
    public static BannnerFormDto of(Banners banners) {
        BannnerFormDto bannnerFormDto = new BannnerFormDto();
        bannnerFormDto.setId(banners.getId());
        bannnerFormDto.setTitle(banners.getTitle());
        bannnerFormDto.setDescription(banners.getDescription());
        bannnerFormDto.setItemId(banners.getItemId());
        bannnerFormDto.setRegTime(banners.getRegTime());
        bannnerFormDto.setUpdateTime(banners.getUpdateTime());
        
        // 배너 이미지 정보 설정
        BannerImg bannerImg = banners.getBannerImg();
        if (bannerImg != null) {
            bannnerFormDto.setBannerImgDto(BannerImgDto.of(bannerImg));
        }
        
        return bannnerFormDto;
    }
    
    public Banners createBanner() {
        Banners banners = new Banners();
        banners.setTitle(this.title);
        banners.setDescription(this.description);
        banners.setItemId(this.itemId);
        return banners;
    }
}
