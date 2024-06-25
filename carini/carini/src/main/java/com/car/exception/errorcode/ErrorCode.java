package com.car.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	
	
	BOARD_DELETE(HttpStatus.INTERNAL_SERVER_ERROR,"파일 삭제 중 오류가 발생하였습니다."),
	CODE_NUMBER_MISMATCH(HttpStatus.BAD_REQUEST,"인증번호가 일치하지 않습니다. 다시 입력하세요!"),
	INQUIRY_NON_EXIST(HttpStatus.BAD_REQUEST,"게시글이 존재하지 않습니다.");
	
	
	private HttpStatus status;
	private String message;
	
}
