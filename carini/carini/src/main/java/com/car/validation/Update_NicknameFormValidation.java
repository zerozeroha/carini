package com.car.validation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class  Update_NicknameFormValidation{

	
	 @NotBlank(message = "닉네임을 입력해주세요.")
	 private String memberNickname;
}
