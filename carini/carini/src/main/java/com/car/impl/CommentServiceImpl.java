package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.car.dto.Board;
import com.car.dto.Comment;
import com.car.dto.Member;
import com.car.persistence.CommentReplyRepository;
import com.car.persistence.CommentRepository;
import com.car.service.CommentReplyService;
import com.car.service.CommentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

	private final CommentRepository commentRepository;
	private final CommentReplyRepository commentReplyRepository;
	@Override
	public void save(Comment comment) {
		
		commentRepository.save(comment);
		
	}

	@Override
	public Comment findComment(Long boardId, String userId) {
		
		
		Optional<Comment> comment = commentRepository.findAllByBoardIdAndUserId(boardId,userId);
		
		if(!comment.isPresent()) {
			return null;	
		}else {
			return comment.get();	
		}
	}

	@Override
	public List<Comment> findComment(Long boardId) {
		
		List<Comment> comment = commentRepository.findAllByBoardId(boardId);
		System.out.println(comment);
		if(comment.isEmpty()) {
			return null;	
		}else {
			return comment;	
		}
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId) {
		System.out.println(commentId);
		commentReplyRepository.deleteByCommentId(commentId);

		commentRepository.deleteById(commentId);
	}

	@Override
	@Transactional
	public void deleteMember(Member findmember) {
		
		commentRepository.deleteByUserId(findmember.getMemberId());
		
	}
}
