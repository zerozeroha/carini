package com.car.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.car.dto.CarBrand;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, String> {

}
