package com.example.chillStream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chillStream.entity.BannerImg;

@Repository
public interface BannerImgRepository extends JpaRepository<BannerImg, Long> {
    List<BannerImg> findByBannersIdOrderByIdAsc(Long bannersId);
    BannerImg findByBannersId(Long bannersId);
}
