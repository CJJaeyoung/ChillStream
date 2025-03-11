package com.example.chillStream.repository;

import com.example.chillStream.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Long>, AdRepositoryCustom{
}
