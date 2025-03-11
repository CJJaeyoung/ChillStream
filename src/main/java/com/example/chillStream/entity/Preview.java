package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Preview extends BaseEntity{
    @Id
    @Column(name = "preview_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String previewUrl; // 미리보기 경로
    private String previewName; // 미리보기 저장 이름
    private String oriPreviewName; // 미리보기 본래 이름

    public void updatePreview(String oriPreviewName, String previewName, String previewUrl){
        this.previewName = previewName;
        this.previewUrl = previewUrl;
        this.oriPreviewName = oriPreviewName;
    }
}
