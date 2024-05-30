package com.car.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.car.dto.Member;
import java.util.List;



public interface MemberRepository extends JpaRepository<Member, String>{
	
	Optional<Member> findByMemberId(String memberId);
	List<Member> findByMemberEmail(String memberEmail);
}
