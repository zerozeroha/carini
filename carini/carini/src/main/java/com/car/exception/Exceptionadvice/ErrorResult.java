package com.car.exception.Exceptionadvice;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {

    private Map<String, String> errors;

	private String code;
	private String message;
	private boolean success;
	private String redirectUrl;
	
	public ErrorResult(Map<String, String> errors,String message) {
		this.errors=errors;
		this.code="validation";
		this.message=message;
		this.success=false;

	}
	public ErrorResult(String code, String message) {
		this.code=code;
		this.message=message;
		this.success=false;
	}
	public ErrorResult(String code, String message, String redirectUrl) {
		this.code=code;
		this.message=message;
		this.success=false;
		this.redirectUrl=redirectUrl;
	}
}
