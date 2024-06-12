package com.car.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.car.dto.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>{
	
	@Modifying // 이 쿼리 메서드가 데이터베이스를 수정하겠다.
	@Transactional // 이 메서드가 트랜잭션 내에서 실행되어야 함을 지정.
	@Query(value = "update Notice n set n.noticeCnt = n.noticeCnt + 1 where n.noticeId = :noticeId", nativeQuery = false)
	int updateNoticeReadCount(@Param("noticeId") Long noticeId);
	
	// findBy[컬럼명]	: 엔티티의 특정 컬럼 값을 이용하여 조회하는 메서드
	// findBy[컬럼명]Containing : 지정한 문자열이 포함된 엔티티를 조회하는 메서드
	// select * from board where boardTitle like '%xxx%';
	Page<Notice> findByNoticeTitleContaining(String noticeTitle, Pageable pageable);
	Page<Notice> findByNoticeContentContaining(String noticeContent, Pageable pageable);
	
	List<Notice> findBynoticeId(Long noticeId);
	
}











