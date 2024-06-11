package com.car.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.car.dto.Member;
import com.car.dto.PagingInfo;
import com.car.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"member", "pagingInfo"})
public class ModelController {
	
	@Autowired
	private MemberService memberService;
	
	public PagingInfo pagingInfo = new PagingInfo();
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member(); // 기본 Member 객체를 세션에 저장
	}
	
	/*
	 * 모델 목록보기
	 * */

	
	
	

}
	
















