package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.car.service.MemberService;

import jakarta.transaction.Transactional;

	

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;

	/*
	 * 멤버 추가하기
	 * */
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
	/*
	 * 해당 멤버 찾기
	 * */
	@Override
	public Member findMember(Member member) {
		
		Optional<Member> findeMember = memberRepository.findByMemberId(member.getMemberId());
		
		if(!findeMember.isPresent()) {
			
			return null;
		}else {
			return findeMember.get();
		}
	}
	
	/*
	 * 모든 멤버 찾기
	 * */
	@Override
	public List<Member> findAllMember() {
		List<Member> MemberList = memberRepository.findAll();
		return MemberList;
	}

	/*
	 * id로 멤버찾기
	 * */
	@Override
	public Member findByMemberId(String id) {
		
		System.out.println(id);
		Optional<Member> findeMember = memberRepository.findByMemberId(id);
		
		return findeMember.get();
	}
	
	/*
	 * 회원 탈퇴
	 * */
	@Override
	public void deleteMember(Member member) {
		
		memberRepository.delete(member);
		
	}
	
	/*
	 * 닉네임 수정
	 * */
	@Override
	@Transactional
	public void updateMember(Member member, String newmemberNickname) {
		
		memberRepository.updateMemberNickname(newmemberNickname,member.getMemberId());
		
	}
	
	/*
	 * 회원정보 수정
	 * */
	@Override
	@Transactional
	public void updateAllMember(String memberId, Member member) {
		memberRepository.updateAllMember(memberId,member.getMemberId(),member.getMemberPw(),
				member.getMemberName(),member.getMemberEmail(),member.getMemberPhoneNum());
		
		
	}
	
	
	@Override
	@Transactional
	public void updateMember(Member member, String newmemberNickname, String newmemberNickname2) {
		memberRepository.updateMemberNickname(newmemberNickname,newmemberNickname2,member.getMemberId());
	}

	
	
	
}
