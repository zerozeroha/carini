package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.dto.Inquiry;
import com.car.dto.Member;
import com.car.persistence.InquiryRepository;
import com.car.service.InquiryService;

@Service
public class InquiryServiceImpl implements InquiryService{

	@Autowired
	private InquiryRepository inquiryRepository;
	
	@Override
	public List<Inquiry> findbyIdinquiry(Member user) {
		
		return inquiryRepository.findByMemberId(user.getMemberId());
	}
	
	@Override
	public void inquiryWrite(Inquiry inquiry) {
		
		inquiryRepository.save(inquiry);
		
	}

	@Override
	public void inquirydelte(Inquiry inquiry) {
		
		inquiryRepository.deleteById(inquiry.getReId());
		
	}

	@Override
	public Inquiry findbyreIdinquiry(Long reId) {
		Optional<Inquiry> inquiry =  inquiryRepository.findById(reId);
		return inquiry.get();
	}
	

}
