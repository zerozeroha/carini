package com.car.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.persistence.BoardRepository;
import com.car.persistence.BookMarkRepository;
import com.car.persistence.CarRepository;
import com.car.service.BookMarkService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookMarkServiceImpl implements BookMarkService{


	@Autowired
	private BookMarkRepository bookMarkRepository;
	
	@Autowired
	private CarRepository carRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
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
		System.out.println(BookmarkList);
		return BookmarkList;
	}

	/*memberId와 carId에 대해 북마크가 존재하는지를 확인*/
	@Override
	public boolean isBookmarkedByMember(String memberId, int carId) {
        return bookMarkRepository.existsByMemberIdAndCarId(memberId, carId);
    }

	/* bookmark 삭제 */
	@Override
	@Transactional
	public void findBookmarkByCarDelete(int carId, String memberId) {
		
		bookMarkRepository.deleteByBookmarkIdAndMemberId(carId, memberId);
	}

	@Override
	@Transactional
	public void insertMember(Bookmark bookmark,Member user) {
		
		List<Bookmark> BookmarkList =bookMarkRepository.findAllByMemberId(user.getMemberId());
		
		if(BookmarkList.isEmpty()) {
			bookMarkRepository.save(bookmark);
		}
		
		for(Bookmark bookmarkone : BookmarkList) {
			if(bookmarkone.getBookmarkNum() == bookmark.getBookmarkNum()) {
			}else {
				bookMarkRepository.save(bookmark);
			}
		}

	}

	@Override
	public Car selectCar(int carId) {
		Optional<Car> car=carRepository.findById(carId);
		return car.get();
	}
	
	@Override
	public int countBookmarkById(String memberId) {
		return bookMarkRepository.getBookmarkCount(memberId);
	}

	@Override
    public Set<Integer> getBookmarkedCarIdsByMember(String memberId) {
        List<Bookmark> bookmarks = bookMarkRepository.findBookmarkByMemberId(memberId);
        return bookmarks.stream()
                .map(Bookmark::getCarId)
                .collect(Collectors.toSet());
    }
	@Override
	@Transactional
	public void deleteMember(Member findmember) {
		
		bookMarkRepository.deleteByMemberId(findmember.getMemberId());
	}
	
	@Override
	public List<Car> getBookmarkTop10Cars() {
		
		List<Integer> carIds = bookMarkRepository.findTop10CarIdsWithCount();
		List<Car> top10Cars = new ArrayList<Car>();
		for(int carId:carIds) {
			Optional<Car> car = carRepository.findById(carId);
			if(car.isPresent()) {
				Car findcar = car.get();
				top10Cars.add(findcar);
			}// if end
		}// for end
		
		return top10Cars;
	}

	
	
	
}
