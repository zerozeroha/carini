package com.car.controller;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.LocaleResolver;

import com.car.dto.Board;
import com.car.dto.Member;
import com.car.dto.Notice;
import com.car.dto.PagingInfo;
import com.car.service.BoardService;
import com.car.service.MemberService;
import com.car.service.NoticeService;
import com.car.validation.AdminMemberValidation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
@SessionAttributes({"user", "pagingInfo"})
public class AdminController {
	
	@Autowired
	private BoardService boardService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private NoticeService noticeService;
	   
	   
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private LocaleResolver localeResolver;
	
	public PagingInfo pagingInfo = new PagingInfo();
	
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member(); // 기본 Member 객체를 세션에 저장
	}
	
	
	@GetMapping("/adminList")
	public String adminList(Model model, HttpSession session) {
		Member user = (Member) session.getAttribute("user");
		model.addAttribute(user);
		
		return "admin/adminList";
	}
	
	/*
	 * 회원(Member) 관리 =======================================================================
	 * */
	
	@GetMapping("/memberList")
	public String showMemberList(Model model, Member member,
       @RequestParam(name = "curPage", defaultValue = "0") int curPage,
       @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
       @RequestParam(name = "searchType", defaultValue = "memberNickname") String searchType,
       @RequestParam(name = "searchWord", defaultValue = "") String searchWord) {
	   
	    curPage = Math.max(curPage, 0);  // Ensure curPage is not negative
	    Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("memberDate").descending());
	    Page<Member> pagedResult = memberService.getMemberList(pageable, searchType, searchWord);

	    
	    int totalRowCount  = (int)pagedResult.getNumberOfElements();
	    int totalPageCount = pagedResult.getTotalPages();
	    int pageSize       = pagingInfo.getPageSize();
	    int startPage      = (curPage / pageSize) * pageSize + 1;
	    int endPage        = startPage + pageSize - 1;
	    endPage = endPage > totalPageCount ? (totalPageCount > 0 ? totalPageCount : 1) : endPage;
	    
	
	    pagingInfo.setCurPage(curPage);
	    pagingInfo.setTotalRowCount(totalRowCount);
	    pagingInfo.setTotalPageCount(totalPageCount);
	    pagingInfo.setStartPage(startPage);
	    pagingInfo.setEndPage(endPage);
	    pagingInfo.setSearchType(searchType);
	    pagingInfo.setSearchWord(searchWord); 
	    pagingInfo.setRowSizePerPage(rowSizePerPage);
	    
	    model.addAttribute("pagingInfo", pagingInfo);
	    model.addAttribute("pagedResult", pagedResult);
	    model.addAttribute("pageable", pageable);
	    model.addAttribute("cp", curPage);
	    model.addAttribute("sp", startPage);
	    model.addAttribute("ep", endPage);
	    model.addAttribute("ps", pageSize);
	    model.addAttribute("rp", rowSizePerPage);
	    model.addAttribute("tp", totalPageCount);
	    model.addAttribute("st", searchType);
	    model.addAttribute("sw", searchWord);
	    model.addAttribute("memberList", pagedResult.getContent()); // Add this line
	
	    return "admin/memberList";
	}
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

	@GetMapping("/updateMember")
	public String updateMemberForm(@RequestParam("memberId") String memberId, Model model) {
		
		Member findmember = memberService.findByMemberId(memberId);
		
		model.addAttribute("member", findmember);
		
		
		return "admin/updateMember";
	}
	
	@PostMapping("/updateMember")
	public String updateMember(@ModelAttribute("member") Member member) {
		
		memberService.updateAllMember(member.getMemberId(), member);
		
		return "admin/updateMember";
	}
	
	@GetMapping("/deleteMember")
	public String deleteMember(@RequestParam("memberId") String memberId, Model model, HttpServletRequest request) {
		
		Member member = memberService.findByMemberId(memberId);
		Locale locale = localeResolver.resolveLocale(request);
		memberService.deleteMember(member);

		model.addAttribute("msg", messageSource.getMessage("info.exit.success", null, locale));
		model.addAttribute("url", "/");
		
		return "alert";
	}
	
	@GetMapping("/insertMember")
	public String insertMemberForm(@ModelAttribute("AdminMemberValidation") AdminMemberValidation member) {
		return "admin/insertMember";
	}
	
	@PostMapping("/insertMember")
	public String insertMember(@Validated @ModelAttribute("AdminMemberValidation") AdminMemberValidation member,BindingResult bindingResult,Model model) {
			
		if(bindingResult.hasErrors()) {
			return "admin/insertMember";
		}
		
		
		List<Member> findmemberEmail=memberService.findByMemberEmail(member.getMemberEmail());
		List<Member> findmemberNickname=memberService.findByMemberNickname(member.getMemberNickname());
		Member findmemberId=memberService.findByMemberId(member.getMemberId());
		List<Member> findmemberPhone = memberService.findByMemberPhoneNum(member.getMemberPhoneNum());
		
		// 아이디 중복검사
		if(findmemberId != null && findmemberId.getMemberId().equals(member.getMemberId())) {
			bindingResult.rejectValue("memberId", null, "존재하는 아이디입니다."); 
			return "member/signup";
		}

		// 닉네임 중복검사

		if(!findmemberNickname.isEmpty()) {
			bindingResult.rejectValue("memberNickname",null, "존재하는 닉네임입니다."); 
			return "member/signup";
		}
		
		// 이메일 중복검사
		if(!findmemberEmail.isEmpty()) {
			bindingResult.rejectValue("memberEmail",null, "존재하는 이메일입니다."); 
			return "member/signup";
		}
		
		// 전화번호 중복검사
		if(!findmemberPhone.isEmpty()) {
			bindingResult.rejectValue("memberPhoneNum", null, "존재하는 전화번호입니다"); 
			return "member/signup";
		}
		
		Member Member = new Member();
		Member.setMemberEmail(member.getMemberEmail());
		Member.setMemberId(member.getMemberId());
		Member.setMemberPw(member.getMemberPw());
		Member.setMemberName(member.getMemberName());
		Member.setMemberNickname(member.getMemberNickname());
		Member.setMemberPhoneNum(member.getMemberPhoneNum());
		Member.setMemberSocial(member.getMemberSocial());
		Member.setMemberRole(member.getMemberRole());
			
		Member save_member=memberService.insertMember(Member);
			
		model.addAttribute("msg", "성공적으로 회원가입이 되었습니다.");
        model.addAttribute("url", "/admin/insertMember");	
        return "alert";	
               
	}
	
	/*
	 * 게시판(Board) 관리 =========================================================================
	 * */
	
	@GetMapping("/boardList")
	public String getBoardList(Model model, Board board,
	       @RequestParam(name = "curPage", defaultValue = "0") int curPage,
	       @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
	       @RequestParam(name = "searchType", defaultValue = "boardWriter") String searchType,
	       @RequestParam(name = "searchWord", defaultValue = "") String searchWord) {
	   
	    curPage = Math.max(curPage, 0);  // Ensure curPage is not negative
	    Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("boardId").descending());
	    Page<Board> pagedResult = boardService.getBoardList(pageable, searchType, searchWord);
	    List<Notice> noticeList = noticeService.noticeList();
	    
	    // Sort noticeList in descending order based on noticeDate
	    noticeList = noticeList.stream()
	                   .sorted(Comparator.comparing(Notice::getNoticeDate).reversed())
	                   .collect(Collectors.toList());
	
	    // Limit the noticeList to the first 2 items
	    if (noticeList.size() > 2) {
	        noticeList = noticeList.subList(0, 3);
	    }
	    
	    int totalRowCount  = (int)pagedResult.getNumberOfElements();
	    int totalPageCount = pagedResult.getTotalPages();
	    int pageSize       = pagingInfo.getPageSize();
	    int startPage      = (curPage / pageSize) * pageSize + 1;
	    int endPage        = startPage + pageSize - 1;
	    endPage = endPage > totalPageCount ? (totalPageCount > 0 ? totalPageCount : 1) : endPage;
	    
	
	    pagingInfo.setCurPage(curPage);
	    pagingInfo.setTotalRowCount(totalRowCount);
	    pagingInfo.setTotalPageCount(totalPageCount);
	    pagingInfo.setStartPage(startPage);
	    pagingInfo.setEndPage(endPage);
	    pagingInfo.setSearchType(searchType);
	    pagingInfo.setSearchWord(searchWord); 
	    pagingInfo.setRowSizePerPage(rowSizePerPage);
	    
	    model.addAttribute("pagingInfo", pagingInfo);
	    model.addAttribute("pagedResult", pagedResult);
	    model.addAttribute("pageable", pageable);
	    model.addAttribute("cp", curPage);
	    model.addAttribute("sp", startPage);
	    model.addAttribute("ep", endPage);
	    model.addAttribute("ps", pageSize);
	    model.addAttribute("rp", rowSizePerPage);
	    model.addAttribute("tp", totalPageCount);
	    model.addAttribute("st", searchType);
	    model.addAttribute("sw", searchWord);
	    model.addAttribute("boardList", pagedResult.getContent()); // Add this line
	    model.addAttribute("noticeList", noticeList);
	
	    return "admin/boardList";
	}
	
	
	

}
