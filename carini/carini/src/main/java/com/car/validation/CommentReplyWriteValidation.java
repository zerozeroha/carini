package com.car.validation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentReplyWriteValidation {

	@NotBlank(message="빈 댓글은 입력하수 없습니다.")
	private String replyTextArea;
	
}
