package com.car.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;


import com.car.dto.Board;
import com.car.dto.Bookmark;
import com.car.dto.Car;
import com.car.dto.CarBrand;
import com.car.dto.Member;
import com.car.dto.Notice;
import com.car.dto.PagingInfo;
import com.car.persistence.BookMarkRepository;
import com.car.persistence.CarRepository;
import com.car.service.BookMarkService;
import com.car.service.MemberService;
import com.car.service.ModelService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/model")
public class ModelController {
   
   @Autowired
   private MemberService memberService;
   @Autowired
   private ModelService modelService;
   @Autowired
   private BookMarkService bookMarkService;
   
   @Autowired
   private BookMarkRepository bookMarkRepository;
   @Autowired
   private MessageSource messageSource;
   @Autowired
   private LocaleResolver localeResolver;
   
   public PagingInfo pagingInfo = new PagingInfo();
   
   @ModelAttribute("member")
   public Member setMember() {
      return new Member(); // 기본 Member 객체를 세션에 저장
   }
   
   /*
    * 모델 목록보기
    * */

   @GetMapping("/getModelList")
   public String getBoardList(Model model, 
          @RequestParam(name = "curPage", defaultValue = "0") int curPage,
          @RequestParam(name = "rowSizePerPage", defaultValue = "10") int rowSizePerPage,
          @RequestParam(name = "filterMinPrice", defaultValue = "0") Long filterMinPrice,
          @RequestParam(name = "filterMaxPrice", defaultValue = "1000000000") Long filterMaxPrice,
          @RequestParam(name = "filterSize", defaultValue = "선택안함") String filterSize,
          @RequestParam(name = "filterFuel", defaultValue = "선택안함") String filterFuel,
          @RequestParam(name = "carSort", defaultValue = "저가순") String carSort,
          @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
          HttpServletRequest request) {
      HttpSession session = request.getSession(false);
      Member user = null;
      
       // 세션이 null이 아니면 사용자 정보를 가져옴
      if (session != null) {
           user = (Member) session.getAttribute("user");
       }


      
      curPage = Math.max(curPage, 0);  // Ensure curPage is not negative
      
      Pageable pageable;

      if(carSort.equals("저가순")){
         pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("carAvgPrice").ascending());
      }else if(carSort.equals("고가순")) {
         pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("carAvgPrice").descending());
      }else {
         pageable = PageRequest.of(curPage, rowSizePerPage, Sort.by("carName").ascending());
      }
       
       Page<Car> pagedResult = modelService.filterCars(pageable, filterMinPrice, filterMaxPrice, filterSize, filterFuel, searchWord);

       // 즐겨찾기 추가
       for (Car car1 : pagedResult) {
          boolean isBookmarked = false;
          if (user != null) {
             isBookmarked = bookMarkService.isBookmarkedByMember(user.getMemberId(), car1.getCarId());
          }
           car1.setBookmarked(isBookmarked);

       }


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
       pagingInfo.setCarMinPrice(filterMinPrice);
       pagingInfo.setCarMaxPrice(filterMaxPrice);
       pagingInfo.setCarSize(filterSize);
       pagingInfo.setCarFuel(filterFuel);
       pagingInfo.setSearchWord(searchWord);
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
        model.addAttribute("sw", searchWord);
        model.addAttribute("fpr", filterMinPrice);
        model.addAttribute("fph", filterMaxPrice);
        model.addAttribute("fs", filterSize);
        model.addAttribute("ff", filterFuel);
        model.addAttribute("cs", carSort);
       model.addAttribute("carList", pagedResult.getContent());
       model.addAttribute("user", user);
       
       return "model/getModelList.html";
   }
   
    @GetMapping("/getModel")
    public String getCar(@RequestParam("carId") int carId, Model model, HttpServletRequest request) {
       HttpSession session = request.getSession(false);
      Member user = (Member) session.getAttribute("user");
       Car car = modelService.getCarbyId(carId);
       
       String[] carName = car.getCarName().strip().split(" ");
       String carBrandName = carName[0];
       
       CarBrand carBrand = modelService.getURLbrBrand(carBrandName);
       
       boolean isBookmarked = false;
       if (user != null) {
          isBookmarked = bookMarkService.isBookmarkedByMember(user.getMemberId(), car.getCarId());
       }
       car.setBookmarked(isBookmarked);
       System.out.println(car);
       model.addAttribute("car", car);
       model.addAttribute("carBrand", carBrand);
       
        return "model/getModel.html";
    }
    
    
   /*
    * 차 비교
    * */   
    @GetMapping("/getCompareModel")
    @ResponseBody
    public Car getCompareModel(@RequestParam("carId") int carId) {
       
       Car car1 = modelService.getCarbyId(carId);
       
       return car1;
    }
   
    @GetMapping("/compare")
    @ResponseBody
    public Map<String, Car> compareCars(@RequestParam("carId1") int carId1, @RequestParam("carId2") int carId2) {
        Car car1 = modelService.getCarbyId(carId1);
        Car car2 = modelService.getCarbyId(carId2);
        
        Map<String, Car> comparisonData = new HashMap<>();
        comparisonData.put("car1", car1);
        comparisonData.put("car2", car2);
        
        return comparisonData;
    }

    
    /**
     * bookmark 추가(겟모델)
     * */
   @PostMapping("/bookmark/{carId}")
   public String modelbookmarkAdd(@PathVariable("carId") String carId, Model model, Bookmark bookmark,
         HttpServletRequest request, HttpSession session) {
      
      Locale locale = localeResolver.resolveLocale(request);

      Member user = (Member) session.getAttribute("user");

      bookmark.setCarId(Integer.parseInt(carId));
      bookmark.setMemberId(user.getMemberId());

      bookMarkService.insertMember(bookmark, user);

      model.addAttribute("msg", messageSource.getMessage("bookmark.add", null, locale));
      model.addAttribute("url", request.getHeader("Referer"));
      return "alert";
   }


   
   /*
    * bookmark 삭제(겟모델)
    */
   @PostMapping("/bookmark/delete/{carId}")
   public String modelbookmarkdelete(@PathVariable("carId") String carId, Model model, HttpServletRequest request,
         HttpSession session) {

      Locale locale = localeResolver.resolveLocale(request);
      Member user = (Member) session.getAttribute("user");

      bookMarkService.findBookmarkByCarDelete(Integer.parseInt(carId), user.getMemberId());
      model.addAttribute("msg", messageSource.getMessage("bookmark.delete", null, locale));
      model.addAttribute("url", request.getHeader("Referer"));
      return "alert";
   }
    
    
    
}