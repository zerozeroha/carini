package com.car.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.car.dto.Agency;

@Service
public interface AgencyService {

	List<Agency> findagency_carBrand(String carBrand);
	List<Agency> findagency_search_brand(String search_brand);
	List<Agency> findagency_search_address(String search_city, String search_gu);
	
}
