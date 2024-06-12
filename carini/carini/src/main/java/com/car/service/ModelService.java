package com.car.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Car;

@Service
public interface ModelService {
	
	long getTotalRowCount(Car car);
	List<Car> getAllCarList();
	Car getCarbyId(Long carId);
	Page<Car> filterCars(Pageable pageable, Long carMinPrice, Long carMaxPrice, String carSize, String carFuel);
	
	
}
