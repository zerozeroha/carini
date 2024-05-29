package com.car.service;


import java.util.List;

import com.car.dto.Member;

public interface MemberService {

	//long getTotalRowCont(Member member);
	//Member getMember(Member member);
	//Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord);

	Member insertMember(Member member);
	Member findMember(Member member);
	List<Member> findAllMember();
	//void updateMember(Member member);
	//void deleteMember(Member member);


}
