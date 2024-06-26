package com.car.impl;

import java.io.File;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.car.dto.Board;
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
	@Transactional
	public void updateNotice(Notice notice) {
		Notice findNotice = noticeRepository.findById(notice.getNoticeId()).get();
		findNotice.setNoticeTitle(notice.getNoticeTitle());
		findNotice.setNoticeContent(notice.getNoticeContent());
		// 새 파일이 업로드되었을 때만 파일명 변경
        if(notice.getUploadFile() != null && !notice.getUploadFile().isEmpty()) {
			try {
				String fileName = saveFile(notice.getUploadFile());
				findNotice.setNoticeFilename(fileName);
			} catch (IOException e) {
				throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
			}
        }
		noticeRepository.save(findNotice);
	}
	
	@Override
	public void insertNotice(Notice notice) {
		
		noticeRepository.save(notice);
		
	}

	@Override
	public void deleteNoticeById(Long noticeId) {
		
		noticeRepository.deleteById(noticeId);
		
	}
	
	private String saveFile(MultipartFile file) throws IOException {
	    if (file == null || file.isEmpty()) {
	        throw new IOException("파일이 없습니다.");
	    }

	    String fileName = file.getOriginalFilename();
	    Path filePath = Paths.get(uploadFolder, fileName);
	    File directory = new File(uploadFolder);

	    if (!directory.exists()) {
	        if (!directory.mkdirs()) {
	            throw new IOException("디렉토리를 생성할 수 없습니다: " + uploadFolder);
	        }
	    }

	    // 파일 경로와 파일명을 로그로 출력
	    System.out.println("파일 저장 경로: " + filePath.toString());
	    System.out.println("파일명: " + fileName);

	    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	    return fileName;
	}
	
	
	@Override
	public void deleteFile(Long noticeId) throws Exception {
		
		Notice notice = noticeRepository.findById(noticeId)
		        .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

	    String filename = notice.getNoticeFilename();
	    
	    if(filename != null && !filename.isEmpty()) {
	        Path filePath = Paths.get(uploadFolder + filename);
	        if (Files.deleteIfExists(filePath)) {
	            notice.setNoticeFilename(null);
	            noticeRepository.save(notice);
	        } else {
	            throw new Exception("파일을 삭제할 수 없습니다.");
	        }
	    } else {
	        throw new IllegalStateException("삭제할 파일이 없습니다.");
	    }
	}


}















