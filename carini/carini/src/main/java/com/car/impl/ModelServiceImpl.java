package com.car.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.car.dto.Car;
import com.car.dto.CarBrand;
import com.car.persistence.CarBrandRepository;
import com.car.persistence.CarRepository;
import com.car.service.ModelService;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;

@Service
public class ModelServiceImpl implements ModelService{
	
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private CarBrandRepository carBrandRepository;
	

	@Override
	public long getTotalRowCount(Car car) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Car> getAllCarList() {
		List<Car> car = carRepository.findAll();
		System.out.println(car);
		return car;
	}

	@Override
	public Car getCarbyId(int carId) {

		Car car = carRepository.findById(carId).get();
		
		return car;
	}
	
	@Override
	public Page<Car> filterCars(Pageable pageable, Long filterMinPrice, Long filterMaxPrice, String filterSize, String filterFuel, String searchWord) {
		
		// 주어진 조건에 따른 Specification 생성
        Specification<Car> spec = createSpecification(filterMinPrice, filterMaxPrice, filterSize, filterFuel, searchWord);
        return carRepository.findAll(spec, pageable);
    }
	
	private Specification<Car> createSpecification(Long filterMinPrice, Long filterMaxPrice, String filterSize, String filterFuel, String searchWord) {
		// root: 조회할 엔티티의 루트를 나타내며, 엔티티의 속성에 접근할 수 있음.
		// query: 쿼리 객체로, 쿼리 자체를 나타냄. select, where 등의 조건을 설정할 수 있음.
		// criteriaBuilder: Predicate(조건)를 생성하는 데 사용되는 빌더 객체.
		// List<Predicate> : 각 필터 조건이 존재하는지 확인하고, 존재할 경우 해당 조건을 Predicates로 추가
		// 모든 Predicate를 AND로 결합하여 Specification을 반환
		
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filterMinPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("carAvgPrice"), filterMinPrice));
            }

            if (filterMaxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("carAvgPrice"), filterMaxPrice));
            }

            if (!"선택안함".equals(filterSize)) {
                predicates.add(criteriaBuilder.like(root.get("carSize"), "%" + filterSize + "%"));
            }

            if (!"선택안함".equals(filterSize)) {
                predicates.add(criteriaBuilder.like(root.get("carFuel"), "%" + filterFuel + "%"));
            }
            
            if (searchWord != null) {
            	predicates.add(criteriaBuilder.like(root.get("carName"), "%" + searchWord + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
	
	@Override
	public void addCarToComparison(int carId, HttpSession session) {
        List<Car> comparisonCars = (List<Car>) session.getAttribute("comparisonCars");
        if (comparisonCars == null) {
            comparisonCars = new ArrayList<>();
        }
        
     // Assuming you have a method to find a car by its ID
        Optional<Car> car = carRepository.findById(carId);
        if (car != null && comparisonCars.size() < 2) {
            comparisonCars.add(car.get());
            session.setAttribute("comparisonCars", comparisonCars);
        }
        
	}
	
	@Override
    public void removeCarFromComparison(int position, HttpSession session) {
        List<Integer> selectedCarIds = (List<Integer>) session.getAttribute("selectedCarIds");
        if (selectedCarIds != null && position >= 0 && position < selectedCarIds.size()) {
            selectedCarIds.remove(position);
        }
        session.setAttribute("selectedCarIds", selectedCarIds);
    }
	
	@Override
    public List<Car> getComparisonCars(HttpSession session) {
        List<Integer> selectedCarIds = (List<Integer>) session.getAttribute("selectedCarIds");
        if (selectedCarIds == null || selectedCarIds.isEmpty()) {
            return new ArrayList<>();
        }
        return findCarsByIds(selectedCarIds); // 자동차 ID로 자동차 정보를 찾는 메서드
    }

    private List<Car> findCarsByIds(List<Integer> carIds) {
        // 자동차 ID로 자동차 정보를 가져오는 로직 (예: DB 조회)
        // 이 예시에서는 임의로 List<Car>를 반환합니다.
        return new ArrayList<>();
    }
    
    @Override
	public CarBrand getURLbrBrand(String carBrandName) {
    	Optional<CarBrand> carBrand =  carBrandRepository.findById(carBrandName);
		
    	if(!carBrand.isPresent()) {
    		return null;
    	}
    	
		return carBrand.get();
	}


}
