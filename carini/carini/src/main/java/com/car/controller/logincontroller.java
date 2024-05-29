package com.car.controller;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.car.service.MemberService;

import org.springframework.ui.Model;


@Controller
public class logincontroller {

	@Value("${pw_role.PASSWORD_REGEX}")
    private static String PASSWORD_REGEX;
	
    @Autowired
	private MemberService memberService;
	
	
	@GetMapping("/signup")
	public String joinView(Model model) {
		List<Member> memberList = memberService.findAllMember();
		System.out.println(memberList.get(1));
		model.addAttribute("models", memberList);
		return "member/signup.html";
	}
	
	@PostMapping("/signup")
	public String join_result(Member member) {
			System.out.println(member.getMemberId());
			System.out.println(member.getMemberEmail());
			System.out.println(member.getMemberPw());
			
			final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
			if (member.getMemberPw() == null || member.getMemberPw().isEmpty()) {
				return "비밀번호를 입력해주세요.";
		    } else if (!PASSWORD_PATTERN.matcher(member.getMemberPw()).matches()) {
		        return "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해야 합니다.";
		    } else {
		    	member.setMemberSocial("회원");
				member.setMemberRole("사용자");
				
				Member save_member=memberService.insertMember(member);
				
				return "member/login.html";
		    }
	}
	
	@GetMapping("/member_login")
	public String loginView(Model model) {
		

		return "member/login.html";
	}
	
	@PostMapping("/member_login")
	public String login_result(Member member,Model model) {
		 	
		String memberId = member.getMemberId();
		String memberPw = member.getMemberPw();
		System.out.println("ID: " + memberId);
	    System.out.println("Password: " + memberPw);
			
		if(memberId.isEmpty() || memberPw.isEmpty()) {
	    		return "/login";
	    }
			
		System.out.println(memberId);
		// 데이터베이스에서 사용자 조회
		Member findmember = memberService.findMember(member);
			 
	     // 사용자가 존재하고 비밀번호가 일치하는지 확인
	     if (findmember != null && findmember.getMemberPw().equals(memberPw)) {
	     // 로그인 성공 시 홈 페이지로 이동
	    	 model.addAttribute("member", findmember);
	            
	    	 return "homepage/home";
	     } else {
	         // 로그인 실패 시 로그인 페이지로 리디렉션
	         System.out.println("로그인 실패: 잘못된 아이디 또는 비밀번호");
	         return "redirect:/login";
	        }
	}
}
