package com.car.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.car.dto.Comment;

@Service
public interface CommentService {

	void save(Comment comment);

	Comment findComment(Long boardId, String userId);

	List<Comment> findComment(Long boardId);

	void deleteComment(Long commentId);
	
}
