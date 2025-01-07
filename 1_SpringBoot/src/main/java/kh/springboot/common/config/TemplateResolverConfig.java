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
	
}
