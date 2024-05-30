package com.car.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class kakaoController {

	    
	    @Value("${kakao.client-id}")
	    private String clientId;

	    @Value("${kakao.redirect-uri}")
	    private String redirectUri;

}
