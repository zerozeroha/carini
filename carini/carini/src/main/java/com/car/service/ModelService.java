package com.car.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.car.dto.Car;

public interface ModelService {
	
	long getTotalRowCount(Car car);
	Page<Car> getModelList(Pageable pageable, String car_price, String car_size, String car_fuel);
	Car getCar(Car car, Long car_id);
	Car getCarbyId(Long carId);
	
	
}
