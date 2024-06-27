package com.car.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	@Modifying
	@Query("UPDATE Inquiry i SET i.reTitleRq = :reTitleRq, i.reContentRq = :reContentRq WHERE i.reId = :reId")
	void answerInquiry(@Param("reTitleRq") String reTitleRq, @Param("reContentRq") String reContentRq, @Param("reId") Long reId);
	
	
}
