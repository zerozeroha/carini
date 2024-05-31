package com.car.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.car.dto.Board;

public interface BoardService {
	
	long getTotalRowCount(Board board);
	Board getBoard(Board board);
	Page<Board> getBoardList(Pageable pageable, String searchType, String searchWord);
	void insertBoard(Board board);
	void updateBoard(Board board);
	void deleteBoard(Board board);
	
}
