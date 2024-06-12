package com.car.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long carId;
	
	private String carName;
	private Long carMinPrice;
	private Long carMaxPrice;
	private String carSize;
	private String carFuel;
	private String carEff;
	private String carImg;
	
	private double carScPer;
	private double carScPrice;
	private double carScGeoju;
	private double carScQuality;
	private double carScDesign;
	private double carScEff;
	private double carScAvg;
}
