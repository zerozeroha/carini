package com.car.controller;

import java.text.NumberFormat;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.LocaleResolver;

import com.car.dto.Board;
import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.dto.PagingInfo;
import com.car.service.MemberService;
import com.car.service.BoardService;
import com.car.service.BookMarkService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/mypage")
@SessionAttributes({"member", "pagingInfo"})
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
   
   
    @GetMapping("/")
    public String backhome(HttpServletRequest request) {
       //세션을 삭제
        HttpSession session = request.getSession(false); 
         // session이 null이 아니라는건 기존에 세션이 존재했었다는 뜻이므로
         // 세션이 null이 아니라면 session.invalidate()로 세션 삭제해주기.
       if(session != null) {
          session.invalidate();
       }
         return "index.html";
    }
    
    @GetMapping("/no_login")
    public String mypageNo_login(Model model) {
       
        return "member/login.html";
    }
    
    /*
     * 회원정보 수정(나의 정보)
     */
    @GetMapping("/form")
    public String mypageForm(HttpSession session) {
    	
    	Member user = (Member) session.getAttribute("user");
    	Member findmember = memberService.findMember(user);
    	if(!findmember.getMemberSocial().equals("회원")) {
    		findmember.setMemberPw("*****");
    		findmember.setMemberPhoneNum("***-****-****");
    		findmember.setMemberEmail("****@****.***");
    	}
    	session.setAttribute("user", findmember);
        return "mypage/mypageview.html";
    }
    
    /*
     * 패스워드확인후 소셜 | 회원 판단
     * */
    @GetMapping("/myinfo")
    public ResponseEntity<Map<String, Object>> myinfo(@RequestParam("user_password") String memberPw, @ModelAttribute("member") Member members , HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Member member = memberService.findByMemberId(members.getMemberId());
        Locale locale = localeResolver.resolveLocale(request);
        if (member != null && member.getMemberPw().equals(memberPw)) {
            response.put("success", true);
            response.put("message", messageSource.getMessage("info.success", null, locale));
            response.put("redirectUrl", member.getMemberSocial().equals("kakao") || member.getMemberSocial().equals("naver")
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
     * */
    @GetMapping("/password")
    public String password(@ModelAttribute("member") Member members, Model model) {

        return "/password.html";
    }
    
    @GetMapping("/myinfo_edit")
    public String myinfo_edit(@ModelAttribute("member") Member members, HttpSession session) {
    	Member findmember = memberService.findMember(members);

	    session.setAttribute("user", findmember);
	    Member user = (Member) session.getAttribute("user");
        System.out.println(user);
        return "mypage/myinfo_edit.html";
    }
    
    @GetMapping("/myinfo_social_edit")
    public String myinfo_social_edit(@ModelAttribute("member") Member members, HttpSession session) {
    	
    	Member findMember = (Member) session.getAttribute("user");
    	if(findMember.getMemberSocial()=="naver") {
    		findMember = memberService.findByMemberId(findMember.getMemberId().replace("\"", ""));
    	}else {
    		findMember = memberService.findByMemberId(findMember.getMemberId().replace("\"", ""));
    	}
 
        session.setAttribute("user", findMember);
       
        return "mypage/myinfo_edit.html";
    }
    
   /*
    * 회원 정보 수정(회원) 
    * */
    /*
     * 닉네임 수정
     */
    @PostMapping("/myinfo/updatenickname")
    public String myInfoNicknameUpdate(@ModelAttribute("member") Member members,
                                       @RequestParam("memberNickname") String memberNickname,
                                       Model model, HttpSession session, HttpServletRequest request) {
        Member member = memberService.findByMemberId(members.getMemberId());
        List<Member> memberList = memberService.findAllMember();
        Locale locale = localeResolver.resolveLocale(request);
        
        for(Member memberOne : memberList) {
        	if(memberNickname==null) {
        		model.addAttribute("msg", messageSource.getMessage("info.Nickinput", null, locale) );
                model.addAttribute("url", "/mypage/myinfo_edit");
                return "alert"; 
        	}
           if (member != null && (memberOne.getMemberNickname().equals(memberNickname) || memberOne.getMemberSocialNickname() == memberNickname)) {
        	  model.addAttribute("msg", messageSource.getMessage("info.Nickinput.failure", null, locale) );
              model.addAttribute("url", "/mypage/myinfo_edit");
              return "alert"; 
              
            }
        }

        memberService.updateMember(member, memberNickname);
        Member savemember = memberService.findByMemberId(members.getMemberId());
        session.setAttribute("user", savemember);
        model.addAttribute("msg", messageSource.getMessage("info.Nickinput.success", null, locale) );
        model.addAttribute("url", "/mypage/form");
        return "alert";  
        
    }
   
    /*
     * 닉네임 수정(소셜)
     */
    @PostMapping("/myinfo/updatenicknameSocial")
    public String myInfoNicknameUpdateSocial(@ModelAttribute("member") Member members,
                                       @RequestParam("memberSocialNickname") String memberSocialNickname,
                                       Model model, HttpSession session, HttpServletRequest request) {
    	Member findMember = (Member) session.getAttribute("user");
        Member member = memberService.findByMemberId(findMember.getMemberId().replace("\"", ""));
        Locale locale = localeResolver.resolveLocale(request);
        List<Member> memberList = memberService.findAllMember();
        
        for(Member memberOne : memberList) {
            if(memberSocialNickname == null) {
            	model.addAttribute("msg", messageSource.getMessage("info.Nickinput", null, locale) );
                model.addAttribute("url", "/mypage/myinfo_edit");
                return "alert"; 
            }
            System.out.println(memberOne.getMemberNickname().equals(memberSocialNickname));
            if (member != null && (memberOne.getMemberNickname().equals(memberSocialNickname) || (memberOne.getMemberSocialNickname() != null && memberOne.getMemberSocialNickname().equals(memberSocialNickname)))) {
            	model.addAttribute("msg", messageSource.getMessage("info.Nickinput.failure", null, locale) );
                model.addAttribute("url", "/mypage/form");
                return "alert"; 
            }
        }
        memberService.updatememberSocialNickname(member, memberSocialNickname);
        model.addAttribute("msg", messageSource.getMessage("info.Nickinput.success", null, locale) );
        model.addAttribute("url", "/mypage/form");
        return "alert";  
        
    }
    
    /*
     * 회원정보 모두 수정
     */
    @PostMapping("/myinfo/updateAll")
    public String myInfoUpdateAll(@ModelAttribute("member") Member members,
                                  @RequestParam("memberPw") String memberPw,
                                  @RequestParam("memberName") String memberName,
                                  @RequestParam("memberEmail") String memberEmail,
                                  @RequestParam("memberPhoneNum") String memberPhoneNum,
                                  Model model, HttpServletRequest request) {
       
       final Pattern PASSWORD_PATTERN = Pattern.compile(passwordRegex);
       List<Member> memberList = memberService.findAllMember();
       Locale locale = localeResolver.resolveLocale(request);
        Member member = memberService.findByMemberId(members.getMemberId());
        if (member != null ) {
           if(!PASSWORD_PATTERN.matcher(memberPw).matches()) {
        	  
        	   model.addAttribute("msg", messageSource.getMessage("info.pwcheck.failure", null, locale) );
                model.addAttribute("url", "/mypage/myinfo/" +  members.getMemberId());
                return "alert";  
           }
           
           String currentMemberEmail = member.getMemberEmail();
           for(Member memberOne : memberList) {
              
              if (!memberOne.getMemberEmail().equals(currentMemberEmail) && memberOne.getMemberEmail().equals(memberEmail)) {
            	  
            	   model.addAttribute("msg", messageSource.getMessage("info.emailcheck.failure", null, locale) );
                   model.addAttribute("url", "/mypage/myinfo/");
                   return "alert"; 
                }
            }
            member.setMemberPw(memberPw);
            member.setMemberName(memberName);
            
            member.setMemberEmail(memberEmail);
            member.setMemberPhoneNum(memberPhoneNum);
            
            memberService.updateAllMember(members.getMemberId(), member);
           
            model.addAttribute("msg", messageSource.getMessage("info.update.success", null, locale) );
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
    public String myInfoDeletePwCheck(@ModelAttribute("member") Member members,
                                      Model model,HttpServletRequest request) {
        Member member = memberService.findByMemberId(members.getMemberId());
        Locale locale = localeResolver.resolveLocale(request);
        memberService.deleteMember(member);
        
        model.addAttribute("msg", messageSource.getMessage("info.exit.success", null, locale) );
        model.addAttribute("url", "/");
        return "alert"; 
    }
    
    /*
     * 회원 정보 수정(소셜) 
     * */
    @PostMapping("/myinfo/updateAll_social")
    public String myInfoupdateAllSocial(@ModelAttribute("member") Member members,
                                  @RequestParam("memberName") String memberName,
                                  @RequestParam("memberEmail") String memberEmail,
                                  @RequestParam("memberPhoneNum") String memberPhoneNum,
                                  Model model, HttpServletRequest request) {

       List<Member> memberList = memberService.findAllMember();
       Locale locale = localeResolver.resolveLocale(request);
        Member member = memberService.findByMemberId(members.getMemberId());
        if (member != null ) {
           
           for(Member memberOne : memberList) {
              
              if (!memberOne.getMemberEmail().equals(memberEmail)) {
            	  model.addAttribute("msg", messageSource.getMessage("info.emailcheck.failure", null, locale) );
                    model.addAttribute("url", "/mypage/myinfo/");
                    return "alert"; 
                }
            }
            member.setMemberName(memberName);
            
            member.setMemberEmail(memberEmail);
            member.setMemberPhoneNum(memberPhoneNum);
            
            memberService.updateAllMember(members.getMemberId(), member);
            model.addAttribute("msg", messageSource.getMessage("info.update.success", null, locale) );
            model.addAttribute("url", "/mypage/myinfo/");
            return "alert";  
        } else {
            return "redirect:/mypage/myinfo/";
        }
    }
    /*===================================
     * 즐겨찾기
     */
    @GetMapping("/bookmark")
    public String myPagebookmarkList(@ModelAttribute("member") Member members, Model model, HttpServletRequest request) {
    	Locale locale = localeResolver.resolveLocale(request);
        List<Bookmark> bookmarkCarID = bookMarkService.findAllBookmarkCar(members.getMemberId());
        
        System.out.println(bookmarkCarID);
        List<Car> bookmarkCarList = bookMarkService.findAllCar(bookmarkCarID);
        for(Car car: bookmarkCarList) {

           car.setCarMaxPrice(NumberFormat.getInstance().format(Long.parseLong(car.getCarMaxPrice().replace(" ", ""))));
           car.setCarMaxPrice(car.getCarMaxPrice()+"만원");
           car.setCarMinPrice(NumberFormat.getInstance().format(Long.parseLong(car.getCarMinPrice().replace(" ", ""))));
           car.setCarMinPrice(car.getCarMinPrice()+"만원");

        }
        
        model.addAttribute("BookmarkCarList", bookmarkCarList);
        return "mypage/bookmark.html";
    }
    

    /*
     * 즐겨찾기 추가
     * */
    @PostMapping("/bookmark/{carId}")
    public String myPagebookmarkAdd(@PathVariable("carId") String carId, @ModelAttribute("member") Member members,Model model,Bookmark bookmark, HttpServletRequest request) {
      
    	Locale locale = localeResolver.resolveLocale(request);
       bookmark.setCarId(Integer.parseInt(carId));
       bookmark.setMemberId(members.getMemberId());
       Bookmark save_bookmark = bookMarkService.insertMember(bookmark);
       
       model.addAttribute("msg", messageSource.getMessage("bookmark.add", null, locale) );
       model.addAttribute("url", "/mypage/getbookmark/" + carId); 
       return "alert";
    }

    /*
     * bookmark 삭제
     */
    @PostMapping("/bookmark/delete/{carId}")
    public String myPagebookmarkdelete(@PathVariable("carId") String carId,@ModelAttribute("member") Member members, Model model, HttpServletRequest request) {

       Locale locale = localeResolver.resolveLocale(request);
       bookMarkService.findBookmarkByCarDelete(Integer.parseInt(carId), members.getMemberId());
       model.addAttribute("msg", messageSource.getMessage("bookmark.delete", null, locale) );
       model.addAttribute("url", "/mypage/bookmark");
        return "alert";  
    }
    
    /*
     * 자동차데이터 상세보기
     * */
    
    @GetMapping("/getbookmark/{carId}")
    public String myPagegetbookmark(@PathVariable("carId") int carId, Model model) {
       
       Car car = bookMarkService.selectCar(carId);
       
       model.addAttribute("car", car);
        return null;
       // return "alert"; <----- 수정해야함 자동차 상세보기 페이지경로  
    }
    
    /*===================================
     * 나의 게시물
     */
    
    @GetMapping("/myBoard")
	public String getBoardList(Model model, Board board,
	       @RequestParam(name = "curPage", defaultValue = "0") int curPage,
	       @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
	       @RequestParam(name = "searchType", defaultValue = "boardWriter") String searchType,
	       @RequestParam(name = "searchWord", defaultValue = "") String searchWord,HttpSession session) {
    		
    	Member findMember = (Member) session.getAttribute("user");
    	
    	searchWord=findMember.getMemberNickname();
    	System.out.println("====================");
    	System.out.println(searchWord);
		curPage = Math.max(curPage, 0);  // Ensure curPage is not negative
	    Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("boardId").descending());
	    Page<Board> pagedResult = boardService.getBoardList(pageable, searchType, searchWord);
	    
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

	    return "mypage/myboard.html";
	}
    /*
     * 내 게시물 상세 조회
     */
    @PostMapping("/myBoard/{boardId}")
    public String myPagemyboard(@PathVariable("boardId") Long boardId, Model model) {
        // 추가 로직이 필요할 경우 작성
       Board board = boardService.selectBoard(boardId);
       
       model.addAttribute("board", board);
       return null;
      //  return "alert";   <----- 수정해야함 글 상세보기 페이지경로  
    }
    
    
    
}
