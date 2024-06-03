package com.car.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	@Column(name="CAR_ID")
	private int car_id;
	
	@Column(name="CAR_NAME")
	private String car_name;
	
	@Column(name="CAR_MIN_PRICE")
	private String car_min_price;
	
	@Column(name="CAR_MAX_PRICE")
	private String car_max_price;
	
	@Column(name="CAR_SIZE")
	private String car_size;
	
	@Column(name="CAR_FUEL")
	private String car_fuel;
	
	@Column(name="CAR_EFF")
	private String car_eff;
	
	@Column(name="CAR_IMG")
	private String car_img;
	
	@Column(name="CAR_SC_PER")
	private double car_sc_per;
	
	@Column(name="CAR_SC_PRICE")
	private double car_sc_price;
	
	@Column(name="CAR_SC_GEOJU")
	private double car_sc_geoju;
	
	@Column(name="CAR_SC_QUALITY")
	private double car_SC_quality;
	
	@Column(name="CAR_SC_DESIGN")
	private double car_sc_design;
	
	@Column(name="CAR_SC_EFF")
	private double car_sc_eff;
	
	
}
