package com.car.validation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Find_pwFormValidation {

	@NotBlank(message="이름은 필수 조건입니다.")
	private String memberId;
	
	@NotBlank(message="핸드폰 번호는 필수 조건입니다.")
	private String memberPhoneNumber;
	
}
