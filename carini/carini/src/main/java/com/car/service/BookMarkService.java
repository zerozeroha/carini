package com.car.service;

import java.util.List;
import java.util.Optional;

import com.car.dto.Board;
import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.Member;
public interface BookMarkService {

	List<Bookmark> findAllBookmarkCar(String id);

	List<Car> findAllCar(List<Bookmark> bookmarkCar_ID);

	void findBookmarkByCarDelete(int car_id,String member_id);

	Bookmark insertMember(Bookmark bookmark);

	Car selectCar(int carId);
	
}
