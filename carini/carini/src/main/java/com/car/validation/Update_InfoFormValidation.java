package com.car.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Update_InfoFormValidation {
	
	@NotBlank(message = "비밀번호룰 입력해주세요")
	@Pattern(regexp = "^$|^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "8~16자의 영문 대/소문자,숫자,특수문자를 입력해주세요")
	private String memberPw;

	@NotBlank(message = "핸드폰 번호를 입력해주세요.")
	@Pattern(regexp = "^$|^\\d{10,11}$", message = "올바른 핸드폰 번호 형식이어야 합니다. (예: 01012345678)")
	private String memberPhoneNum;

	@NotBlank(message = "이메일을 입력해주세요.")
	@Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "올바른 이메일 형식이어야 합니다 (예 qqqq@naver.com)")
	private String memberEmail;

	private String memberName;
}
