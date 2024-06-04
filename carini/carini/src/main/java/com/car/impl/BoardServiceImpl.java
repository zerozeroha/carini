package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	@Override
	public List<Board> boardList(Member member) {
		
		List<Board> boardList = boardRepository.findBymemberId(member.getMemberId());
		
		if(boardList.isEmpty()) {
			System.out.println("66666");
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
	public Board getBoard(Board board) {
		Optional<Board> findBoard = boardRepository.findById(board.getBoardId());
		if(findBoard.isPresent()) {
			boardRepository.updateReadCount(board.getBoardId());
			return findBoard.get();
		} else {
			return null;			
		}
	}

	@Override
	public Page<Board> getBoardList(Pageable pageable, String searchType, String searchWord) {
		if(searchType.equalsIgnoreCase("board_title")) {
			return boardRepository.findByBoardTitleContaining(searchWord, pageable);
		} else if(searchType.equalsIgnoreCase("board_writer")) {
			return boardRepository.findByBoardWriterContaining(searchWord, pageable);
		} else {
			return boardRepository.findByBoardContentContaining(searchWord, pageable);
		}
	}

	@Override
	public void insertBoard(Board board) {
		boardRepository.save(board);
//		boardRepository.updateLastSeq(0L, 0L, board.getBoardId());
	}

	@Override
	public void updateBoard(Board board) {
		Board findBoard = boardRepository.findById(board.getBoardId()).get();
		findBoard.setBoardTitle(board.getBoardTitle());
		findBoard.setBoardContent(board.getBoardContent());
		boardRepository.save(findBoard);
	}

	@Override
	public void deleteBoard(Board board) {
		boardRepository.deleteById(board.getBoardId());
	}

	
	
}
