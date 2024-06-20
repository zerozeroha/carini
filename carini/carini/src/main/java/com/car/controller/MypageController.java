package com.car.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import com.car.dto.Board;
import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.Inquiry;
import com.car.dto.Member;
import com.car.dto.PagingInfo;
import com.car.service.MemberService;
import com.car.validation.BoardUpdateFormValidation;
import com.car.validation.InquiryWriteValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.car.service.BoardService;
import com.car.service.BookMarkService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/mypage")
@SessionAttributes({ "user", "pagingInfo" })
public class MypageController {

	@Value("${pw-role.password-rejex}")
	private String passwordRegex;

	public PagingInfo pagingInfo = new PagingInfo();
	@Autowired
	private BookMarkService bookMarkService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private BoardService boardService;

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private LocaleResolver localeResolver;

	@ModelAttribute("member")
	public Member setMember() {
		return new Member(); // 기본 Member 객체를 세션에 저장
	}

	@Value("${path.upload}")
	public String uploadFolder;

	@GetMapping("/no_login")
	public String mypageNo_login(Model model) {

		return "member/login.html";
	}

	/*
	 * 회원정보 수정(나의 정보)
	 */
	@GetMapping("/form")
	public String mypageForm(HttpSession session,Model model,HttpServletRequest request,@ModelAttribute("InquiryWriteValidation") InquiryWriteValidation InquiryValidation) {

		Member user = (Member) session.getAttribute("user");
		Member findmember = memberService.findMember(user.getMemberId());
		findmember.setMemberPw("*****");
		findmember.setMemberPhoneNum("***-****-****");
		findmember.setMemberEmail("****@****.***");
	    session.setAttribute("originalUrl", request.getRequestURI());
		session.setAttribute("user", findmember);
		model.addAttribute("inquiry", new Inquiry());
		return "mypage/mypageview.html";
	}

	/*
	 * 패스워드확인후 소셜 | 회원 판단
	 */
	@GetMapping("/myinfo")
	public ResponseEntity<Map<String, Object>> myinfo(@RequestParam("user_password") String memberPw,
			@ModelAttribute("member") Member members, HttpServletRequest request) {
		
		Map<String, Object> response = new HashMap<>();
		Member member = memberService.findByMemberId(members.getMemberId());
		Locale locale = localeResolver.resolveLocale(request);
		
		if (member != null && member.getMemberPw().equals(memberPw)) {
			response.put("success", true);
			response.put("message", messageSource.getMessage("info.success", null, locale));
			response.put("redirectUrl",
					member.getMemberSocial().equals("kakao") || member.getMemberSocial().equals("naver")
							? "/mypage/myinfo_social_edit"
							: "/mypage/myinfo_edit");
		} else {
			response.put("success", false);
			response.put("message", messageSource.getMessage("info.failure", null, locale));
		}

		return ResponseEntity.ok(response);
	}

	/*
	 * 패스워드 팝업창
	 */
	@GetMapping("/password")
	public String password(@ModelAttribute("member") Member members, Model model) {

		return "/password.html";
	}

	@GetMapping("/myinfo_edit")
	public String myinfo_edit(@ModelAttribute("member") Member members, HttpSession session) {

		
		Member findmember = memberService.findMember(members.getMemberId());
		session.setAttribute("user", findmember);
		return "mypage/myinfo_edit.html";
	}

	@GetMapping("/myinfo_social_edit")
	public String myinfo_social_edit(@ModelAttribute("member") Member members, HttpSession session) {

		Member findMember = (Member) session.getAttribute("user");
		if (findMember.getMemberSocial() == "naver") {
			findMember = memberService.findByMemberId(findMember.getMemberId().replace("\"", ""));
		} else {
			findMember = memberService.findByMemberId(findMember.getMemberId().replace("\"", ""));
		}

		session.setAttribute("user", findMember);

		return "mypage/myinfo_edit.html";
	}

	/*
	 * 회원 정보 수정(회원)
	 */
	/*
	 * 닉네임 수정
	 */
	@PostMapping("/myinfo/updatenickname")
	public String myInfoNicknameUpdate(@ModelAttribute("member") Member members,
			@RequestParam("memberNickname") String memberNickname, Model model, HttpSession session,
			HttpServletRequest request) {
		Member member = memberService.findByMemberId(members.getMemberId());
		List<Member> memberList = memberService.findAllMember();
		Locale locale = localeResolver.resolveLocale(request);

		for (Member memberOne : memberList) {
			if (memberNickname == null) {
				model.addAttribute("msg", messageSource.getMessage("info.Nickinput", null, locale));
				model.addAttribute("url", "/mypage/myinfo_edit");
				return "alert";
			}
			if (member != null && (memberOne.getMemberNickname().equals(memberNickname)
					|| memberOne.getMemberSocialNickname() == memberNickname)) {
				model.addAttribute("msg", messageSource.getMessage("info.Nickinput.failure", null, locale));
				model.addAttribute("url", "/mypage/myinfo_edit");
				return "alert";

			}
		}

		memberService.updateMember(member, memberNickname);
		boardService.updateBoardWriter(member,memberNickname);
		Member savemember = memberService.findByMemberId(members.getMemberId());
		
		session.setAttribute("user", savemember);
		model.addAttribute("msg", messageSource.getMessage("info.Nickinput.success", null, locale));
		model.addAttribute("url", "/mypage/form");
		return "alert";

	}

	/*
	 * 닉네임 수정(소셜)
	 */
	@PostMapping("/myinfo/updatenicknameSocial")
	public String myInfoNicknameUpdateSocial(@ModelAttribute("member") Member members,
			@RequestParam("memberSocialNickname") String memberSocialNickname, Model model, HttpSession session,
			HttpServletRequest request) {
		Member findMember = (Member) session.getAttribute("user");
		Member member = memberService.findByMemberId(findMember.getMemberId().replace("\"", ""));
		Locale locale = localeResolver.resolveLocale(request);
		List<Member> memberList = memberService.findAllMember();

		for (Member memberOne : memberList) {
			if (memberSocialNickname == null) {
				model.addAttribute("msg", messageSource.getMessage("info.Nickinput", null, locale));
				model.addAttribute("url", "/mypage/myinfo_edit");
				return "alert";
			}
			System.out.println(memberOne.getMemberNickname().equals(memberSocialNickname));
			if (member != null && (memberOne.getMemberNickname().equals(memberSocialNickname)
					|| (memberOne.getMemberSocialNickname() != null
							&& memberOne.getMemberSocialNickname().equals(memberSocialNickname)))) {
				model.addAttribute("msg", messageSource.getMessage("info.Nickinput.failure", null, locale));
				model.addAttribute("url", "/mypage/form");
				return "alert";
			}
		}
		memberService.updatememberSocialNickname(member, memberSocialNickname);
		model.addAttribute("msg", messageSource.getMessage("info.Nickinput.success", null, locale));
		model.addAttribute("url", "/mypage/form");
		return "alert";

	}

	/*
	 * 회원정보 모두 수정
	 */
	@PostMapping("/myinfo/updateAll")
	public String myInfoUpdateAll(@ModelAttribute("member") Member members, @RequestParam("memberPw") String memberPw,
			@RequestParam("memberName") String memberName, @RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberPhoneNum") String memberPhoneNum, Model model, HttpServletRequest request) {

		final Pattern PASSWORD_PATTERN = Pattern.compile(passwordRegex);
		List<Member> memberList = memberService.findAllMember();
		Locale locale = localeResolver.resolveLocale(request);
		Member member = memberService.findByMemberId(members.getMemberId());
		if (member != null) {
			if (!PASSWORD_PATTERN.matcher(memberPw).matches()) {

				model.addAttribute("msg", messageSource.getMessage("info.pwcheck.failure", null, locale));
				model.addAttribute("url", "/mypage/myinfo/" + members.getMemberId());
				return "alert";
			}

			String currentMemberEmail = member.getMemberEmail();
			for (Member memberOne : memberList) {

				if (!memberOne.getMemberEmail().equals(currentMemberEmail)
						&& memberOne.getMemberEmail().equals(memberEmail)) {

					model.addAttribute("msg", messageSource.getMessage("info.emailcheck.failure", null, locale));
					model.addAttribute("url", "/mypage/myinfo/");
					return "alert";
				}
			}
			member.setMemberPw(memberPw);
			member.setMemberName(memberName);

			member.setMemberEmail(memberEmail);
			member.setMemberPhoneNum(memberPhoneNum);

			memberService.updateAllMember(members.getMemberId(), member);

			model.addAttribute("msg", messageSource.getMessage("info.update.success", null, locale));
			model.addAttribute("url", "/mypage/form");
			return "alert";
		} else {
			return "redirect:/member_login";
		}
	}

	/*
	 * 회원탈퇴
	 */
	@PostMapping("/myinfo/delete")
	public String myInfoDeletePwCheck(@ModelAttribute("member") Member members, Model model,
			HttpServletRequest request) {
		Member member = memberService.findByMemberId(members.getMemberId());
		Locale locale = localeResolver.resolveLocale(request);
		memberService.deleteMember(member);

		model.addAttribute("msg", messageSource.getMessage("info.exit.success", null, locale));
		model.addAttribute("url", "/");
		return "alert";
	}

	/*
	 * 회원 정보 수정(소셜)
	 */
	@PostMapping("/myinfo/updateAll_social")
	public String myInfoupdateAllSocial(@ModelAttribute("member") Member members,
			@RequestParam("memberName") String memberName, @RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberPhoneNum") String memberPhoneNum, Model model, HttpServletRequest request) {

		List<Member> memberList = memberService.findAllMember();
		Locale locale = localeResolver.resolveLocale(request);
		Member member = memberService.findByMemberId(members.getMemberId());
		if (member != null) {

			for (Member memberOne : memberList) {

				if (!memberOne.getMemberEmail().equals(memberEmail)) {
					model.addAttribute("msg", messageSource.getMessage("info.emailcheck.failure", null, locale));
					model.addAttribute("url", "/mypage/myinfo/");
					return "alert";
				}
			}
			member.setMemberName(memberName);

			member.setMemberEmail(memberEmail);
			member.setMemberPhoneNum(memberPhoneNum);

			memberService.updateAllMember(members.getMemberId(), member);
			model.addAttribute("msg", messageSource.getMessage("info.update.success", null, locale));
			model.addAttribute("url", "/mypage/myinfo/");
			return "alert";
		} else {
			return "redirect:/mypage/myinfo/";
		}
	}

	/*
	 * =================================== 즐겨찾기
	 */
	@GetMapping("/bookmark")
	public String myPagebookmarkList(Model model, HttpServletRequest request, HttpSession session) {
		Locale locale = localeResolver.resolveLocale(request);
		
		Member user = (Member) session.getAttribute("user");
		List<Bookmark> bookmarkCarID = bookMarkService.findAllBookmarkCar(user.getMemberId());

		System.out.println(bookmarkCarID);
		List<Car> bookmarkCarList = bookMarkService.findAllCar(bookmarkCarID);

		model.addAttribute("BookmarkCarList", bookmarkCarList);
		return "mypage/bookmark.html";
	}
	
	/*
	 * 즐겨찾기 추가
	 */
	@PostMapping("/bookmark/{carId}")
	public String myPagebookmarkAdd(@PathVariable("carId") String carId, Model model, Bookmark bookmark, HttpServletRequest request, HttpSession session) {

		
		Locale locale = localeResolver.resolveLocale(request);

		Member user = (Member) session.getAttribute("user");

		bookmark.setCarId(Integer.parseInt(carId));
		bookmark.setMemberId(user.getMemberId());
		
		Bookmark save_bookmark = bookMarkService.insertMember(bookmark);
		
		model.addAttribute("msg", messageSource.getMessage("bookmark.add", null, locale));
		model.addAttribute("url", request.getHeader("Referer"));
	    return "alert";
	}

	/*
	 * bookmark 삭제
	 */
	@PostMapping("/bookmark/delete/{carId}")
	public String myPagebookmarkdelete(@PathVariable("carId") String carId,	Model model, HttpServletRequest request, HttpSession session) {

		Locale locale = localeResolver.resolveLocale(request);
		Member user = (Member) session.getAttribute("user");
		
		bookMarkService.findBookmarkByCarDelete(Integer.parseInt(carId), user.getMemberId());
		model.addAttribute("msg", messageSource.getMessage("bookmark.delete", null, locale));
		model.addAttribute("url", request.getHeader("Referer"));
		return "alert";
	}

	/*
	 * =================================== 나의 게시물
	 */
	@RequestMapping("/myBoard")
	public String getBoardList(Model model, Board board,
			@RequestParam(name = "curPage", defaultValue = "0") int curPage,
			@RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
			@RequestParam(name = "searchType", defaultValue = "boardWriter") String searchType,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, HttpSession session) {

		Member findMember = (Member) session.getAttribute("user");

		searchWord = findMember.getMemberNickname();
		System.out.println("====================");
		System.out.println(searchWord);
		curPage = Math.max(curPage, 0); // Ensure curPage is not negative
		Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("boardId").descending());
		Page<Board> pagedResult = boardService.getBoardList(pageable, searchType, searchWord);

		int totalRowCount = (int) pagedResult.getNumberOfElements();
		int totalPageCount = pagedResult.getTotalPages();
		int pageSize = pagingInfo.getPageSize();
		int startPage = (curPage / pageSize) * pageSize + 1;
		int endPage = startPage + pageSize - 1;
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

		return "mypage/myboard.html";
	}

	/*
	 * 내 게시물 상세 조회
	 */
	@GetMapping("/myBoard/getBoard")
	public String myPagemyboard(Board board, Model model,HttpSession session) {
			
		Member user = (Member) session.getAttribute("user");
	       
	    model.addAttribute("board", boardService.getBoard(board, user.getMemberId())); // 여기서 조회수 증가
	      
	    return "mypage/getMyBoard";
	}
	/*
	 * 내 게시물 수정하기 폼
	 * */
	 @GetMapping("/updateMyBoard")
	   public String updateMyBoard(@RequestParam("boardId") Long boardId, Model model, HttpSession session,
			   @ModelAttribute("BoardUpdateFormValidation") BoardUpdateFormValidation boardValidation,
			   BindingResult bindingResult) {
		       Member user = (Member) session.getAttribute("user");

		       Board board = boardService.getBoardById(boardId);
		       if (board == null) {
		           model.addAttribute("msg", "게시글을 찾을 수 없습니다.");
		           model.addAttribute("url", "/board/getBoardList");
		           return "alert";
		       }
		       
		       if (board.getMemberId().equals(user.getMemberId())) {
		    	   boardValidation.setBoardTitle(board.getBoardTitle());
		           boardValidation.setBoardContent(board.getBoardContent());
		           
		           model.addAttribute("BoardUpdateFormValidation", boardValidation);
		           model.addAttribute("board", board);
		           return "mypage/updateMyBoard";  // 게시글 수정 페이지
		       }
		       
		       model.addAttribute("msg", "작성자만 수정이 가능합니다!.");
		       model.addAttribute("url", "/mypage/myBoard");
		       return "alert";
	   }
	 /*
	  * 나의 게시물 수정하기
	  * */
	 @PostMapping("/updateBoard")
	   public String updateBoard(Board board, Model model,
			   @Validated @ModelAttribute("BoardUpdateFormValidation") BoardUpdateFormValidation boardValidation ,
			   BindingResult bindingResult)  {
	     
		 if (bindingResult.hasErrors()) {

		       return "mypage/updateMyBoard";
		    }
	      
	      // 파일재업로드
	      MultipartFile uploadFile = board.getUploadFile();
	      if(uploadFile != null && !uploadFile.isEmpty()) {
	         String fileName = uploadFile.getOriginalFilename();
	         Path filePath = Paths.get(uploadFolder + fileName);
	         try {
	            Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	               board.setBoardFilename(fileName);
	         } catch (IOException e) {
	            e.printStackTrace();
	         }         
	      }
	      
	      boardService.updateBoard(board);
	        model.addAttribute("msg", "게시글이 수정되었습니다!");
	        model.addAttribute("url", "/mypage/myBoard/getBoard?boardId=" + board.getBoardId());
	        return "alert";
	   }
	 
	 /*
	  * 나의게시글 삭제
	  * */
	@PostMapping("/myBoard/deleteBoard")
	public String myPagemydeleteBoard(@RequestParam("selectedBoardsData") String selectedBoards,
			@RequestParam(name = "curPage", defaultValue = "0") int curPage,
			@RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
			@RequestParam(name = "searchType", defaultValue = "boardWriter") String searchType,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord,HttpSession session,Model model) {
		
		Member user = (Member) session.getAttribute("user");
        if (user == null || user.getMemberId() == null) {
            // 사용자 정보가 없는 경우 로그인 페이지로 리다이렉트
            return "redirect:/member_login";
        }
		// ObjectMapper 객체 생성
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        // JSON 문자열을 Board 배열로 역직렬화
	        Board[] selectedBoardsArray = objectMapper.readValue(selectedBoards, Board[].class);

	        // 선택된 게시물을 순회하면서 삭제 작업 수행
	        for (Board board : selectedBoardsArray) {
	            boardService.deleteBoard(board);
	          
	        }


	        // 세션에서 사용자 정보 가져오기
	        

	    } catch (JsonProcessingException e) {
	        // JSON 처리 중 오류 발생 시 예외 처리
	        e.printStackTrace();
	        // 오류 페이지로 리다이렉트 또는 오류 메시지 반환 등의 작업 수행
	    }
	    // 삭제 작업 완료 후 게시물 목록 페이지로 이동
	    
	    model.addAttribute("msg", "게시글이 삭제되었습니다!");
        model.addAttribute("url", "/mypage/myBoard?curPage=" + curPage + "&rowSizePerPage=" + rowSizePerPage + "&searchType=" + searchType + "&searchWord=" + searchWord);
        return "alert";
	}

}
