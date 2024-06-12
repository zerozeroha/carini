package com.car.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import com.car.dto.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>{
	
	@Modifying // 이 쿼리 메서드가 데이터베이스를 수정하겠다.
	@Transactional // 이 메서드가 트랜잭션 내에서 실행되어야 함을 지정.
	@Query(value = "update Board b set b.boardCnt = b.boardCnt + 1 where b.boardId = :boardId and b.memberId != :memberId", nativeQuery = false)
	// nativeQuery=true은 기본적인 sql문장 사용하겠다. false로 주면 별칭 무조건 줘야 됨.(기본쿼리가 아닌 JPQL쿼리이기 때문)
	int updateReadCount(@Param("boardId") Long boardId, @Param("memberId") String memberId);
	
//	@Modifying
//	@Transactional
//	@Query("update Board b set b.boardId = :b.boardId, b.board_lev=:board_lev, "
//		 + "b.board_seq=:board_seq where b.boardId=:boardId")
//	void updateLastSeq(@Param("board_lev") Long board_lev, @Param("board_seq") Long board_seq, @Param("boardId") Long boardId);
	
	
	// findBy[컬럼명]	: 엔티티의 특정 컬럼 값을 이용하여 조회하는 메서드
	// findBy[컬럼명]Containing : 지정한 문자열이 포함된 엔티티를 조회하는 메서드
	// select * from board where boardTitle like '%xxx%';
	Page<Board> findByBoardTitleContaining(String boardTitle, Pageable pageable);
	Page<Board> findByBoardWriterContaining(String boardWriter, Pageable pageable);
	Page<Board> findByBoardContentContaining(String boardContent, Pageable pageable);
  
	List<Board> findBymemberId(String memberId);

}
