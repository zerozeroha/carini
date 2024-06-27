package com.car.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Inquiry;
import com.car.dto.Member;

@Service
public interface InquiryService {

	void inquiryWrite(Inquiry inquiry);
	List<Inquiry> findbyIdinquiry(Member user);
	void inquirydelte(Inquiry inquiry);
	Inquiry findbyreIdinquiry(Long reId);
	Page<Inquiry> getInquiryList(Pageable pageable, String searchType, String searchWord);
	void answerInquiry(Inquiry inquiry);
	void deleteInquiryById(Long reId);
}
