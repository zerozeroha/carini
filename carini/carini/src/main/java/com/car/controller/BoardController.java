package com.car.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.car.dto.Board;
import com.car.dto.Member;
import com.car.dto.PagingInfo;
import com.car.persistence.BoardRepository;
import com.car.service.BoardService;
import com.car.service.MemberService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"member", "pagingInfo"})
public class BoardController {

	@Autowired
	private BoardService boardService;
	@Autowired
	private MemberService memberService;

	
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
	
	@GetMapping("/board/getBoardList")
	public String getBoardList(Model model, Board board,
	       @RequestParam(name = "curPage", defaultValue = "0") int curPage,
	       @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
	       @RequestParam(name = "searchType", defaultValue = "boardTitle") String searchType,
	       @RequestParam(name = "searchWord", defaultValue = "") String searchWord) {
	    
	    Pageable pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("boardId").descending());
	    Page<Board> pagedResult = boardService.getBoardList(pageable, searchType, searchWord);
	    
	    int totalRowCount  = (int)pagedResult.getNumberOfElements();
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
	    model.addAttribute("boardList", pagedResult.getContent()); // Add this line

	    return "board/getBoardList";
	}


	@GetMapping("/board/getBoard")
	public String getBoard(@ModelAttribute("member") Member member, Board board, Model model) {
		
		if(member.getMemberId() == null) { return "redirect:/member_login"; }
		 
		model.addAttribute("board", boardService.getBoard(board));
		
		return "board/getBoard";
	}
	
	@GetMapping("/board/insertBoard")
	public String insertBoardForm(@ModelAttribute("member") Member member, Board board, Model model) {
		
		if(member.getMemberId() == null) { 
			return "redirect:/member_login";  
		}
		Member fullMember = memberService.findByMemberId(member.getMemberId());
	    model.addAttribute("member", fullMember);

		return "board/insertBoard";
	}
	
	@PostMapping("/board/insertBoard")
	public String insertBoard(@ModelAttribute("member") Member member, Board board) 
			throws IOException {
		
		if(member.getMemberId() == null) {return "redirect:/member_login";}

		// 파일업로드
		MultipartFile uploadFile = board.getUploadFile();
		if(!uploadFile.isEmpty()) {
			String fileName = uploadFile.getOriginalFilename();
			uploadFile.transferTo(new File(uploadFolder + fileName));
			board.setBoardFilename(fileName);
		}

		boardService.insertBoard(board);
		
		return "redirect:/board/getBoardList";
	}
	
	
	@GetMapping("/board/updateBoard")
	public String updateBoardForm(@ModelAttribute("member") Member member, Board board, Model model) {
		
		Board findBoard = boardService.getBoard(board);
		
		if(findBoard.getMemberId().equals(member.getMemberId())) {
			model.addAttribute("board", findBoard);
			return "board/updateBoard";
		}else {
			model.addAttribute("msg", "작성자가 일치하지 않습니다.");
	        model.addAttribute("url", "/board/getBoardList");
			return "alert";
		}

	}
	
	@PostMapping("/board/updateBoard")
	public String updateBoard(@ModelAttribute("member") Member member, Board board, Model model)  {
	    
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
        model.addAttribute("url", "/board/getBoardList");
		return "alert";
	}
	
	
	@GetMapping("/board/deleteBoard")
	public String deleteBoard(@ModelAttribute("member") Member member, Board board)  {
		if(member.getMemberId() == null) {
			return "redirect:/member_login";
		}	
		boardService.deleteBoard(board);
		return "forward:/board/getBoardList";
	}
	
	@GetMapping("/board/download")
	public ResponseEntity<Resource> handleFileDownload(HttpServletRequest req, 
			@RequestParam(name = "boardId") int boardId, @RequestParam(name = "fn") String fn) throws Exception {
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
	
	@PostMapping("/board/updateBoard/deleteFile")
	public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long boardId, @ModelAttribute("member") Member member) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        Board board = boardService.getBoard(Board.builder().boardId(boardId).build());

	        if (member.getMemberId() == null) {
	            response.put("success", false);
	            response.put("message", "로그인이 필요합니다.");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	        }

	        if (!board.getMemberId().equals(member.getMemberId())) {
	            response.put("success", false);
	            response.put("message", "작성자만 파일을 삭제할 수 있습니다.");
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	        }

	        String fileName = board.getBoardFilename();
	        if (fileName != null && !fileName.isEmpty()) {
	            Path filePath = Paths.get(uploadFolder + fileName).toAbsolutePath();
	            Files.deleteIfExists(filePath);
	            board.setBoardFilename(null);  // DB에서 파일명 제거
	            boardService.updateBoard(board);
	            response.put("success", true);
	            response.put("message", "파일이 삭제되었습니다.");
	        } else {
	            response.put("success", false);
	            response.put("message", "삭제할 파일이 없습니다.");
	        }
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        log.error("파일 삭제 중 오류 발생", e);
	        response.put("success", false);
	        response.put("message", "오류가 발생했습니다: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	

}


















