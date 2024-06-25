package com.car.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Find_pwFormValidation {

	@NotBlank(message="아이디를 입력해주세요.")
	private String memberId;
	
	@NotBlank(message="핸드폰 번호를 입력해주세요.")
	@Pattern(regexp = "^\\d{10,11}$", message = "올바른 핸드폰 번호 형식이어야 합니다. (예: 01012345678)")
	private String memberPhoneNumber;
	
}
