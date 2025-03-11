package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
public class Ad extends BaseEntity {
    @Id
    @Column(name = "ad_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String adClickUrl;

    private String oriAdName;
    private String adName;
    private String adUrl;

    public void updateAd(String title, String oriAdName, String adName, String adUrl, String adClickUrl){
        this.title = title;
        this.oriAdName = oriAdName;
        this.adName = adName;
        this.adUrl = adUrl;
        this.adClickUrl = adClickUrl;
    }
    
    public void updateTitleUrl(String title, String adClickUrl){ //title과 adClickUrl만 업데이트
        this.title = title;
        this.adClickUrl = adClickUrl;
    }
}
