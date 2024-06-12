package com.car.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer>{
	
	//Optional<Car> findAllCar(int car_id);
	
	
	Page<Car> findByCarSizeContaining(String carSize, Pageable pageable);
	Page<Car> findByCarFuelContaining(String carFuel, Pageable pageable);
	
	
}
