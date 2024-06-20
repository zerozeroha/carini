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
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
    		message = "비밀번호는 숫자, 소문자, 대문자, 특수문자를 포함하여 8자 이상이어야 합니다.")
    private String memberPw;

}

