package com.car.controller;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.RequestAttributes;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.car.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;


@Controller
@SessionAttributes("member")
public class LoginController {

	@Value("${pw-role.password-rejex}")
	private String passwordRegex;

    @Autowired
	private MemberService memberService;
	
    @ModelAttribute("member")
	public Member setMember() {
		return new Member(); // 기본 Member 객체를 세션에 저장
	}
    /*
     * 회원가입 view
     * */
	@GetMapping("/signup")
	public String joinView(Model model) {
		List<Member> memberList = memberService.findAllMember();
		model.addAttribute("models", memberList);
		return "member/signup.html";
	}
	

    /*
     * 회원가입 
     * */
	@PostMapping("/signup")
	public String join_result(Member member) {
			System.out.println(member.getMemberId());
			System.out.println(member.getMemberNickname());
			System.out.println(member.getMemberPw());
			final Pattern PASSWORD_PATTERN = Pattern.compile(passwordRegex);
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
	
	/*
	 * 로그인 view
	 * */
	@GetMapping("/member_login")
	public String loginView(Model model) {
		
		return "member/login.html";
	}
	
	@GetMapping("/home")
	public String goHome(@ModelAttribute("member") Member member)  {
		System.out.println(member.getMemberId());
		if(member.getMemberId() == null) {
			return "redirect:/";
		}	
		return "homepage/home.html";
	}
	
	/*
	 * 로그인
	 * */
	@PostMapping("/member_login_check")
	public String login_result(Member member,Model model,HttpServletRequest request,HttpSession session) {

		String memberId = member.getMemberId();
		String memberPw = member.getMemberPw();
		
		
		if(memberId.isEmpty() || memberPw.isEmpty()) {
	    		return "/login";
	    }
		// 데이터베이스에서 사용자 조회
		Member findmember = memberService.findMember(member);
		
	     // 사용자가 존재하고 비밀번호가 일치하는지 확인
	     if (findmember != null && findmember.getMemberPw().equals(memberPw)) {
	    
	    	 // 로그인 성공 시 세션에 멤버정보 저장하고 홈페이지로 이동

	    	 session = request.getSession();
	    	 session.setAttribute("member", findmember);
	    	 
    	 	return "homepage/home.html";
	     } else {
	         // 로그인 실패 시 로그인 페이지로 리디렉션
	         System.out.println("로그인 실패: 잘못된 아이디 또는 비밀번호");
	         return "redirect:/login";
	     }
	}
	
	@PostMapping("/logout")
	public String logout(HttpServletRequest request) {
		//세션을 삭제
		HttpSession session = request.getSession(false); 
        // session이 null이 아니라는건 기존에 세션이 존재했었다는 뜻이므로
        // 세션이 null이 아니라면 session.invalidate()로 세션 삭제해주기.
		if(session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}
}
