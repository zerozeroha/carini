package com.car.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.time.LocalDate;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.car.validation.BoardWriteFormValidation;
import com.car.validation.InquiryWriteValidation;
import com.car.validation.Update_InfoFormValidation;
import com.car.validation.Update_NicknameFormValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.car.service.BoardService;
import com.car.service.BookMarkService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

	@Value("${pw-role.password-rejex}")
	private String passwordRegex;

	public PagingInfo pagingInfo = new PagingInfo();
	
	private final BookMarkService bookMarkService;
	
	private final MemberService memberService;
	
	private final BoardService boardService;

	private final MessageSource messageSource;
	
	private final LocaleResolver localeResolver;


	
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
	public String mypageForm(HttpSession session, Model model, HttpServletRequest request,
			@ModelAttribute("InquiryWriteValidation") InquiryWriteValidation InquiryValidation) {

		
		Member user =null;
		
		if(session != null) {
			user = (Member) session.getAttribute("user");
		} 

		Member findmember = memberService.findMember(user.getMemberId());
		findmember.setMemberPw(user.getMemberNickname());
		findmember.setMemberPw("*****");
		findmember.setMemberPhoneNum("***-****-****");
		findmember.setMemberEmail("****@****.***");
		findmember.setMemberNickname(findmember.getMemberNickname());
		findmember.setMemberSocialNickname(findmember.getMemberSocialNickname());
		session.setAttribute("originalUrl", request.getRequestURI());
		System.out.println(findmember);
		model.addAttribute("hiddenuser", findmember);
		model.addAttribute("inquiry", new Inquiry());
		return "mypage/mypageview.html";
	}

	/*
	 * 패스워드확인후 소셜 | 회원 판단
	 */
	@GetMapping("/myinfo")
	public ResponseEntity<Map<String, Object>> myinfo(@RequestParam("user_password") String memberPw,
			@ModelAttribute("member") Member members, HttpServletRequest request, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		Member user = (Member) session.getAttribute("user");
		Member member = memberService.findByMemberId(user.getMemberId());
		Locale locale = localeResolver.resolveLocale(request);
		
		boolean passwordCheck = memberService.passwordCheck(member,memberPw);
		
		if (member != null && passwordCheck) {
			response.put("success", true);
			response.put("message", messageSource.getMessage("info.success", null, locale));
			response.put("redirectUrl",
					member.getMemberSocial().equals("kakao") || member.getMemberSocial().equals("naver") || member.getMemberSocial().equals("google")
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
	public String myinfo_edit(HttpSession session,
			@ModelAttribute("Update_InfoFormValidation") Update_InfoFormValidation members, Model model) {
		Member user = (Member) session.getAttribute("user");
		Member finduser = memberService.findByMemberId(user.getMemberId());
		members.setMemberEmail(finduser.getMemberEmail());
		members.setMemberName(finduser.getMemberName());
		members.setMemberPhoneNum(finduser.getMemberPhoneNum());
		members.setMemberPw(finduser.getMemberPw());
		model.addAttribute("Update_InfoFormValidation", finduser);
		session.setAttribute("showuser", finduser);

		return "mypage/myinfo_edit.html";
	}

	@GetMapping("/myinfo_social_edit")
	public String myinfo_social_edit(@ModelAttribute("Update_InfoFormValidation") Update_InfoFormValidation members,
			HttpSession session) {
		Member user = (Member) session.getAttribute("user");
		Member finduser = memberService.findByMemberId(user.getMemberId());
		session.setAttribute("showuser", finduser);
		return "mypage/myinfo_edit.html";
	}

	/*
	 * 회원 정보 수정(회원)
	 */
	/*
	 * 닉네임 수정
	 */
	@PostMapping("/myinfo/updatenickname")
	public String myInfoNicknameUpdate(@ModelAttribute("member") Member members, BindingResult bindingResult,
			@RequestParam("memberNickname") String memberNickname, Model model, HttpSession session,
			HttpServletRequest request) {

		Member user = (Member) session.getAttribute("user");
		System.out.println(memberNickname);
		Member member = memberService.findByMemberId(user.getMemberId());
		
		List<Member> memberList = memberService.findAllMember();
		
		Locale locale = localeResolver.resolveLocale(request);
		
		for (Member memberOne : memberList) {
			if (memberNickname.equals("")) {
				model.addAttribute("msg", messageSource.getMessage("info.Nickinput", null, locale));
				model.addAttribute("url", "/mypage/myinfo_edit");
				return "alert";
			}
			System.out.println("memberNickname3"+memberNickname);
			if (member != null && (memberOne.getMemberNickname().equals(memberNickname)
					|| memberOne.getMemberSocialNickname().equals(memberNickname) )) {
				model.addAttribute("msg", messageSource.getMessage("info.Nickinput.failure", null, locale));
				model.addAttribute("url", "/mypage/myinfo_edit");
				return "alert";

			}
		}
		
		memberService.updateMember(member, memberNickname);
		boardService.updateBoardWriter(member, memberNickname);
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
	public String myInfoUpdateAll(
			@Validated @ModelAttribute("Update_InfoFormValidation") Update_InfoFormValidation members,
			BindingResult bindingResult, Model model, HttpSession session, HttpServletRequest request) {

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			// 필드별로 발생한 모든 오류 메시지를 맵에 담음
			bindingResult.getFieldErrors().forEach(error -> {
				String fieldName = error.getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
			System.out.println(errors);
			return "mypage/myinfo_edit.html";
		}

		Member user = (Member) session.getAttribute("user");
		List<Member> memberList = memberService.findAllMember();
		Locale locale = localeResolver.resolveLocale(request);
		Member member = memberService.findByMemberId(user.getMemberId());
		if (member != null) {
			String currentMemberEmail = member.getMemberEmail();
			for (Member memberOne : memberList) {

				if (!memberOne.getMemberEmail().equals(currentMemberEmail)
						&& memberOne.getMemberEmail().equals(members.getMemberEmail())) {

					model.addAttribute("msg", messageSource.getMessage("info.emailcheck.failure", null, locale));
					model.addAttribute("url", "/mypage/form");
					return "alert";
				}
			}
			member.setMemberPw(members.getMemberPw());
			member.setMemberName(members.getMemberName());

			member.setMemberEmail(members.getMemberEmail());
			member.setMemberPhoneNum(members.getMemberPhoneNum());

			memberService.updateAllMember(user.getMemberId(), member);

			model.addAttribute("msg", messageSource.getMessage("info.update.success", null, locale));
			model.addAttribute("url", "/mypage/form");
			return "alert";
		} else {
			model.addAttribute("msg", messageSource.getMessage("info.update.fail", null, locale));
			model.addAttribute("url", "/mypage/form");
			return "alert";
		}
	}

	/*
	 * 회원탈퇴
	 */
	@PostMapping("/myinfo/delete")
	public String myInfoDeletePwCheck( Model model,
			HttpServletRequest request,HttpSession session) {
		Member member=(Member) session.getAttribute("user");
		System.out.println(member.getMemberId());
		Member findmember = memberService.findByMemberId(member.getMemberId());
		Locale locale = localeResolver.resolveLocale(request);
		memberService.deleteMember(findmember);
		
		model.addAttribute("msg", messageSource.getMessage("info.exit.success", null, locale));
		model.addAttribute("url", "/user_logout");
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
	public String myPagebookmarkAdd(@PathVariable("carId") String carId, Model model, Bookmark bookmark,
			HttpServletRequest request, HttpSession session) {
		
		Locale locale = localeResolver.resolveLocale(request);

		Member user = (Member) session.getAttribute("user");

		bookmark.setCarId(Integer.parseInt(carId));
		bookmark.setMemberId(user.getMemberId());

		bookMarkService.insertMember(bookmark, user);

		model.addAttribute("msg", messageSource.getMessage("bookmark.add", null, locale));
		model.addAttribute("url", request.getHeader("Referer"));
		return "alert";
	}

	@GetMapping("/bookmark/{carId}")
	public String myPagebookmarkAddGet(@PathVariable("carId") String carId, Model model, Bookmark bookmark,
			HttpServletRequest request, HttpSession session) {

		Locale locale = localeResolver.resolveLocale(request);
		if(((Member) session.getAttribute("user") == null)) {
			
		}
		Member user = (Member) session.getAttribute("user");
		bookmark.setCarId(Integer.parseInt(carId));
		bookmark.setMemberId(user.getMemberId());

		bookMarkService.insertMember(bookmark, user);

		return "redirect:/model/getModelList";
	}

	/*
	 * bookmark 삭제
	 */
	@PostMapping("/bookmark/delete/{carId}")
	public String myPagebookmarkdelete(@PathVariable("carId") String carId, Model model, HttpServletRequest request,
			HttpSession session) {

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
	 * 게시판 작성
	 */
	@GetMapping("/insertMyBoard")
	public String insertBoardForm(Board board, Member member,
			@ModelAttribute("BoardWriteFormValidation") BoardWriteFormValidation boardValidation, Model model) {

		LocalDate currentDate = LocalDate.now();

		model.addAttribute("date", currentDate);

		return "mypage/insertMyBoard";
	}

	/*
	 * 게시판 작성
	 */
	@PostMapping("/insertMyBoard")
	public String insertBoard(Board board,
			@Validated @ModelAttribute("BoardWriteFormValidation") BoardWriteFormValidation boardValidation,
			BindingResult bindingResult, Model model)

			throws IOException {
		LocalDate currentDate = LocalDate.now();
		model.addAttribute("date", currentDate);
		if (bindingResult.hasErrors()) {
			return "mypage/insertMyBoard";
		}

		// 파일업로드

		MultipartFile uploadFile = board.getUploadFile();
		if (!uploadFile.isEmpty()) {
			String fileName = uploadFile.getOriginalFilename();

			uploadFile.transferTo(new File(uploadFolder + fileName));
			board.setBoardFilename(fileName);
		}

		boardService.insertBoard(board);
		model.addAttribute("msg", "게시글이 작성되었습니다!");
		model.addAttribute("url", "/mypage/myBoard");
		return "alert";
	}

	/*
	 * 내 게시물 상세 조회
	 */
	@GetMapping("/myBoard/getBoard")
	public String myPagemyboard(Board board, Model model, HttpSession session) {

		Member user = (Member) session.getAttribute("user");

		model.addAttribute("board", boardService.getBoard(board, user.getMemberId())); // 여기서 조회수 증가

		return "mypage/getMyBoard";
	}

	/*
	 * 내 게시물 수정하기 폼
	 */
	@GetMapping("/updateMyBoard")
	public String updateMyBoard(@RequestParam("boardId") Long boardId, Model model, HttpSession session,
			@ModelAttribute("BoardUpdateFormValidation") BoardUpdateFormValidation boardValidation,
			BindingResult bindingResult) {
		Member user = (Member) session.getAttribute("user");

		Board board = boardService.getBoardById(boardId);
		if (board == null) {
			model.addAttribute("msg", "게시글을 찾을 수 없습니다.");
			model.addAttribute("url", "/mypage/myBoard");
			return "alert";
		}

		if (board.getMemberId().equals(user.getMemberId())) {
			boardValidation.setBoardTitle(board.getBoardTitle());
			boardValidation.setBoardContent(board.getBoardContent());

			model.addAttribute("BoardUpdateFormValidation", boardValidation);
			model.addAttribute("board", board);
			return "mypage/updateMyBoard"; // 게시글 수정 페이지
		}

		model.addAttribute("msg", "작성자만 수정이 가능합니다!.");
		model.addAttribute("url", "/mypage/myBoard");
		return "alert";
	}

	/*
	 * 나의 게시물 수정하기
	 */
	@PostMapping("/updateBoard")
	public String updateBoard(Board board,
			@Validated @ModelAttribute("BoardUpdateFormValidation") BoardUpdateFormValidation boardValidation,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			board = boardService.getBoardById(board.getBoardId());
			model.addAttribute("board", board);
			return "mypage/updateMyBoard";
		}

		// 파일재업로드
		MultipartFile uploadFile = board.getUploadFile();
		if (uploadFile != null && !uploadFile.isEmpty()) {
			String fileName = uploadFile.getOriginalFilename();
			Path filePath = Paths.get(uploadFolder + fileName);
			try {
				Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				board.setBoardFilename(fileName);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
			}
		}
		boardService.updateBoard(board);
		model.addAttribute("msg", "게시글이 수정되었습니다!");
		model.addAttribute("url", "/mypage/myBoard/getBoard?boardId=" + board.getBoardId());
		return "alert";
	}

	/*
	 * 나의게시글 삭제
	 */
	@PostMapping("/myBoard/deleteBoard")
	public String myPagemydeleteBoard(@RequestParam("selectedBoardsData") String selectedBoards,
			@RequestParam(name = "curPage", defaultValue = "0") int curPage,
			@RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
			@RequestParam(name = "searchType", defaultValue = "boardWriter") String searchType,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, HttpSession session, Model model) {

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
		model.addAttribute("url", "/mypage/myBoard?curPage=" + curPage + "&rowSizePerPage=" + rowSizePerPage
				+ "&searchType=" + searchType + "&searchWord=" + searchWord);
		return "alert";
	}

	/*
	 * 나의 게시글 삭제
	 */
	@GetMapping("/myboard/getMyboard/deleteBoard")
	public String deleteBoard(Board board, HttpSession session, Model model) {
		Board findboard = boardService.getBoardById(board.getBoardId());

		if (findboard == null) {
			model.addAttribute("msg", "해당 게시물이 존재하지않습니다.");
			model.addAttribute("url", "/mypage/myBoard");
			return "alert";
		}

		boardService.deleteBoard(findboard);
		model.addAttribute("msg", "해당 게시물을 삭제하였숩니다.");
		model.addAttribute("url", "/mypage/myBoard");
		return "alert";
	}

}
