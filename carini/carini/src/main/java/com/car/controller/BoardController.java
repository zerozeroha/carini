package com.car.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.car.dto.Board;
import com.car.dto.Member;
import com.car.dto.PagingInfo;
import com.car.service.BoardService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@SessionAttributes({"member", "pagingInfo"})
public class BoardController {

	@Autowired
	private BoardService boardService;

	public PagingInfo pagingInfo = new PagingInfo();
	
	@Value("${path.upload}")
	public String uploadFolder;
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member();
	}
	
	@GetMapping("/getBoardList")
	public String getBoardList(Model model
			, @RequestParam(defaultValue = "0") int curPage
            , @RequestParam(defaultValue = "10") int rowSizePerPage
            , @RequestParam(defaultValue = "title") String searchType
            , @RequestParam(defaultValue = "") String searchWord) {
		
		Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("seq").descending());
		Page<Board> pagedResult = boardService.getBoardList(pageable, searchType, searchWord);
		
		int totalRowCount  = pagedResult.getNumberOfElements();
		int totalPageCount = pagedResult.getTotalPages();
		int pageSize       = pagingInfo.getPageSize();
		int startPage      = curPage / pageSize * pageSize + 1;
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
		
		return "board/getBoardList";
	}
	
	
	@GetMapping("/getBoard")
	public String getBoard(@ModelAttribute("member") Member member, Board board, Model model) {
		if(member.getMemberId() == null) {
			return "redirect:login";
		}
		model.addAttribute("board", boardService.getBoard(board));
		
		return "board/getBoard";
	}
	
	@GetMapping("/insertBoard")
	public String insertBoardForm(@ModelAttribute("member") Member member) {
		
		if(member.getMemberId() == null) {
			return "redirect:member_login";
		}
		
		return "board/insertBoard";
	}
	
	@PostMapping("/insertBoard")
	public String insertBoard(@ModelAttribute("member") Member member, Board board) 
			throws IOException {
		
		if(member.getMemberId() == null) {
			return "redirect:member_login";
		}
		
		// 파일업로드
				MultipartFile uploadFile = board.getUploadFile();
				if(!uploadFile.isEmpty()) {
					String fileName = uploadFile.getOriginalFilename();
					uploadFile.transferTo(new File(uploadFolder + fileName));
					board.setBoardFilename(fileName);
				}
	
		boardService.insertBoard(board);
		return "redirect:getBoardList";
	}
	
}