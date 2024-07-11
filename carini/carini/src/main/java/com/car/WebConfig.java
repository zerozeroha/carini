package com.car;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.car.Interceptor.AdminCheckInterceptor;
import com.car.Interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginCheckInterceptor())
			.order(1)
			.addPathPatterns("/**")
			.excludePathPatterns(
					"/", "/signup", "/member_login", "/user_logout","/member_login_check", "/home",
					"/css/**", "/*.ico", "/error","/js/**","/img/**","/find_idForm","/find_pwForm","/find_id",
					"/find_pw","**.jpg","/find_id_code_check","/find_pw_code_check",
					"/css/**", "/*.ico", "/error","/js/**","/img/**","/model/getModelList","/model/getModel","/script/**","/update_pw",
					"/center/centerMap","/board/getBoardList","/api/naver/oauth","/oauth/kakao","/center/search_brand","/center/search_address", 
					"/admin/**","/logout2","/homepage/first_home","/api/naver/callback","/api/kakao/callback","/login","/oauth/google","/api/google/callback",
					"/model/getCompareModel", "/model/compare","/inquiry/inquiryList","s3/download/*"
			);
		registry.addInterceptor(new AdminCheckInterceptor())
		.order(2)
		.addPathPatterns("/admin/**");
	 }
}
