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
	public String join_result(Member member,Model model) {
			
			List<Member> findmemberEmail=memberService.findByMemberEmail(member.getMemberEmail());
			List<Member> findmemberNickname=memberService.findByMemberNickname(member.getMemberNickname());
			if(!findmemberEmail.isEmpty()) {
				model.addAttribute("msg", "사용중인 이메일입니다.");
                model.addAttribute("url", "/signup");
                return "alert";  
			}
			
			if(!findmemberNickname.isEmpty()) {
				model.addAttribute("msg", "사용중인 닉네임입니다.");
                model.addAttribute("url", "/signup");
                return "alert";  
			}
			
			final Pattern PASSWORD_PATTERN = Pattern.compile(passwordRegex);
			if (member.getMemberPw() == null || member.getMemberPw().isEmpty()) {
				model.addAttribute("msg", "비밀번호를 입력해주세요.");
                model.addAttribute("url", "/signup");
                return "alert";  
		    } else if (!PASSWORD_PATTERN.matcher(member.getMemberPw()).matches()) {
		    	model.addAttribute("msg", "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해야 합니다.");
                model.addAttribute("url", "/signup");
                return "alert";
		    } else {
		    	member.setMemberSocial("회원");
				member.setMemberRole("사용자");
				
				Member save_member=memberService.insertMember(member);
				model.addAttribute("msg", "성공적으로 회원가입이 되었습니다.");
                model.addAttribute("url", "member/login.html");
                return "alert";
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
	public String goHome( HttpSession session)  {
//		System.out.println(member.getMemberId());
//		System.out.println(member.getMemberNickname());
//		System.out.println("-=============");
		// HttpSession session = request.getSession();
		Member user = (Member) session.getAttribute("user");
		System.out.println("home----------" + user);
		
		if(user == null) {
			return "redirect:/";
		}	
		return "homepage/home.html";
	}
	
	/*
	 * 로그인
	 * */
	@PostMapping("/member_login_check")
	public String login_result(Member member,Model model,HttpServletRequest request, HttpSession session) {

		String memberId = member.getMemberId();
		String memberPw = member.getMemberPw();
		
		
		if(memberId.isEmpty() || memberPw.isEmpty()) {
	    		return "/login";
	    }
		// 데이터베이스에서 사용자 조회
		Member findmember = memberService.findMember(member);
		
	     // 사용자가 존재하고 비밀번호가 일치하는지 확인
	     if (findmember != null && findmember.getMemberPw().equals(memberPw)) {
	    	 findmember.setMemberPw("*****");
	    	 findmember.setMemberPhoneNum("***-****-****");
	    	 findmember.setMemberEmail("****@****.***");
	    	 // 로그인 성공 시 세션에 멤버정보 저장하고 홈페이지로 이동
	    	 session.setAttribute("user", findmember);
	    	 
	    	   	 
	    	 
	    	 return "redirect:/home";
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
