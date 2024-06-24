package com.car.controller;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.RequestAttributes;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.car.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;




import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.RedirectView;

import com.car.dto.Agency;
import com.car.dto.Board;
import com.car.dto.Member;
import com.car.dto.Notice;
import com.car.dto.PagingInfo;
import com.car.persistence.BoardRepository;
import com.car.service.AgencyService;
import com.car.service.BoardService;
import com.car.service.MemberService;
import com.car.service.NoticeService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;



@RequestMapping("/center")
@Controller
public class CenterController {

	@Autowired
	private AgencyService agencyService;
	
    @GetMapping("/centerMap")
    public String centerView(@RequestParam(value="carBrand",defaultValue = "") String carBrand,Model model) {
    	
    	List<Agency> agencies = agencyService.findagency_carBrand(carBrand);
    	
    	model.addAttribute("agency", agencies);
    	
        return "center/centerMap";
    }
    
    @PostMapping("/search_brand")
    public String search_brandCenter(@RequestParam(value="search_brand", defaultValue = "") String search_brand,Model model) {
    	List<Agency> agencies = agencyService.findagency_search_brand(search_brand);
    	
    	if(agencies.isEmpty()) {
    		model.addAttribute("msg", "검색결과가 없습니다.");
    		model.addAttribute("url", "/center/centerMap");
    		return "alert";
    	}
    	
    	model.addAttribute("agency", agencies);
        	
        return "center/centerMap";
    
    }
    
    @PostMapping("/search_address")
    public String search_addressCenter(@RequestParam(value="city", defaultValue = "") String search_city,
    		@RequestParam(value="gu", defaultValue = "") String search_gu,
    		Model model) {
    	List<Agency> agencies = agencyService.findagency_search_address(search_city,search_gu);
    	
    	if(agencies.isEmpty()) {
    		model.addAttribute("msg", "검색결과가 없습니다.");
    		model.addAttribute("url", "/center/centerMap");
    		return "alert";
    	}
    	
    	model.addAttribute("agency", agencies);
        return "center/centerMap";
    
    }
}
