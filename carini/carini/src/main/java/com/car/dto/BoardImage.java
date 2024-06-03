package com.car.dto;

import org.springframework.data.annotation.Id;

import jakarta.persistence.ManyToOne;
import lombok.ToString;

//@Entity
@ToString(exclude = "board")
public class BoardImage {

	@Id
	private String uuid;
	private String imageFileName;

	@ManyToOne
	private Board board;
}
