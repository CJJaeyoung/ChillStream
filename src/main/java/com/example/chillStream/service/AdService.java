package com.example.chillStream.service;

import com.example.chillStream.dto.AdDto;
import com.example.chillStream.dto.AdSearchDto;
import com.example.chillStream.entity.Ad;
import com.example.chillStream.repository.AdRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class AdService {

    private final ContentsService contentsService;
    private final AdRepository adRepository;

    //기존 광고 가져오기(수정)
    public AdDto getAdDtl(Long adId){
        Ad ad = adRepository.findById(adId).orElseThrow(EntityNotFoundException::new);
        return AdDto.of(ad);
    }

    //광고 수정
    public void updateAd(AdDto adDto, MultipartFile adFile) throws Exception{
        contentsService.updateAd(adDto, adFile);
    }

    //광고 삭제
    public void deleteAd(Long adId){
        Ad ad = adRepository.findById(adId).orElseThrow(EntityNotFoundException::new);
        adRepository.delete(ad);
    }

    //광고 관리 페이지
    @Transactional(readOnly = true)
    public Page<Ad> getAdminItemPage(AdSearchDto adSearchDto, Pageable pageable) {
        return adRepository.getAdminAdPage(adSearchDto, pageable);
    }

    //광고 추가
    public void saveAd(AdDto adDto, MultipartFile adFile) throws Exception {
        Ad ad = new Ad();
        ad.setTitle(adDto.getTitle());
        ad.setAdClickUrl(adDto.getAdClickUrl());
        contentsService.saveAdFile(ad,adFile);
    }
}
