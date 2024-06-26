package com.car.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Notice;

@Service
public interface NoticeService {
	
	long getTotalRowCount(Notice notice);
	Page<Notice> getNoticeList(Pageable pageable, String searchType, String searchWord);
	Notice getNotice(Notice notice);
	Notice getNoticebyId(Long noticeId);
	List<Notice> noticeList();
	
	void insertNotice(Notice notice);
	void updateNotice(Notice notice);
	void deleteNoticeById(Long noticeId);
	void deleteFile(Long noticeId) throws Exception;
	
	
}
