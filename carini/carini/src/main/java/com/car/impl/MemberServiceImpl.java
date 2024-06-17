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
	public Member findMember(String memberId) {
		
		Optional<Member> findeMember = memberRepository.findByMemberId(memberId);
		
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
		System.out.println("sadadsa");
		Optional<Member> findeMember = memberRepository.findByMemberId(id);
		System.out.println(findeMember.isPresent());
		if(!findeMember.isPresent()) {
			return null;
		}
		
		return findeMember.get();
	}
	/*
	 * email로 멤버찾기
	 * */
	@Override
	public List<Member> findByMemberEmail(String memberEmail) {
		
		List<Member> findeMember = memberRepository.findByMemberEmail(memberEmail);
		
		return findeMember;
	}
	/*
	 * 닉네임으로로 멤버찾기
	 * */
	@Override
	public List<Member> findByMemberNickname(String memberNickname) {
		List<Member> findeMember = memberRepository.findByMemberNickname(memberNickname);
		
		return findeMember;
	}
	/*
	 * 회원 탈퇴
	 * */
	@Override
	public void deleteMember(Member member) {
		
		memberRepository.delete(member);
		
	}
	
	/*
	 * 닉네임 수정(회원)
	 * */
	@Override
	@Transactional
	public void updateMember(Member member, String newmemberNickname) {
		
		memberRepository.updateMemberNickname(newmemberNickname,member.getMemberId());
		
	}
	/*
	 * 닉네임 수정(소셜)
	 * */
	@Override
	@Transactional
	public void updatememberSocialNickname(Member member, String newmemberSocialNickname) {
		memberRepository.updateMemberSocialNickname(newmemberSocialNickname,member.getMemberId());
		
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
	public List<Member> findByMemberPhoneNum(String memberPhoneNum) {
		List<Member> findeMember= memberRepository.findByMemberPhoneNum(memberPhoneNum);
		return findeMember;
	}
	
//	@Override
//	@Transactional
//	public void updateMember(Member member, String newmemberNickname, String newmemberNickname2) {
//		memberRepository.updateMemberNickname(newmemberNickname,newmemberNickname2,member.getMemberId());
//	}



}















