package com.car.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.car.dto.Notice;
import com.car.persistence.NoticeRepository;
import com.car.service.NoticeService;

public class NoticeServiceImpl implements NoticeService {
	
	@Autowired
	private NoticeRepository noticeRepository;

	@Override
	public long getTotalRowCount(Notice notice) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<Notice> getNoticeList(Pageable pageable, String searchType, String searchWord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Notice getNotice(Notice notice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateNotice(Notice notice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertNotice(Notice notice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteNotice(Notice notice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Notice getNoticebyId(Long NoticeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFile(Long boardId) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
