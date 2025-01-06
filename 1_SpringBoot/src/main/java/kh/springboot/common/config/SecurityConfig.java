package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 설정 파일 클래스를 빈으로 등록, 빈은 객체
@Configuration
public class SecurityConfig { //설정 파일의 역할을 할 클래스
	
	//반환 값(리턴값)자체를 bean으로 등록하겠다
	@Bean 
	public BCryptPasswordEncoder getPasswordEncoding() {
		return new BCryptPasswordEncoder();
	}
}
