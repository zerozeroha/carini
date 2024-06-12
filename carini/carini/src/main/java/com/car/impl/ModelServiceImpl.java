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
import com.car.persistence.CarRepository;
import com.car.service.ModelService;

import jakarta.persistence.criteria.Predicate;

@Service
public class ModelServiceImpl implements ModelService{
	
	@Autowired
	private CarRepository carRepository;
	

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
	public Car getCarbyId(Long carId) {

		return null;
	}
	
	@Override
	public Page<Car> filterCars(Pageable pageable, Long carMinPrice, Long carMaxPrice, String carSize, String carFuel) {
		
		// 주어진 조건에 따른 Specification 생성
        Specification<Car> spec = createSpecification(carMinPrice, carMaxPrice, carSize, carFuel);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(spec);
        return carRepository.findAll(spec, pageable);
    }
	
	private Specification<Car> createSpecification(Long carMinPrice, Long carMaxPrice, String carSize, String carFuel) {
		// root: 조회할 엔티티의 루트를 나타내며, 엔티티의 속성에 접근할 수 있음.
		// query: 쿼리 객체로, 쿼리 자체를 나타냄. select, where 등의 조건을 설정할 수 있음.
		// criteriaBuilder: Predicate(조건)를 생성하는 데 사용되는 빌더 객체.
		// List<Predicate> : 각 필터 조건이 존재하는지 확인하고, 존재할 경우 해당 조건을 Predicates로 추가
		// 모든 Predicate를 AND로 결합하여 Specification을 반환
		
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (carMinPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("carMinPrice"), carMinPrice));
            }

            if (carMaxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("carMaxPrice"), carMaxPrice));
            }

            if (!"선택안함".equals(carSize)) {
                predicates.add(criteriaBuilder.like(root.get("carSize"), "%" + carSize + "%"));
            }

            if (!"선택안함".equals(carFuel)) {
                predicates.add(criteriaBuilder.like(root.get("carFuel"), "%" + carFuel + "%"));
            }
            System.out.println("predicates" + predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
