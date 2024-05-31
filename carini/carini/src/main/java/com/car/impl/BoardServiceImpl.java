package com.car.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Board;
import com.car.persistence.BoardRepository;
import com.car.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService{
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Override
	public long getTotalRowCount(Board board) {
		return boardRepository.count();
	}

	@Override
	public Board getBoard(Board board) {
		Optional<Board> findBoard = boardRepository.findById(board.getBoard_seq());
		if(findBoard.isPresent()) {
			boardRepository.updateReadCount(board.getBoard_seq());
			return findBoard.get();
		} else {
			return null;			
		}
	}

	@Override
	public Page<Board> getBoardList(Pageable pageable, String searchType, String searchWord) {
		if(searchType.equalsIgnoreCase("board_title")) {
			return boardRepository.findByTitleContaining(searchWord, pageable);
		} else if(searchType.equalsIgnoreCase("board_writer")) {
			return boardRepository.findByWriterContaining(searchWord, pageable);
		} else {
			return boardRepository.findByContentContaining(searchWord, pageable);
		}
	}

	@Override
	public void insertBoard(Board board) {
		boardRepository.save(board);
		boardRepository.updateLastSeq(0L, 0L, board.getBoard_seq());
	}

	@Override
	public void updateBoard(Board board) {
		Board findBoard = boardRepository.findById(board.getBoard_seq()).get();
		findBoard.setBoardTitle(board.getBoardTitle());
		findBoard.setBoardContent(board.getBoardContent());
		boardRepository.save(findBoard);
	}

	@Override
	public void deleteBoard(Board board) {
		boardRepository.deleteById(board.getBoard_seq());
	}
}
