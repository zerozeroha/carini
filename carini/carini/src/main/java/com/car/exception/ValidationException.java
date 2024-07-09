package com.car.exception;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ValidationException extends RuntimeException{

	private Map<String, String> errors;

	private String message;
    public ValidationException(Map<String, String> errors) {
        this.errors = errors;
        
    }
	public ValidationException(Map<String, String> errors, String message) {
		this.errors = errors;
		this.message = message;
	}
	
}
