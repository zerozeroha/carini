package com.car.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.car.dto.Comment;
import com.car.exception.ValidationException;
import com.car.service.CommentService;
import com.car.validation.CommentWriteValidation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

	private final CommentService commentService;
	
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
		response.put("redirectUrl", "/board/getBoard?boardId="+comment.getBoardId());
		
		return ResponseEntity.ok(response);
	}
	
		
	@PostMapping("/comment/delete")
	public ResponseEntity<Map<String, Object>> deletcomment(@RequestParam("commentId") Long commentId,@RequestParam("boardId") Long boardId) {
		commentService.deleteComment(commentId);
		
		Map<String, Object> response = new HashMap<>();
		response.put("message", "댓글이 정상적으로 삭제되었습니다.");
		response.put("redirectUrl", "/board/getBoard?boardId="+boardId);
		
		return ResponseEntity.ok(response);

	}
}
