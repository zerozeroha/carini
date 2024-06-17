package com.car.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.car.dto.Board;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.persistence.BoardRepository;
import com.car.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardRepository boardRepository;
	
	@Value("${path.upload}")
	public String uploadFolder;
	
	@Override
	public List<Board> boardList(Member member) {
		
		List<Board> boardList = boardRepository.findBymemberId(member.getMemberId());
		
		if(boardList.isEmpty()) {
			
			return null;
		}else {
			return boardList;
		}
	}

	@Override
	public long getTotalRowCount(Board board) {
		return boardRepository.count();
	}
	
	@Override
	public Page<Board> getBoardList(Pageable pageable, String searchType, String searchWord) {
		if(searchType.equalsIgnoreCase("boardTitle")) {
			return boardRepository.findByBoardTitleContaining(searchWord, pageable);
		} else if(searchType.equalsIgnoreCase("boardWriter")) {
			return boardRepository.findByBoardWriterContaining(searchWord, pageable);
		} else {
			return boardRepository.findByBoardContentContaining(searchWord, pageable);
		}
	}

	@Override
	public Board getBoard(Board board, String memberId) {
		Optional<Board> findBoard = boardRepository.findById(board.getBoardId());
		if(findBoard.isPresent()) {
			boardRepository.updateReadCount(board.getBoardId(), memberId); //조회수증가
			return findBoard.get();
		} else {
			return null;			
		}
	}
	
	@Override
	@Transactional
	public void updateBoard(Board board) {
		
		Board findBoard = boardRepository.findById(board.getBoardId()).get();
		findBoard.setBoardTitle(board.getBoardTitle());
		findBoard.setBoardContent(board.getBoardContent());
		 // 새 파일이 업로드되었을 때만 파일명 변경
        if(board.getUploadFile() != null && !board.getUploadFile().isEmpty()) {
        	String fileName;
			try {
				fileName = saveFile(board.getUploadFile());
				findBoard.setBoardFilename(fileName);
			} catch (IOException e) {
				throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
			}
        }
		boardRepository.save(findBoard);
	}
	
	private String saveFile(MultipartFile file) throws IOException {
	    if (file == null || file.isEmpty()) {
	        throw new IllegalArgumentException("파일이 없습니다.");
	    }

	    Path uploadPath = Paths.get(uploadFolder);

	    // 파일 이름 충돌 방지를 위한 타임스탬프 추가
	    String originalFilename = file.getOriginalFilename();
	    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    String newFilename = System.currentTimeMillis() + extension;

	    Path filePath = uploadPath.resolve(newFilename);

	    // 파일 저장
	    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	    return newFilename;
	}
	

	@Override
	public void insertBoard(Board board) {
		boardRepository.save(board);
//		boardRepository.updateLastSeq(0L, 0L, board.getBoardId());
	}

	
	@Override
	public void deleteBoard(Board board) {
		boardRepository.deleteById(board.getBoardId());
	}

	
	@Override
	public Board selectBoard(Long boardId) {
		
		Optional<Board> board=boardRepository.findById(boardId);
		
		return board.get();
	}
	
	
	@Override
	public Board getBoardById(Long boardId) {
		return boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("Board not found"));
	}

	
	@Override
	public void deleteFile(Long boardId) throws Exception {
		
		Board board = boardRepository.findById(boardId)
		        .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

	    String filename = board.getBoardFilename();
	    if(filename != null && !filename.isEmpty()) {
	        Path filePath = Paths.get(uploadFolder + filename);
	        if (Files.deleteIfExists(filePath)) {
	        	System.out.println("=====================");
	        	System.out.println(Files.deleteIfExists(filePath));
	            board.setBoardFilename(null);
	            boardRepository.save(board);
	        } else {
	            throw new Exception("파일을 삭제할 수 없습니다.");
	        }
	    } else {
	        throw new IllegalStateException("삭제할 파일이 없습니다.");
	    }
		
		
	}

	@Override
	@Transactional
	public void updateBoardWriter(Member member, String newmemberNickname) {
		boardRepository.updateBoardWriter(newmemberNickname,member.getMemberId());
		
	}

	

	
	
}
