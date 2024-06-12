package com.car.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.car.dto.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {

//    @Query("SELECT c FROM Car c WHERE c.carMinPrice >= :carMinPrice")
//    Page<Car> findByPriceMoreThanCarMinPrice(@Param("carMinPrice") Long carMinPrice, Pageable pageable);
//
//    @Query("SELECT c FROM Car c WHERE c.carMaxPrice <= :carMaxPrice")
//    Page<Car> findByPriceLessThanCarMaxPrice(@Param("carMaxPrice") Long carMaxPrice, Pageable pageable);
//
//    @Query("SELECT c FROM Car c WHERE c.carSize LIKE %:carSize%")
//    Page<Car> findByCarSizeContaining(@Param("carSize") String carSize, Pageable pageable);
//
//    @Query("SELECT c FROM Car c WHERE c.carFuel LIKE %:carFuel%")
//    Page<Car> findByCarFuelContaining(@Param("carFuel") String carFuel, Pageable pageable);

}


