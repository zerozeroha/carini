package com.car.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.dto.Board;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.persistence.BoardRepository;
import com.car.service.BoardService;

@Service
public class BoardServiceimpl implements BoardService{

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

	
	
}
