package com.car.validation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeUpdateFormValidation {
	
	@NotBlank(message = "제목은 필수 조건입니다.")
	private String noticeTitle;
	
	@NotBlank(message = "내용은 필수 조건입니다.")
	private String noticeContent;

}
