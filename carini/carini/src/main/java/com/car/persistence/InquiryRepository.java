package com.car.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long>{

	List<Inquiry> findByMemberId(String memberId);
	
	Page<Inquiry> findByReTitleContaining(String reTitle, Pageable pageable);
	Page<Inquiry> findByReContentContaining(String reContent, Pageable pageable);
	Page<Inquiry> findByMemberIdContaining(String memberId, Pageable pageable);
	Page<Inquiry> findByReTitleRqContaining(String reTitleRq, Pageable pageable);
	Page<Inquiry> findByReContentRqContaining(String reContentRq, Pageable pageable);
	
}
