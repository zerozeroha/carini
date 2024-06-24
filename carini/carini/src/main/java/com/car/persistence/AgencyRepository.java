package com.car.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.car.dto.Agency;
import com.car.dto.Board;

public interface AgencyRepository  extends JpaRepository<Agency, Long>{

	@Query("SELECT a FROM Agency a WHERE a.carBrand = :carBrand")
    List<Agency> findAllCarBrand(@Param("carBrand") String carBrand);

	
	

}
