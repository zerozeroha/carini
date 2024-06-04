package com.car.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.car.dto.Member;
import java.util.List;


@Repository
public interface MemberRepository extends JpaRepository<Member, String>{
	
	Optional<Member> findByMemberId(String memberId);
	List<Member> findByMemberEmail(String memberEmail);
	
	@Modifying
	@Query("UPDATE Member m SET m.memberNickname = :newNickname WHERE m.memberId = :memberId")
	void updateMemberNickname(@Param("newNickname") String newNickname, @Param("memberId") String memberId);
	
	@Modifying
	@Query("UPDATE Member m SET m.memberId = :newmemberId , m.memberPw = :newmemberPw , m.memberName = :newmemberName , m.memberEmail = :newmemberEmail , m.memberPhoneNum = :newmemberPhoneNum WHERE m.memberId = :memberId")
	void updateAllMember(@Param("memberId") String memberId, @Param("newmemberId") String newmemberId, @Param("newmemberPw") String newmemberPw, @Param("newmemberName") String newmemberName, @Param("newmemberEmail") String newmemberEmail, @Param("newmemberPhoneNum") String newmemberPhoneNum);



}