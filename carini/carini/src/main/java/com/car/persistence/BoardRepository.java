package com.car.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.car.dto.Board;

public interface BoardRepository extends CrudRepository<Board, Long>{
	
	@Modifying // 이 쿼리 메서드가 데이터베이스를 수정하겠다.
	@Transactional // 이 메서드가 트랜잭션 내에서 실행되어야 함을 지정.
	@Query(value = "update Board b set b.board_cnt = b.board_cnt + 1 where b.board_seq = :board_seq", nativeQuery = false)
	// nativeQuery=true은 기본적인 sql문장 사용하겠다. false로 주면 별칭 무조건 줘야 됨.(기본쿼리가 아닌 JPQL쿼리이기 때문)
	int updateReadCount(@Param("board_seq") Long seq);
	
	@Modifying
	@Transactional
	@Query("update Board b set b.board_ref = b.board_seq, b.board_lev=:lev, b.board_seq=:_seq where b.seq=:seq")
	void updateLastSeq(@Param("lev") Long i, @Param("_seq") Long j, @Param("seq") Long seq);
	

	// select * from board where title like '%xxx%';
	Page<Board> findByTitleContaining(String boardTitle, Pageable pageable);
	Page<Board> findByWriterContaining(String boardWriter, Pageable pageable);
	Page<Board> findByContentContaining(String boardContent, Pageable pageable);
	
}
