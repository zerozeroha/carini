package com.car.service;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Board;
import com.car.dto.Member;

@Service
public interface BoardService {
	
	long getTotalRowCount(Board board);
	Board getBoard(Board board);
	Page<Board> getBoardList(Pageable pageable, String searchType, String searchWord);
	void insertBoard(Board board);
	void updateBoard(Board board);
	void deleteBoard(Board board);
	
	List<Board> boardList(Member member);
		

}
