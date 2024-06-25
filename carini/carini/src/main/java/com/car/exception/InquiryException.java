package com.car.exception;

import com.car.exception.errorcode.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InquiryException extends RuntimeException{

	private ErrorCode errorCode;
	private String message;
	
	@Override
	public String toString() {
		
		if(message==null) {
			return errorCode.getMessage();
		}
		
		return String.format("%s. %s", errorCode.getMessage(),message);
		
	}
	
}
