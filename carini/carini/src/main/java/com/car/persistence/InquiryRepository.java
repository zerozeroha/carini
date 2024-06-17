package com.car.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long>{

	List<Inquiry> findByMemberId(String memberId);

}
