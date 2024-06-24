package com.car.exception;

import org.springframework.http.HttpStatus;

import com.car.exception.errorcode.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardDeleteFileException extends RuntimeException{

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
