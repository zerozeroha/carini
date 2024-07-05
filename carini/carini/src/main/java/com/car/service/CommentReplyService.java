package com.car.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.car.dto.CommentReply;

@Service
public interface CommentReplyService {

	void save(CommentReply commentReply);

	List<CommentReply> findCommentList(Long commentId, Long boardId);

	void delete(CommentReply commentReply);

}
