package com.car.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.car.dto.Board;
import com.car.dto.Car;
import com.car.dto.Member;
import com.car.dto.Notice;
import com.car.dto.PagingInfo;
import com.car.persistence.CarRepository;
import com.car.service.MemberService;
import com.car.service.ModelService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"member", "pagingInfo"})
public class ModelController {
	
	@Autowired
	private MemberService memberService;
	@Autowired
	private ModelService modelService;
	
	public PagingInfo pagingInfo = new PagingInfo();
	
	@ModelAttribute("member")
	public Member setMember() {
		return new Member(); // 기본 Member 객체를 세션에 저장
	}
	
	/*
	 * 모델 목록보기
	 * */
	@GetMapping("/model/getModelList")
	public String getBoardList(Model model, Car car,
	       @RequestParam(name = "curPage", defaultValue = "0") int curPage,
	       @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
	       @RequestParam(name = "carMinPrice", defaultValue = "0") Long carMinPrice,
	       @RequestParam(name = "carMaxPrice", defaultValue = "1000000000") Long carMaxPrice,
	       @RequestParam(name = "carSize", defaultValue = "선택안함") String carSize,
	       @RequestParam(name = "carFuel", defaultValue = "선택안함") String carFuel,
	       @RequestParam(name = "carSort", defaultValue = "저가순") String carSort,
	       HttpSession session) {
		
		System.out.println("Received carSize: " + carSize); // 디버깅 로그 추가
	    System.out.println("Received carFuel: " + carFuel); // 디버깅 로그 추가
		
		curPage = Math.max(curPage, 0);  // Ensure curPage is not negative
		
		Pageable pageable;
		
		if(carSort == "저가순"){
			pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("carMinPrice").ascending());
		}else if(carSort == "고가순") {
			pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("carMinPrice").descending());
		}else {
			pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("carName").ascending());
		}
	    
	    System.out.println("pageable======>" + pageable);
	    Page<Car> pagedResult = modelService.filterCars(pageable, carMinPrice, carMaxPrice, carSize, carFuel);
	    System.out.println("pagedResult======>" + pagedResult);

	    int totalRowCount  = (int)pagedResult.getNumberOfElements();
	    int totalPageCount = pagedResult.getTotalPages();
	    int pageSize       = pagingInfo.getPageSize();
	    int startPage      = (curPage / pageSize) * pageSize + 1;
	    int endPage        = startPage + pageSize - 1;
	    endPage = endPage > totalPageCount ? (totalPageCount > 0 ? totalPageCount : 1) : endPage;
	    

	    pagingInfo.setCurPage(curPage);
	    pagingInfo.setTotalRowCount(totalRowCount);
	    pagingInfo.setTotalPageCount(totalPageCount);
	    pagingInfo.setStartPage(startPage);
	    pagingInfo.setEndPage(endPage);
	    pagingInfo.setCarMinPrice(carMinPrice);
	    pagingInfo.setCarMaxPrice(carMaxPrice);
	    pagingInfo.setCarSize(carSize);
	    pagingInfo.setCarFuel(carFuel);
	    pagingInfo.setRowSizePerPage(rowSizePerPage);
	    
	    model.addAttribute("pagingInfo", pagingInfo);
	    model.addAttribute("pagedResult", pagedResult);
	    model.addAttribute("pageable", pageable);
		model.addAttribute("cp", curPage);
		model.addAttribute("sp", startPage);
		model.addAttribute("ep", endPage);
		model.addAttribute("ps", pageSize);
		model.addAttribute("rp", rowSizePerPage);
		model.addAttribute("tp", totalPageCount);
	    model.addAttribute("carList", pagedResult.getContent());

	    return "model/getModelList";
	}
	

	
//	@GetMapping("/model/getModelList")
//	public String getBoardList(Model model, Car car, HttpSession session) {
//		
//		List<Car> carList = modelService.getAllCarList();
//		model.addAttribute("carList", carList);
//		
//		
//		return "model/getModelList";
//		
//	}
	

}
	
















