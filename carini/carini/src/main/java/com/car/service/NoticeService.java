package com.car.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Notice;

@Service
public interface NoticeService {
	
	long getTotalRowCount(Notice notice);
	Page<Notice> getNoticeList(Pageable pageable, String searchType, String searchWord);
	Notice getNotice(Notice notice);
	void updateNotice(Notice notice);
	void insertNotice(Notice notice);
	void deleteNotice(Notice notice);
	Notice getNoticebyId(Long NoticeId);
	void deleteFile(Long boardId) throws Exception;
	
	
	
}
