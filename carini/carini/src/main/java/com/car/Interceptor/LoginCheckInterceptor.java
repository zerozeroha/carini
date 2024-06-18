package com.car.Interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		String requestURI = request.getRequestURI();
		log.info("인증 체크 인터셉터 실행 {}", requestURI);
		HttpSession session = request.getSession(false);
		
		if (session == null || session.getAttribute("user") == null) {
			log.info("미인증 사용자 요청");
			//로그인으로 redirect
			response.sendRedirect("/member_login?redirectURL=" + requestURI);
			return false;
	 }
	 return true;
	 }
}
