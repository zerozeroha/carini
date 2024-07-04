package com.car.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CommentReply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //기본 키가 데이터베이스에 의해 자동으로 생성되도록 지정
	private Long commentReplyId;
	
	private String replyTextArea;
	
	private Long commentId;
	
	private Long boardId;
	
	private String memberNickname;
	@Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private String commentDate;
	
}
