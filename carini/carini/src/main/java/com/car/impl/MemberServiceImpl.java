package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.car.service.MemberService;

	

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Override
	public Member insertMember(Member member) {
        
		Optional<Member> findeMember = memberRepository.findByMemberId(member.getMemberId());
		
		if(!findeMember.isPresent()) {
			
			memberRepository.save(member);
			
			return member;
		}else {
			return findeMember.get();
		}
        
    }

	@Override
	public Member findMember(Member member) {
		
		Optional<Member> findeMember = memberRepository.findByMemberId(member.getMemberId());
		
		if(!findeMember.isPresent()) {
			
			memberRepository.save(member);
			return member;
		}else {
			return findeMember.get();
		}
	}

	@Override
	public List<Member> findAllMember() {
		List<Member> MemberList = memberRepository.findAll();
		return MemberList;
	}
	
}
