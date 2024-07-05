package com.car.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.car.dto.CommentReply;
import com.car.persistence.CommentReplyRepository;
import com.car.persistence.CommentRepository;
import com.car.service.CommentReplyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentReplyServiceImpl implements CommentReplyService{

	private final CommentReplyRepository commentReplyRepository;
	
	@Override
	public void save(CommentReply commentReply) {
		
		
		commentReplyRepository.save(commentReply);

	}

	@Override
	public List<CommentReply> findCommentList(Long commentId, Long boardId) {
		
		List<CommentReply> CommentReply = commentReplyRepository.findByCommentIdAndBoardId(commentId,boardId);
		
		if(CommentReply.isEmpty()) {
			return null;
		}
		
		return CommentReply;
	}

	@Override
	public void delete(CommentReply commentReply) {
		
		commentReplyRepository.deleteById(commentReply.getCommentReplyId());
		
	}

}
