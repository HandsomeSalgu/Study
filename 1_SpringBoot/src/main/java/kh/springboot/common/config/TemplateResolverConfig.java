package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

// 설정 파일 클래스를 빈으로 등록
@Configuration
public class TemplateResolverConfig {
	
	@Bean
	public ClassLoaderTemplateResolver memberResolver() {
		ClassLoaderTemplateResolver mResolver = new ClassLoaderTemplateResolver();
		mResolver.setPrefix("templates/views/member/");
		mResolver.setSuffix(".html");
		
		mResolver.setTemplateMode(TemplateMode.HTML);
		//HTML 설정
		
		mResolver.setCharacterEncoding("UTF-8");
		//한글이니깐 UTF-8
		
		mResolver.setCacheable(false);
		//자동 새로고침 기능

		mResolver.setCheckExistence(true);
		//templates/views/member/ 의 각 목록마다 한개씩 다 비교해서 찾아주게 하는 기능
		
		return mResolver;
	}
	
	@Bean
	public ClassLoaderTemplateResolver boardResolver() {
		ClassLoaderTemplateResolver bResolver = new ClassLoaderTemplateResolver();
		bResolver.setPrefix("templates/views/board/");
		bResolver.setSuffix(".html");
		bResolver.setTemplateMode(TemplateMode.HTML);
		bResolver.setCharacterEncoding("UTF-8");
		bResolver.setCacheable(false);
		bResolver.setCheckExistence(true);
		
		return bResolver;
	}
	
	@Bean
	public ClassLoaderTemplateResolver adminResolver() {
		ClassLoaderTemplateResolver mResolver = new ClassLoaderTemplateResolver();
		mResolver.setPrefix("templates/views/admin/");
		mResolver.setSuffix(".html");
		
		mResolver.setTemplateMode(TemplateMode.HTML);
		//HTML 설정
		
		mResolver.setCharacterEncoding("UTF-8");
		//한글이니깐 UTF-8
		
		mResolver.setCacheable(false);
		//자동 새로고침 기능

		mResolver.setCheckExistence(true);
		//templates/views/member/ 의 각 목록마다 한개씩 다 비교해서 찾아주게 하는 기능
		
		return mResolver;
	}
	
	//스프링에서의 viewResolver는 url pattern에 따라 viewResolver가 정해진다
	//스프링부트에서의 viewResolver는 VR1, VR2, VR3 이렇게 여러개 만들어져서 한 개씩 비교해 같은 게 있으면 바로 걔를 가져온다
}
