package com.car.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

	
	Optional<Comment> findAllByBoardIdAndUserId(Long boardId, String userId);

	List<Comment> findAllByBoardId(Long boardId);

	void deleteById(Long commentId);

	void deleteByUserId(String memberId);
}
