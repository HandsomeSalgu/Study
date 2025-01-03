package kh.springboot.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import kh.springboot.member.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor //여기에 붙는다
public class MemberController {
	
	//private MemberService mService;
	//객체 생성을 대신 해준다
	//하지만 이렇게 하면 주입이 안됐다, MemberService에서도 @Service를 사용하여 빈을 생성했지만, 현재 mService에는 주입시키지 않았다
	
	//의존성 주입 
	//1. 필드 주입 @Autowired
	@Autowired	//해당 빈을 보고 맞는 게 있으면 자동으로 주입
//	private MemberService mService;
	
	//2. 생성자 주입 @RequiredArgsConstructor + final
	//		@RequiredArgsConstructor : 특정 변수(final이 붙은 상수 혹은 @NotNull이 붙은 변수)만 가지고 생성자 생성(lombok에 있다)
	
	//@Autowired를 사용하면 편하지만, 요즘은 final이 붙으면 변이가 일어나지 않음으로써 오는 불변성이 보장되기 때문에 생성자 주입을 좀 더 권장하고 있다
	private final MemberService mService;
	
	@GetMapping("/member/signIn")
	public String singIn() {
		return "views/member/login";
		// 로그인 화면 연결
	}
	
	/***** 파라미터 전송받는 방법 ******/
	//로그인
	
	//1. (Servlet 방식)HttpServletRequest 이용
//	@PostMapping("/member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println("id1 " + id);
//		System.out.println("pwd1 " + pwd);
//	}
	
	//2. @RequestParam 이용
	//		value			view에서 받아올 파라미터 이름(view의 name)이 들어가는 곳
	//						@RequestParam에 들어가는 속성이 value 하나 뿐이라면 생략 가능
	//		defaultValue	값이 null이거나 들어오지 않았을 때 기본적으로 들어갈 데이터를 지정하는 속성
	//		required		기본 값 true, 지정한 파라미터가 꼭 필요한(필수적인) 변수인지 설정하는 속성
	//						required를 안쓰고 defaultValue를 대신 써도 상관없다
	
	//@RequestParam은 내가 보내온 파라미터만 받을 수 있다 없는 것은 400에러가 뜬다
	//@RequestParam은 값을 내 마음대로 받아올 수 있다(String, int 등등)
//	@PostMapping("/member/signIn")
//	public void login(@RequestParam("id") String userId, @RequestParam("pwd") String userPwd) {
//	public void login(@RequestParam(value="id", defaultValue="testId") String userId, @RequestParam(value="pwd") String userPwd) {
//	public void login(@RequestParam(value="id", defaultValue="testId") String userId, 
//					  @RequestParam(value="pwd") String userPwd,
//					  @RequestParam(value="tt", required=false) String t) {
//		System.out.println("id2 " + userId);
//		System.out.println("pwd2 " + userPwd);
//		System.out.println("tt " + t);
//	}
	
	//3. @RequestParam 생략
//	@PostMapping("/member/signIn")
//	public void login(String id, String pwd){
//		System.out.println("id3 " + id);
//		System.out.println("pwd3 " + pwd);
//	}
	
	//4. @ModelAttribute이용
	//		기본 생성자와 setter를 이용한 주입 방식(둘 중 하나라도 없으면 안된다)
//	@PostMapping("/member/signIn")
//	public void login(@ModelAttribute Member m){
//		System.out.println("id4 " + m.getId());
//		System.out.println("pwd4 " + m.getPwd());
//	}
	
	//5. @ModelAttribute 생략
	@PostMapping("/member/signIn")
	public String login(Member m, HttpSession session){
		//System.out.println("id4 " + m.getId());
		//System.out.println("pwd4 " + m.getPwd());
		Member loginUser = mService.login(m);
		if(loginUser != null) {
			session.setAttribute("loginUser", loginUser);
			return "redirect:/home";
		}else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	}
	
}
