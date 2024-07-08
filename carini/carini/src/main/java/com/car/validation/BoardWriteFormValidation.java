package com.car.validation;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardWriteFormValidation {
  
	@NotBlank(message = "제목을 입력해주세요.")
	private String boardTitle;
	
	@NotBlank(message = "내용을 입력해주세요.")
	private String boardContent;

}
