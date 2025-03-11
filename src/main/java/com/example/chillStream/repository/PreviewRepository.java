package com.example.chillStream.repository;

import com.example.chillStream.entity.Preview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreviewRepository extends JpaRepository<Preview, Long> {
    Preview findByItemId(Long itemid);
}
