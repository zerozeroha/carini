package com.car.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class kakaoController {
	
	@Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.redirect-uri}")
    private String redirectUri;
}
