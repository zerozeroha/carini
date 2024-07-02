package com.car.service;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Board;
import com.car.dto.Member;

@Service
public interface BoardService {
	
	long getTotalRowCount(Board board);
	Page<Board> getBoardList(Pageable pageable, String searchType, String searchWord);
	Board getBoard(Board board, String memberId);
	void updateBoard(Board board);
	void insertBoard(Board board);
	void deleteBoard(Board board);
	Board getBoardById(Long boardId);
	void deleteFile(Long boardId) throws Exception;
	
	List<Board> boardList(Member member);
	Board selectBoard(Long boardId);
	void updateBoardWriter(Member member, String memberNickname);
	int countBoardById(String memberId);
	void deleteMember(Member findmember);
		

}
