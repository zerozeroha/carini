package com.car.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.dto.Agency;
import com.car.persistence.AgencyRepository;
import com.car.service.AgencyService;

@Service
public class AgencyServiceImpl implements AgencyService{

	
	@Autowired
	private AgencyRepository agencyRepository;

	@Override
	public List<Agency> findagency_carBrand(String carBrand) {
		 
		List<Agency> agencies;
		if(carBrand.equals("")) {
			agencies= agencyRepository.findAll();
		}else {
			agencies= agencyRepository.findAllCarBrand(carBrand);
		}
		return agencies;
	}
	
	@Override
	public List<Agency> findagency_search_brand(String search_brand) {
		 
		if(search_brand.equals("")) {
			return agencyRepository.findAll();
		}else {
			return agencyRepository.findAllCarBrand(search_brand);
		}
	}
	
	@Override
	public List<Agency> findagency_search_address(String search_city, String search_gu) {
		
		List<Agency> agencies;
		List<Agency> new_agencies=new ArrayList();
		if(search_city.equals("") && search_gu.equals("")) {
			return agencyRepository.findAll();
		}else{
			agencies= agencyRepository.findAll();
			for (Agency agency : agencies) {
				
				String[] addressParts = agency.getAgencyAddress().split(" ");
				System.out.println(addressParts[0]);
				System.out.println(search_city);
				System.out.println(addressParts[1]);
				System.out.println(search_gu);
				if((addressParts[0].equals("") || addressParts[0].equals(search_city)) && (addressParts[1].equals("") || addressParts[1].equals(search_gu))) {
					System.out.println(agency);
					new_agencies.add(agency);
				}
			}
			return new_agencies;
		}
	}
	
	
}
