package com.car.validation;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardWriteFormValidation {

	
	@NotBlank(message = "제목은 필수 조건입니다.")
	private String boardTitle;
	
	@NotBlank(message = "내용은 필수 조건입니다.")
	private String boardContent;

}
