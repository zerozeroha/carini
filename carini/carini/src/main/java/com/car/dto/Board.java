package com.car.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
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
@EntityListeners(BoardListeners.class)
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //기본 키가 데이터베이스에 의해 자동으로 생성되도록 지정
	private Long boardId;
	
	@Column(nullable = false)
	private String boardTitle;
	
	private String boardContent;
	
	@Column(updatable = false)
	private String boardWriter;

	private String memberId;
	
	@Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date boardDate; // create_date(테이블에는 이렇게 들어감.)
	
	@Column(insertable = false, updatable = true, columnDefinition = "bigint default 0")
	private Long boardCnt;
	
	private String boardFilename;
	
	@Transient
	private MultipartFile uploadFile;
	
	private Long board_ref;
	private Long board_lev;
	private Long board_seq;
}
