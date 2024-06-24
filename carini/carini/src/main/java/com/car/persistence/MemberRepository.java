package com.car.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.car.dto.Board;
import com.car.dto.Member;
import java.util.List;


@Repository
public interface MemberRepository extends JpaRepository<Member, String>{
	
	Optional<Member> findByMemberId(String memberId);
	List<Member> findByMemberEmail(String memberEmail);
	List<Member> findByMemberNickname(String memberNickname);
	List<Member> findByMemberPhoneNum(String memberPhoneNum);
	
	Optional<Member> findByMemberNameAndMemberPhoneNum(String memberName,String memberPhoneNum);
	Optional<Member> findByMemberIdAndMemberPhoneNum(String memberId,String memberPhoneNum);

	
	@Modifying
	@Query("UPDATE Member m SET m.memberNickname = :newNickname WHERE m.memberId = :memberId")
	void updateMemberNickname(@Param("newNickname") String newNickname, @Param("memberId") String memberId);
	
	@Modifying
	@Query("UPDATE Member m SET m.memberSocialNickname = :newmemberSocialNickname WHERE m.memberId = :memberId")
	void updateMemberSocialNickname(@Param("newmemberSocialNickname") String newmemberSocialNickname, @Param("memberId") String memberId);
	
	@Modifying
	@Query("UPDATE Member m SET m.memberId = :newmemberId , m.memberPw = :newmemberPw , m.memberName = :newmemberName , m.memberEmail = :newmemberEmail , m.memberPhoneNum = :newmemberPhoneNum WHERE m.memberId = :memberId")
	void updateAllMember(@Param("memberId") String memberId, @Param("newmemberId") String newmemberId, @Param("newmemberPw") String newmemberPw, @Param("newmemberName") String newmemberName, @Param("newmemberEmail") String newmemberEmail, @Param("newmemberPhoneNum") String newmemberPhoneNum);
	
	@Modifying
	@Query("UPDATE Member m SET m.memberPw = :newmemberPw WHERE m.memberId = :memberId")
	void updateMemberPw(@Param("newmemberPw") String newmemberPw,@Param("memberId") String memberId);
	
	Page<Member> findByMemberNicknameContaining(String searchWord, Pageable pageable);
	Page<Member> findByMemberPhoneNumContaining(String searchWord, Pageable pageable);
	Page<Member> findByMemberIdContaining(String searchWord, Pageable pageable);
	
//	@Modifying
//    @Query("UPDATE Member m SET m.memberNickname = :newNickname , m.memberSocialNickname = :newNickname2 WHERE m.memberId = :memberId")
//    void updateMemberNickname(@Param("newNickname") String newNickname, @Param("newNickname2") String newNickname2 ,@Param("memberId") String memberId);


}
