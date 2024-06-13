package com.car.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints ={@UniqueConstraint(columnNames = {"member_id", "car_id"})})
public class Bookmark {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bookmarkNum;
	
    private String memberId;
	private int carId;
	
}

