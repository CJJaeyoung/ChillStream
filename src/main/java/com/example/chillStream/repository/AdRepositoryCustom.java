package com.example.chillStream.repository;

import com.example.chillStream.dto.AdSearchDto;
import com.example.chillStream.entity.Ad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdRepositoryCustom {
    Page<Ad> getAdminAdPage(AdSearchDto adSearchDto, Pageable pageable);
}
