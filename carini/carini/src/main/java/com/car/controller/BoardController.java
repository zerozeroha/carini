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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.RedirectView;

import com.car.dto.Board;
import com.car.dto.Member;
import com.car.dto.Notice;
import com.car.dto.PagingInfo;
import com.car.persistence.BoardRepository;
import com.car.service.BoardService;
import com.car.service.MemberService;
import com.car.service.NoticeService;
import com.car.validation.BoardUpdateFormValidation;

import com.car.validation.BoardWriteFormValidation;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"user", "pagingInfo"})
public class BoardController {

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
   
   @Value("${path.upload}")
   public String uploadFolder;
   
   @ModelAttribute("member")
   public Member setMember() {
      return new Member(); // 기본 Member 객체를 세션에 저장
   }
   /*
   @GetMapping("/layoutExtend")
   public String layoutExtends() {
    return "test/main";
   }
   */
   
   /*
    * 게시판 목록보기
    * */
   @GetMapping("/board/getBoardList")
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

       return "board/getBoardList";
   }

   /*
     * 게시판 상세보기
     * */
   @GetMapping("/board/getBoard")
   public String getBoard(Board board, Model model, HttpSession session) {
      Member user = (Member) session.getAttribute("user");
       
      model.addAttribute("board", boardService.getBoard(board, user.getMemberId())); // 여기서 조회수 증가
      
      return "board/getBoard";
   }
   
   /*
     * 게시판 작성
     * */
   @GetMapping("/board/insertBoard")
   public String insertBoardForm(Board board, Member member, @ModelAttribute("BoardWriteFormValidation") BoardWriteFormValidation boardValidation ,Model model) {
	   
	  LocalDate currentDate = LocalDate.now();
	  
      model.addAttribute("date",currentDate);
      
      return "board/insertBoard";
   }
   
   
   /*
     * 게시판 작성
     * */
   @PostMapping("/board/insertBoard")
   public String insertBoard(Board board,@Validated @ModelAttribute("BoardWriteFormValidation") BoardWriteFormValidation boardValidation, BindingResult bindingResult,Model model) 

         throws IOException {
	  LocalDate currentDate = LocalDate.now();
	  model.addAttribute("date",currentDate);
      if(bindingResult.hasErrors()) {
			return "board/insertBoard";
      }
<<<<<<< HEAD
=======
      // 파일업로드
>>>>>>> bc7f65d7bb21ed23fedfc34aa8fcc3d90b1552d9
      MultipartFile uploadFile = board.getUploadFile();
      if(!uploadFile.isEmpty()) {
         String fileName = uploadFile.getOriginalFilename();
         uploadFile.transferTo(new File(uploadFolder + fileName));
         board.setBoardFilename(fileName);
      }
      
      

      boardService.insertBoard(board);
      model.addAttribute("msg", "게시글이 작성되었습니다!");
      model.addAttribute("url", "/board/getBoardList");
      return "alert";
   }
   
   /*
     * 게시판 수정 폼
     * */
   @GetMapping("/board/updateBoard")
   public String updateBoardForm(@RequestParam("boardId") Long boardId, Model model,
                                 @ModelAttribute("BoardUpdateFormValidation") BoardUpdateFormValidation boardValidation,
                                 BindingResult bindingResult, HttpSession session) {
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
           
           return "board/updateBoard";  // 게시글 수정 페이지
       }
       
       model.addAttribute("msg", "작성자만 수정이 가능합니다!.");
       model.addAttribute("url", "/board/getBoard?boardId=" + board.getBoardId());
       return "alert";
   }
   /*
    * 게시판 수정 하기
    * */
   @PostMapping("/board/updateBoard")
   public String updateBoard(Board board,@Validated @ModelAttribute("BoardUpdateFormValidation") BoardUpdateFormValidation boardValidation ,
		   BindingResult bindingResult, Model model)  {
	   
	   if (bindingResult.hasErrors()) {

	       return "board/updateBoard";
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
      model.addAttribute("url", "/board/getBoard?boardId=" + board.getBoardId());
      return "alert";
   }
   
   
   /*
     * 게시판 삭제
     * */
   @GetMapping("/board/deleteBoard")
   public String deleteBoard(Board board, HttpSession session)  {
      boardService.deleteBoard(board);
      return "forward:/board/getBoardList";
   }
   
   /*
     * 파일 다운로드
     * */
   @GetMapping("/board/download")
   public ResponseEntity<Resource> handleFileDownload(HttpServletRequest req, 
         @RequestParam(name = "boardId") Long boardId, @RequestParam(name = "fn") String fn) throws Exception {
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
   
   /*
     * 파일 삭제
     * */
   @PostMapping("/board/deleteFile/{boardId}")
   @ResponseBody
   public ResponseEntity<Map<String, String>> deleteFile(@PathVariable(name = "boardId") Long boardId, HttpServletRequest request) {
       Map<String, String> response = new HashMap<>();
       Locale locale = localeResolver.resolveLocale(request);
       try {
           boardService.deleteFile(boardId);
           response.put("message", messageSource.getMessage("board.filedelete.success", null, locale));
           response.put("status", "success");
           return ResponseEntity.ok(response);
       } catch (Exception e) {
           log.error("게시글 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
           response.put("message", messageSource.getMessage("board.filedelete.failure", null, locale));
           response.put("status", "error");
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
       }
   }

}


















