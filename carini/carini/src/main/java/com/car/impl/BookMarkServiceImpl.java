package com.car.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.persistence.CarRepository;
import com.car.persistence.BookMarkRepository;
import com.car.service.BookMarkService;

import jakarta.transaction.Transactional;

@Service

public class BookMarkServiceImpl implements BookMarkService{


	@Autowired
	private BookMarkRepository bookMarkRepository;
	@Autowired
	private CarRepository carRepository;
	
	/* 멤버 아이디로 차데이터(번호) 추출 */
	@Override
	public List<Bookmark> findAllBookmarkCar(String id) {
		List<Bookmark> BookmarkCarList = bookMarkRepository.findBookmarkByMemberId(id);
		
		
		return BookmarkCarList;
	}

	/* 차데이터(번호)로 bookmark 리스트 추출 */
	@Override
	public List<Car> findAllCar(List<Bookmark> BookmarkCarList) {
		
		List<Car> BookmarkList = new ArrayList<>();
		for(Bookmark bookmark : BookmarkCarList) {
			Optional<Car> car=carRepository.findById(bookmark.getCarId());

			BookmarkList.add(car.get());
		}
		return BookmarkList;
	}

	/* bookmark 삭제 */
	@Override
	@Transactional
	public void findBookmarkByCarDelete(int carid,String memberId) {
		
		bookMarkRepository.deleteByBookmarkIdAndMemberId(carid, memberId);
	}


}
