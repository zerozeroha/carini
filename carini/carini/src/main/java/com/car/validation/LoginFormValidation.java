package com.car.validation;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class LoginFormValidation {

	@NotEmpty(message = "아이디를 입력해주세요.")
    private String memberId;
	
	@NotBlank(message = "비밀번호를 입력해주세요.")
    private String memberPw;

}

