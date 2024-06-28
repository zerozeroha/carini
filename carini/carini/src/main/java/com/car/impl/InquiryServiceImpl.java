package com.car.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Override
	public Page<Inquiry> getInquiryList(Pageable pageable, String searchType, String searchWord) {
		if(searchType.equalsIgnoreCase("reContent")) {
			return inquiryRepository.findByReContentContaining(searchWord, pageable);
			
		} else if(searchType.equalsIgnoreCase("memberId")) {
			return inquiryRepository.findByMemberIdContaining(searchWord, pageable);
			
		} else if(searchType.equalsIgnoreCase("reTitleRq")) {
			return inquiryRepository.findByReTitleRqContaining(searchWord, pageable);
			
		} else if(searchType.equalsIgnoreCase("reContentRq")) {
			return inquiryRepository.findByReContentRqContaining(searchWord, pageable);
			
		} else {
			return inquiryRepository.findByReTitleContaining(searchWord, pageable);
		}
	}
	
	@Override
	public void answerInquiry(Inquiry inquiry) {
		inquiry.setReDateRq(new Date());
		inquiryRepository.save(inquiry);
	}

	@Override
	public void deleteInquiryById(Long reId) {
		inquiryRepository.deleteById(reId);
	}
	
	@Override
	public int countInquiryById(String memberId) {
		return inquiryRepository.getBookmarkCount(memberId);
	}
	
	
}
