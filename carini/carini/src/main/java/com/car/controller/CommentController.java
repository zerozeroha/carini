package com.car.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.car.dto.Board;
import com.car.dto.Comment;
import com.car.dto.CommentReply;
import com.car.dto.Member;
import com.car.exception.ValidationException;
import com.car.service.CommentReplyService;
import com.car.service.CommentService;
import com.car.validation.CommentWriteValidation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

	private final CommentService commentService;
	
	private final CommentReplyService commentReplyService;
	@PostMapping("/comment/write")
	public ResponseEntity<Map<String, Object>> writecomment(@ModelAttribute("Comment") Comment comment,
			@Validated @ModelAttribute("CommentWriteValidation") CommentWriteValidation commentWriteValidation,BindingResult bindingResult) {
		
		
		log.info("cpmment={}",comment);
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			// 필드별로 발생한 모든 오류 메시지를 맵에 담음
			bindingResult.getFieldErrors().forEach(error -> {
				String fieldName = error.getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
			
			throw new ValidationException(errors);
		}
		
		commentService.save(comment);
		
		Map<String, Object> response = new HashMap<>();
		
		response.put("message", "댓글이 작성되었습니다.");
		
		return ResponseEntity.ok(response);
	}
	
		
	@PostMapping("/comment/delete")
	public ResponseEntity<Map<String, Object>> deletcomment(@RequestParam("commentId") Long commentId) {
		commentService.deleteComment(commentId);
		
		Map<String, Object> response = new HashMap<>();
		response.put("message", "댓글이 정상적으로 삭제되었습니다.");
		
		return ResponseEntity.ok(response);
	}
	
	/*
	 * 댓글작성후 보여준느 폼
	 */
	@GetMapping("/comment/getBoard")
	public ResponseEntity<Map<String, Object>> getBoard(Board board, Model model, HttpSession session,
			@ModelAttribute("CommentWriteValidation") CommentWriteValidation commentWriteValidation) {
		Map<String, Object> response = new HashMap<>();
		
		response.put("comments",  commentService.findComment(board.getBoardId()));
		return ResponseEntity.ok(response);
	}
	/*
	 * 댓글삭제 후 보여준느 폼
	 */
	@GetMapping("/comment/delete")
	public ResponseEntity<Map<String, Object>> deletcomment_view(@RequestParam("boardId") Long boardId) {
		
		Map<String, Object> response = new HashMap<>();
		response.put("comments",  commentService.findComment(boardId));
		
		return ResponseEntity.ok(response);
	}
	
	/*
	 * 대댓글 작성후 저장
	 * */
	@PostMapping("/comment/reply")
	public ResponseEntity<Map<String, Object>> replycomment_save(CommentReply commentReply,HttpServletRequest request) {
		
	
		Map<String, Object> response = new HashMap<>();

		commentReplyService.save(commentReply);

		response.put("message", "댓글이 정상적으로 작성되었습니다.");

		return ResponseEntity.ok(response);
	}
	
	/*
	 * 대댓글 보여주기 폼
	 * */
	@GetMapping("/comment/more")
	public ResponseEntity<Map<String, Object>> comment_moreview(CommentReply commentReply,HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		Map<String, Object> response = new HashMap<>();
		response.put("sessionNicename",(String)((Member)session.getAttribute("user")).getMemberNickname());
		response.put("comment_List",  commentReplyService.findCommentList(commentReply.getCommentId(),commentReply.getBoardId()));
		
		return ResponseEntity.ok(response);
	}
	/*
	 * 내댓글 삭제
	 * */
	@PostMapping("/comment/more/delete")
	public ResponseEntity<Map<String, Object>> comment_moredelete(CommentReply commentReply) {
		
		Map<String, Object> response = new HashMap<>();
		
		commentReplyService.delete(commentReply);
		response.put("message", "댓글이 정상적으로 삭제되었습니다.");
		response.put("redirectUrl", "/board/getBoard?boardId=" + commentReply.getBoardId());
		return ResponseEntity.ok(response);
	}
}
