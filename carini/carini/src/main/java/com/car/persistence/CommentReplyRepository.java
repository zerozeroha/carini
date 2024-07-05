package com.car.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.CommentReply;

@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long>{

	List<CommentReply> findByCommentIdAndBoardId(Long commentId, Long boardId);

}
