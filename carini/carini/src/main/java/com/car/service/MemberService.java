package com.car.service;


import java.util.List;

import com.car.dto.Member;

public interface MemberService {

	//long getTotalRowCont(Member member);
	//Member getMember(Member member);
	//Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord);

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

}
