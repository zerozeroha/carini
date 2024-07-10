package com.car.controller;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.car.dto.Inquiry;
import com.car.dto.Member;
import com.car.dto.Notice;
import com.car.dto.PagingInfo;
import com.car.service.BookMarkService;
import com.car.service.MemberService;
import com.car.service.NoticeService;
import com.car.validation.InquiryWriteValidation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class NoticeController {
	
	private final NoticeService noticeService;

	
	public PagingInfo pagingInfo = new PagingInfo();
	
	@Value("${path.upload}")
	public String uploadFolder;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member(); // 기본 Member 객체를 세션에 저장
	}
	
	/*
	 * 게시판 목록보기
	 * */
	@GetMapping("/notice/getNoticeList")
	public String getNoticeList(Model model, Notice notice,
	       @RequestParam(name = "curPage", defaultValue = "0") int curPage,
	       @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
	       @RequestParam(name = "searchType", defaultValue = "noticeTitle") String searchType,
	       @RequestParam(name = "searchWord", defaultValue = "") String searchWord,@ModelAttribute("InquiryWriteValidation") InquiryWriteValidation InquiryValidation) {
		
		curPage = Math.max(curPage, 0);  // Ensure curPage is not negative
	    Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("noticeId").descending());
	    Page<Notice> pagedResult = noticeService.getNoticeList(pageable, searchType, searchWord);
	    
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
	    model.addAttribute("noticeList", pagedResult.getContent()); // Add this line
	    model.addAttribute("inquiry", new Inquiry());
	    
	    return "notice/getNoticeList";
	}
	
	/*
     * 공지사항 상세보기
     * */
	@GetMapping("/notice/getNotice")
	public String getNotice(Notice notice, Model model,@ModelAttribute("InquiryWriteValidation") InquiryWriteValidation InquiryValidation) {

		model.addAttribute("notice", noticeService.getNoticebyId(notice.getNoticeId())); // 여기서 조회수 증가
		model.addAttribute("inquiry", new Inquiry());

		return "notice/getNotice";
	}
	
	/*
     * 파일 다운로드
     * */
	@GetMapping("/notice/download")
	public ResponseEntity<Resource> handleFileDownload(HttpServletRequest req, 
			@RequestParam(name = "noticeId") Long noticeId, @RequestParam(name = "fn") String fn) throws Exception {
		req.setCharacterEncoding("utf-8");
		String fileName = req.getParameter("fn");
	    Path filePath = Paths.get(uploadFolder + fileName).toAbsolutePath();
	    Resource resource = null;
	    try {
	        resource = new UrlResource(filePath.toUri());	        
	        if (resource.exists()) {
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" 
	                    		+ URLEncoder.encode(resource.getFilename(), "utf-8") + "\"")
	                    .body(resource);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	    } catch (MalformedURLException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	
}


















