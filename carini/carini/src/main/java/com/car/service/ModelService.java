package com.car.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.car.dto.Car;
import com.car.dto.CarBrand;

import jakarta.servlet.http.HttpSession;

@Service
public interface ModelService {
	
	long getTotalRowCount(Car car);
	List<Car> getAllCarList();
	Car getCarbyId(int carId);
	Page<Car> filterCars(Pageable pageable, Long filterMinPrice, Long filterMaxPrice, String filterSize,
			String filterFuel, String searchWord, String carSort, Boolean exCar);
	void addCarToComparison(int carId, HttpSession session);
	void removeCarFromComparison(int position, HttpSession session);
	List<Car> getComparisonCars(HttpSession session);
	CarBrand getURLbrBrand(String carBrandName);
	void updateCar(Car car);
	void insertCar(Car car);
	void deleteCar(int carId);
	
}
