package com.example.chillStream.dto;

import com.example.chillStream.entity.Ad;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class AdDto {
    private Long id;
    @NotEmpty(message = "제목은 필수 입력값입니다")
    private String title;
    private String oriAdName;
    private String adName;
    private String adUrl;
    @NotEmpty(message = "광고 페이지 URL은 필수 입력값입니다")
    private String adClickUrl;

    public static ModelMapper modelMapper = new ModelMapper();

    public static AdDto of(Ad ad){
        return modelMapper.map(ad, AdDto.class);
    }

}
