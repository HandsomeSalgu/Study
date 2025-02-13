package kh.springboot.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kh.springboot.common.interceptor.CheckAdminInterceptor;
import kh.springboot.common.interceptor.CheckLoginInterceptor;
import kh.springboot.common.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")	//내가 매핑할 uri를 설정하는 것 (파일을 가지고 올 때 경로를 어떻게 쓸지 결정)
				.addResourceLocations("file:///d:/dev/uploadFiles/","file:///d:/dev/profiles/", "classpath:/static/");//정적 리소스 위치
				//여기까지만 하면 uploadFiles만 보기 때문이고, image/라는 게 계속 들어가야된다
				//그래서 classpath:/static/을 추가하고 매핑 uri 설정하는 곳에

	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(new TestInterceptor())//인터셉터
//				.addPathPatterns("/**"); 
		
		
		registry.addInterceptor(new CheckLoginInterceptor())
				.addPathPatterns("/member/myInfo", "/member/edit","/member/updatePassword","/member/delete")
				.addPathPatterns("/board/**", "/attm/**")
				.excludePathPatterns("/board/list", "/attm/list", "/board/top");
		
		registry.addInterceptor(new CheckAdminInterceptor())
			    .addPathPatterns("/admin/**");
		
		registry.addInterceptor(new LoginInterceptor())
				.addPathPatterns("/member/signIn");
	}
	
	
}
