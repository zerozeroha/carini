package com.car.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.car.controller.AdminController;
import com.car.dto.Board;
import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.CarBrand;
import com.car.persistence.BookMarkRepository;
import com.car.persistence.CarBrandRepository;
import com.car.persistence.CarRepository;
import com.car.service.ModelService;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModelServiceImpl implements ModelService{
	
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private CarBrandRepository carBrandRepository;
	@Autowired
	private BookMarkRepository bookmarkRepository;
	

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
	public Page<Car> filterCars(Pageable pageable, Long filterMinPrice, Long filterMaxPrice, String filterSize, String filterFuel, String searchWord, String carSort, Boolean exCar) {
		
	    // Specification 생성
	    Specification<Car> spec = createSpecification(filterMinPrice, filterMaxPrice, filterSize, filterFuel, searchWord, exCar);
	    
	    Sort sort;
	    if ("즐겨찾기순".equals(carSort)) {
	        sort = Sort.unsorted(); // 정렬은 나중에 수동으로 처리
	    } if ("고가순".equals(carSort)) {
	        sort = Sort.by(Sort.Direction.DESC, "carAvgPrice");
	    } else if ("이름순".equals(carSort)) {
	        sort = Sort.by(Sort.Direction.ASC, "carName");
	    } else {
	        // 기본값 "저가순"
	        sort = Sort.by(Sort.Direction.ASC, "carAvgPrice");
	    }
	    
	    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
	    
	    Page<Car> result = carRepository.findAll(spec, sortedPageable);
	    
	    if ("즐겨찾기순".equals(carSort)) {
	        // 즐겨찾기순 정렬을 위한 특별 처리
	    	List<Car> cars = new ArrayList<>(result.getContent()); // Make a modifiable copy
	        List<Object[]> bookmarkCounts = carRepository.findAllCarsWithBookmarkCount(Pageable.unpaged());
	        
	        Map<Integer, Long> bookmarkCountMap = bookmarkCounts.stream()
	            .collect(Collectors.toMap(
	                obj -> ((Car)obj[0]).getCarId(),
	                obj -> (Long)obj[1]
	            ));
	        
	        cars.forEach(car -> car.setBookmarkCount(bookmarkCountMap.getOrDefault(car.getCarId(), 0L)));
	        cars.sort((c1, c2) -> Long.compare(c2.getBookmarkCount(), c1.getBookmarkCount()));
	        
	        return new PageImpl<>(cars, pageable, result.getTotalElements());
	    }
	    
	    return result;
	}
	
	private Specification<Car> createSpecification(Long filterMinPrice, Long filterMaxPrice, String filterSize, String filterFuel, String searchWord, Boolean exCar) {
		// root: 조회할 엔티티의 루트를 나타내며, 엔티티의 속성에 접근할 수 있음.
		// query: 쿼리 객체로, 쿼리 자체를 나타냄. select, where 등의 조건을 설정할 수 있음.
		// criteriaBuilder: Predicate(조건)를 생성하는 데 사용되는 빌더 객체.
		// List<Predicate> : 각 필터 조건이 존재하는지 확인하고, 존재할 경우 해당 조건을 Predicates로 추가
		// 모든 Predicate를 AND로 결합하여 Specification을 반환
		
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (exCar != null && exCar) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("carAvgPrice"), 50000L));
            } else {
                if (filterMinPrice != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("carAvgPrice"), filterMinPrice));
                }
                if (filterMaxPrice != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("carAvgPrice"), filterMaxPrice));
                }
            }
            if (filterFuel != null && !"선택안함".equals(filterFuel)) {
                predicates.add(criteriaBuilder.like(root.get("carFuel"), "%" + filterFuel + "%"));
            }

            if (filterSize != null && !"선택안함".equals(filterSize)) {
                predicates.add(criteriaBuilder.like(root.get("carSize"), "%" + filterSize + "%"));
            }
            
            if (searchWord != null && !searchWord.isEmpty()) {
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
    
    @Override
    @Transactional
    public void updateCar(Car car) {
        Optional<Car> optionalCar = carRepository.findById(car.getCarId());
        
        if (optionalCar.isPresent()) {
            Car findCar = optionalCar.get();

            findCar.setCarName(car.getCarName());
            findCar.setCarMinPrice(car.getCarMinPrice());
            findCar.setCarMaxPrice(car.getCarMaxPrice());
            findCar.setCarAvgPrice(car.getCarAvgPrice());
            findCar.setCarSize(car.getCarSize());
            findCar.setCarFuel(car.getCarFuel());
            findCar.setCarImg(car.getCarImg());
            findCar.setCarScPer(car.getCarScPer());
            findCar.setCarScPrice(car.getCarScPrice());
            findCar.setCarScGeoju(car.getCarScGeoju());
            findCar.setCarScQuality(car.getCarScQuality());
            findCar.setCarScDesign(car.getCarScDesign());
            findCar.setCarScEff(car.getCarScEff());
            findCar.setCarScAvg(car.getCarScAvg());
            
            carRepository.save(findCar);
        } else {
            throw new IllegalArgumentException("No car found with ID: " + car.getCarId());
        }
    }
    
    @Override
    public void insertCar(Car car) {
    	carRepository.save(car);
    }
    
    @Override
    public void deleteCar(int carId) {
    	Optional<Car> findcar = carRepository.findById(carId);
    	
    	if(findcar.isPresent()) {
    		carRepository.deleteById(carId);
    	}
    }

    
    
    
    
    
    
    
    
    
    
}
