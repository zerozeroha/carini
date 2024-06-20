package com.car;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.car.Interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginCheckInterceptor())
			.order(1)
			.addPathPatterns("/**")
			.excludePathPatterns(
					"/", "/signup", "/member_login", "/logout","/member_login_check",
					"/css/**", "/*.ico", "/error","/js/**","/img/**","/find_idForm","/find_pwForm","/find_id",
					"/find_pw","**.jpg","/find_id_code_check","/find_pw_code_check"
					"/css/**", "/*.ico", "/error","/js/**","/img/**"
			);
	 }
}
