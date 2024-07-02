package com.car.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.car.dto.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface MemberService {

	//long getTotalRowCont(Member member);
	//Member getMember(Member member);
	//Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord);
	Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord);
	Member insertMember(Member member);
	Member findMember(String memberId);
	List<Member> findAllMember();
	void updateMember(Member member,String newmemberNickname);
	void deleteMember(Member member);
	Member findByMemberId(String id);
	void updateAllMember(String memberId, Member member);

	//void updateMember(Member member, String memberNickname, String memberNickname2);

	List<Member> findByMemberEmail(String memberEmail);
	List<Member> findByMemberNickname(String memberNickname);
	void updatememberSocialNickname(Member member, String memberSocialNickname);
	List<Member> findByMemberPhoneNum(String memberPhoneNum);
	SingleMessageSentResponse sendmessage(String phone, String codeNumber, String aPIKEY, String sECRETKEY,String FROM_NUMBER);

	Member SMSfindMemberPw(String memberId, String memberPhoneNumber,HttpSession session);
	Member SMSfindMember(String memberName, String memberPhoneNumber, HttpSession session);
	void updatepw(String memberId, String newPw);
	boolean passwordCheck(Member member, String memberPw);


}
