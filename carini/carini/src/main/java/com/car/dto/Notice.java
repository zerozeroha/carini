package com.car.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity // 이 클래스를 데이터베이스 테이블에 매핑되는 JPA 엔터티로 표시
@NoArgsConstructor
@AllArgsConstructor
@Builder // 클래스의 인스턴스를 생성
public class Notice {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long noticeId;
	
	@Column(nullable = false)
	private String noticeTitle;
	
	@Column(nullable = false)
	private String noticeContent;
	
	@Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private String noticeDate;
	
	@Column(insertable = false, updatable = true, columnDefinition = "bigint default 0")
	private String noticeCnt;
	
	private String noticeFilename;
	
	@Transient
	private MultipartFile uploadFile;

}













