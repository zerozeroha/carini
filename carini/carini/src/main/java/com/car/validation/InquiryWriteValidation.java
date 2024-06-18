package com.car.validation;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InquiryWriteValidation {
	
	
	@NotBlank(message = " 내용은 필수 조건입니다.")
	private String reContent;
	    
	@NotBlank(message = "제목은 필수 조건입니다.")
	private String reTitle;
	
}
