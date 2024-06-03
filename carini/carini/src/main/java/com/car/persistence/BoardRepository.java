package com.car.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>{

	List<Board> findBymemberId(String memberId);
	
	
}
