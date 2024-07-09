package com.car.exception;

import java.util.Map;

import com.car.exception.errorcode.ErrorCode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CodeNumberException extends RuntimeException{
	
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
