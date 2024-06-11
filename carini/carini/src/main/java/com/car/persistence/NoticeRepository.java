package com.car.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.car.dto.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>{

}
