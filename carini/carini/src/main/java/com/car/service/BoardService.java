package com.car.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.car.dto.Board;
import com.car.dto.Member;

@Service
public interface BoardService {

	List<Board> boardList(Member member);
		
}
