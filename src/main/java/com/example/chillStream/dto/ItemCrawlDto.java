package com.example.chillStream.dto;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.entity.ItemCrawl;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ItemCrawlDto {
    
    private Long id;
    private String title;
    private String itemDetail;
    private MainGenre mainGenre;
    private SubGenre subGenre;
    private Integer runningTime;
    private String thumbnailUrl;
    private ThumbnailDto thumbnailDto;
    private String releaseDate;
    private Integer tmdbId;
    private Double voteAverage;
    private Double popularity;
    private String trailerUrl;
    private String productionCountries;
    
    @Builder
    public ItemCrawlDto(Long id, String title, String itemDetail, MainGenre mainGenre,
                        SubGenre subGenre, Integer runningTime, String thumbnailUrl,
                        String releaseDate, Integer tmdbId, Double voteAverage, Double popularity, String trailerUrl, String productionCountries) {
        this.id = id;
        this.title = title;
        this.itemDetail = itemDetail;
        this.mainGenre = mainGenre;
        this.subGenre = subGenre;
        this.runningTime = runningTime;
        this.thumbnailUrl = thumbnailUrl;
        this.releaseDate = releaseDate;
        this.tmdbId = tmdbId;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.trailerUrl = trailerUrl;
        this.productionCountries = productionCountries;
    }
    
    public ItemCrawl toEntity() {
        ItemCrawl itemCrawl = new ItemCrawl();
        itemCrawl.setTitle(title);
        itemCrawl.setItemDetail(itemDetail);
        itemCrawl.setMainGenre(mainGenre);
        itemCrawl.setSubGenre(subGenre);
        itemCrawl.setRunningTime(runningTime);
        itemCrawl.setThumbnailUrl(thumbnailUrl);
        itemCrawl.setReleaseDate(releaseDate);
        itemCrawl.setTmdbId(tmdbId);
        itemCrawl.setVoteAverage(voteAverage);
        itemCrawl.setPopularity(popularity);
        itemCrawl.setTrailerUrl(trailerUrl);
        itemCrawl.setProductionCountries(productionCountries);
        return itemCrawl;
    }
    
    public ItemCrawlDto(ItemCrawl itemCrawl) {
        this.id = itemCrawl.getId();
        this.title = itemCrawl.getTitle();
        this.itemDetail = itemCrawl.getItemDetail();
        this.mainGenre = itemCrawl.getMainGenre();
        this.subGenre = itemCrawl.getSubGenre();
        this.runningTime = itemCrawl.getRunningTime();
        this.thumbnailUrl = itemCrawl.getThumbnailUrl();
        this.releaseDate = itemCrawl.getReleaseDate();
        this.tmdbId = itemCrawl.getTmdbId();
        this.voteAverage = itemCrawl.getVoteAverage();
        this.popularity = itemCrawl.getPopularity();
        this.trailerUrl = itemCrawl.getTrailerUrl();
        this.productionCountries = itemCrawl.getProductionCountries();
    }
    
    public static ItemCrawlDto fromEntity(ItemCrawl itemCrawl) {
        return ItemCrawlDto.builder()
              .id(itemCrawl.getId())
              .title(itemCrawl.getTitle())
              .itemDetail(itemCrawl.getItemDetail())
              .mainGenre(itemCrawl.getMainGenre())
              .subGenre(itemCrawl.getSubGenre())
              .runningTime(itemCrawl.getRunningTime())
              .thumbnailUrl(itemCrawl.getThumbnailUrl())
              .releaseDate(itemCrawl.getReleaseDate())
              .tmdbId(itemCrawl.getTmdbId())
              .voteAverage(itemCrawl.getVoteAverage())
              .popularity(itemCrawl.getPopularity())
              .trailerUrl(itemCrawl.getItemDetail())
              .productionCountries(itemCrawl.getProductionCountries())
              .build();
    }
} 