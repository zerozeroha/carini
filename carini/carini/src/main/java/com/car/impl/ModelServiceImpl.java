package com.car.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.car.dto.Car;
import com.car.service.ModelService;

public class ModelServiceImpl implements ModelService{

	@Override
	public long getTotalRowCount(Car car) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<Car> getModelList(Pageable pageable, String car_price, String car_size, String car_fuel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Car getCar(Car car, Long car_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Car getCarbyId(Long carId) {
		// TODO Auto-generated method stub
		return null;
	}

}
