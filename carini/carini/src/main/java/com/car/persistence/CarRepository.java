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
	
	@Query("SELECT c, COUNT(b.carId) AS bookmarkCount " +
           "FROM Car c LEFT JOIN Bookmark b ON c.carId = b.carId " +
           "GROUP BY c.carId " +
           "ORDER BY bookmarkCount DESC")
    List<Object[]> findAllCarsWithBookmarkCount(Pageable pageable);

}


