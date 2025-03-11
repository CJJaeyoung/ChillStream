package com.example.chillStream.dto;

import com.example.chillStream.constant.Country;
import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.entity.Item;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;


@Getter
@Setter
@ToString
public class ItemFormDto {
   private Long id;
   @NotEmpty(message = "제목을 입력해주세요")
   private String title;
   @NotEmpty(message = "공개일 입력해주세요")
   private String releaseDate;
   @NotNull(message = "러닝타임을 입력해주세요")
   private Integer runningTime;
   @NotEmpty(message = "줄거리를 입력해주세요")
   private String itemDetail;
   
   private Country country;
   private MainGenre mainGenre;
   private SubGenre subGenre;
   
   private ThumbnailDto thumbnailDto;
   private PreviewDto previewDto;
   private VideoDto videoDto;
   private SubtitleDto subtitleDto;
   
   private Double voteAverage;
   private Double popularity;
   
   private Long ItemCrawlId;
   
   public static ModelMapper modelMapper = new ModelMapper();
   
   public static ItemFormDto of(Item item) {
      return modelMapper.map(item, ItemFormDto.class);
   }
   
   public Item createItem() {
      return modelMapper.map(this, Item.class);
   }
   
   
   public static ItemFormDto fromEntity(Item item) {
      ItemFormDto dto = new ItemFormDto();
      dto.setId(item.getId());
      dto.setTitle(item.getTitle());
      dto.setReleaseDate(item.getReleaseDate());
      dto.setRunningTime(item.getRunningTime());
      dto.setItemDetail(item.getItemDetail());
      dto.setCountry(item.getCountry());
      dto.setMainGenre(item.getMainGenre());
      dto.setSubGenre(item.getSubGenre());
      
  
      
      // 연관 객체 DTO로 변환
      if (item.getThumbnail() != null) {
         dto.setThumbnailDto(ThumbnailDto.of(item.getThumbnail())); // of 메서드 활용
      }
      if (item.getPreview() != null) {
         dto.setPreviewDto(PreviewDto.of(item.getPreview())); // of 메서드 활용
      }
 
      
      
      return dto;
   }
}
