package com.car.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Notice;
import com.car.persistence.NoticeRepository;
import com.car.service.NoticeService;

@Service
public class NoticeServiceImpl implements NoticeService {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Value("${path.upload}")
	public String uploadFolder;

	@Override
	public long getTotalRowCount(Notice notice) {
		return noticeRepository.count();
	}

	@Override
	public Page<Notice> getNoticeList(Pageable pageable, String searchType, String searchWord) {
		if(searchType.equalsIgnoreCase("noticeTitle")) {
			return noticeRepository.findByNoticeTitleContaining(searchWord, pageable);
		} else {
			return noticeRepository.findByNoticeContentContaining(searchWord, pageable);
		}
	}

	@Override
	public Notice getNotice(Notice notice) {
		Optional<Notice> findNotice = noticeRepository.findById(notice.getNoticeId());
		if(findNotice.isPresent()) {
			noticeRepository.updateNoticeReadCount(notice.getNoticeId()); //조회수증가
			return findNotice.get();
		} else {
			return null;			
		}
	}


	@Override
	public Notice getNoticebyId(Long noticeId) {
		noticeRepository.updateNoticeReadCount(noticeId); //조회수증가
		return noticeRepository.findById(noticeId)
				.orElseThrow(() -> new RuntimeException("Notice not found"));
	}
	
	@Override
	public List<Notice> noticeList() {
		List<Notice> noticeList = noticeRepository.findAll();
		
		if(noticeList.isEmpty()) {
			return null;
		} else {
			return noticeList;
		}
	}

	@Override
	public void updateNotice(Notice notice) {
		Notice findNotice = noticeRepository.findById(notice.getNoticeId()).get();
		
	}
	
	@Override
	public void insertNotice(Notice notice) {
		
		noticeRepository.save(notice);
		
	}

	@Override
	public void deleteNoticeById(Long noticeId) {
		
		noticeRepository.deleteById(noticeId);
		
	}


}















