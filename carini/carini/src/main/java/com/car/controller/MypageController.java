package com.car.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.car.dto.Board;
import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.service.MemberService;
import com.car.service.BoardService;
import com.car.service.BookMarkService;
import com.mysql.cj.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MypageController {

	@Autowired
	private BookMarkService bookMarkService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private BoardService boardService;
	@GetMapping("/")
    public String backhome() {
		return "index.html";
	}
	

	/*
	 * 로그인전 나의정보를 눌렀을때
	 * */
	@GetMapping("/mypage/myinfo/no_login")
    public RedirectView myinfoNo_login(Model model) {
        
		model.addAttribute("loginMessage", "로그인이 필요합니다. 로그인 해주세요.");
		
		return new RedirectView("/member_login");
    }
	
	
	/*
	 * 회원정보 수정(나의 정보)
	 * */
    @GetMapping("/mypage/myinfo/{memberId}")
    public String myinfoview(@PathVariable("memberId") String memberId,Model model) {
       
		log.info("memberId = {}",memberId);
		
		Member member = memberService.findByMemberId(memberId);
		model.addAttribute("member", member);
		return "mypage/myinfo.html";
    }
    
    /*
	 * 닉네임 수정
	 * */
    @PostMapping("/mypage/myinfo/updatenickname/{memberId}")
    public RedirectView myInfoNicknameUpdate(@PathVariable("memberId") String memberId,@RequestParam("memberNickname") String memberNickname,Model model) {
    	
    	Member member = memberService.findByMemberId(memberId);
    	if(member !=null) {
    		memberService.updateMember(member,memberNickname);
    		Member newmember = memberService.findByMemberId(memberId);
    		model.addAttribute("member", newmember);
    		return new RedirectView("/mypage/myinfo/"+memberId);
    	}else {
    		return new RedirectView("/mypage/myinfo/"+memberId);

    	}
    }
    
    
    /*
     * 회원정보 모두 수정
     * */
    @PostMapping("/mypage/myinfo/updateAll/{memberId}")
    public RedirectView myInfoNicknameUpdate(@PathVariable("memberId") String memberId,
    		@RequestParam("memberPw") String memberPw,
    		@RequestParam("memberName") String memberName,
    		@RequestParam("memberEmail") String memberEmail,
    		@RequestParam("memberPhoneNum") String memberPhoneNum,
    		Model model) {
    	
	    	Member member = memberService.findByMemberId(memberId);
	    	if(member !=null) {
	    		member.setMemberPw(memberPw);
	    		member.setMemberName(memberName);
	    		member.setMemberEmail(memberEmail);
	    		member.setMemberPhoneNum(memberPhoneNum);
	    		memberService.updateAllMember(memberId,member);
	    		Member newmember = memberService.findByMemberId(memberId);
	    		model.addAttribute("member", newmember);
	    		return new RedirectView("/mypage/myinfo/"+memberId);
	    	}else {
	    		return new RedirectView("/mypage/myinfo/"+memberId);
	
	    	}
    }
	/*
	 * 회원탈퇴
	 * */
    @PostMapping("/mypage/myinfo/delete/{memberId}")
    public RedirectView myInfoDeletePwCheck(@PathVariable("memberId") String memberId,@RequestParam("memberPw") String memberPw,Model model) {
    	
    	Member member = memberService.findByMemberId(memberId);
    	
    	if(member !=null && member.getMemberPw().equals(memberPw)) {
    		memberService.deleteMember(member);
    		return new RedirectView("/");
    	}else {
    		model.addAttribute("member", member);
    		return new RedirectView("/mypage/myinfo/"+memberId);

    	}
    }
	
	/*
	 * 
	 * ==============================================================================================
	 * 즐겨찾기
	 * */
	
	/*
	 * 로그인전 즐겨찾기 눌렀을때
	 * */     
    @GetMapping("/mypage/bookmark/no_login")
    public String BookmarkNo_login(Model model) {
        
		model.addAttribute("loginMessage", "로그인이 필요합니다. 로그인 해주세요.");
		
		return "member/login.html";
    }
	
	/*
	 * bookmark 리스트
	 * */        
	@GetMapping("/mypage/bookmark/{memberId}")
    public String myPagebookmarkList(@PathVariable("memberId") String memberId, Model model) {
		
        Member member = memberService.findByMemberId(memberId);
        List<Bookmark> BookmarkCar_ID = bookMarkService.findAllBookmarkCar(member.getMemberId());
        
        List<Car> BookmarkCarList = bookMarkService.findAllCar(BookmarkCar_ID);
        
        
        model.addAttribute("member", member);
        model.addAttribute("BookmarkCarList", BookmarkCarList);
        
        return "mypage/bookmark.html";
    }

	/*
	 * bookmark 삭제
	 * */
	@PostMapping("/mypage/bookmark/delete/{carId}/{memberId}")
    public RedirectView myPagebookmarkdelete(@PathVariable("carId") int carId,@PathVariable("memberId") String memberId) {
		bookMarkService.findBookmarkByCarDelete(carId,memberId);
		
		return new RedirectView("/mypage/bookmark/"+memberId);
    }
	
	
	/*
	 * 
	 * ==============================================================================================
	 * 나의 게시물
	 * */
	
	
	/*
	 * 로그인전 내 게시물을 눌렀을때
	 * */     
    @GetMapping("/mypage/myboard/no_login")
    public String myboardNo_login(Model model) {
        
		model.addAttribute("loginMessage", "로그인이 필요합니다. 로그인 해주세요.");
		
		return "member/login.html";
    }
	@GetMapping("/mypage/myBoard/{memberId}")
    public String myPageMyboard(@PathVariable("memberId") String memberId,Model model) {
		
		Member member = memberService.findByMemberId(memberId);

		
		List<Board> boards = boardService.boardList(member);
	
		
		model.addAttribute("boards", boards);
		return "mypage/myboard.html";
    }
	
	/*
	 * 내 게시물 상세 조회
	 * */
	@PostMapping("/mypage/myBoard/{boardId}")
    public RedirectView myPagemyboard(@PathVariable("boardId") Long boardId , Model model) {
		
		return new RedirectView("/mypage/bookmark/");
	//	return 
    }
}
