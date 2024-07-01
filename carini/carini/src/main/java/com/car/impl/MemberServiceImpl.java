package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.car.service.BoardService;
import com.car.service.BookMarkService;
import com.car.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

	

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord) {
		if(searchType.equalsIgnoreCase("memberNickname")) {
			return memberRepository.findByMemberNicknameContaining(searchWord, pageable);
		} else if(searchType.equalsIgnoreCase("memberPhoneNum")) {
			return memberRepository.findByMemberPhoneNumContaining(searchWord, pageable);
		} else {
			return memberRepository.findByMemberIdContaining(searchWord, pageable);
		}
	}

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
		Optional<Member> findeMember = memberRepository.findByMemberId(id);
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
		System.out.println("============");
		System.out.println(member);
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

	@Override
	public SingleMessageSentResponse sendmessage(String phone, String codeNumber, String APIKEY, String SECRETKEY,String FROM_NUMBER) {
		
		DefaultMessageService messageService =   NurigoApp.INSTANCE.initialize(APIKEY, SECRETKEY, "https://api.coolsms.co.kr");
		Message message = new Message();
		
		message.setFrom(FROM_NUMBER);	//발신번호
		message.setTo(phone);	// 수신번호
		message.setText("CARINI[인증번호]"+codeNumber +"를 입력해주세요!");
		
		SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
		
		return response;
	}
	
	@Override
	@Transactional
	public Member SMSfindMember(String memberName, String memberPhoneNumber,HttpSession session) {

		Optional<Member> member = memberRepository.findByMemberNameAndMemberPhoneNum(memberName,memberPhoneNumber);
		
		if(!member.isPresent()) {
			return null;
		}
		
		session.setAttribute("find_idMember", member.get());
		//return member;
		return member.get();
	}
	
	@Override
	@Transactional
	public Member SMSfindMemberPw(String memberId, String memberPhoneNumber,HttpSession session) {
		Optional<Member> member = memberRepository.findByMemberIdAndMemberPhoneNum(memberId,memberPhoneNumber);
		if(!member.isPresent()) {
			return null;
		}
		session.setAttribute("find_pwMember", member.get());
		return member.get();
	}
	
	/*
	 * 비밀번호 수정
	 * */
	@Override
	@Transactional
	public void updatepw(String memberId, String newmemberPw) {
		memberRepository.updateMemberPw(newmemberPw,memberId);
	}
	
	/*
	 * 비밀번호 암호화 체크
	 * */
	public boolean passwordCheck(Member findmember, String memberPw) {
		boolean passwordCheck = passwordEncoder.matches(memberPw, findmember.getMemberPw());
		
		if(passwordCheck) {
			return passwordCheck;
		}
		return false;
	}
}