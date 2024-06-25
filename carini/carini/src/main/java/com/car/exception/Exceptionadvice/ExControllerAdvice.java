package com.car.exception.Exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.car.exception.BoardDeleteFileException;
import com.car.exception.ValidationException;
import com.car.exception.CodeNumberException;
import com.car.exception.InquiryException;

@RestControllerAdvice
public class ExControllerAdvice {

	@ExceptionHandler
	public ResponseEntity<ErrorResult> BoardDeleteFileException(BoardDeleteFileException e){
		
		ErrorResult errorResult = new ErrorResult(e.getErrorCode().name(), e.getErrorCode().getMessage());
		
		return new ResponseEntity<ErrorResult>(errorResult,e.getErrorCode().getStatus()); 
	}
	
	
	@ExceptionHandler
	public ResponseEntity<ErrorResult> ValidationException(ValidationException ex){
		
		ErrorResult errorResult = new ErrorResult(ex.getErrors(),"정보가 일치하지 않습니다.");
		
		return new ResponseEntity<ErrorResult>(errorResult,HttpStatus.BAD_REQUEST); 
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResult> CodeNumberException(CodeNumberException e){
		
		
		ErrorResult errorResult = new ErrorResult(e.getErrorCode().name(), e.getErrorCode().getMessage());

		return new ResponseEntity<ErrorResult>(errorResult,e.getErrorCode().getStatus()); 
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorResult> InquiryException(InquiryException e){
		
		ErrorResult errorResult = new ErrorResult(e.getErrorCode().name(), e.getErrorCode().getMessage());

		return new ResponseEntity<ErrorResult>(errorResult,e.getErrorCode().getStatus()); 
	}
}
