package kh.springboot.member.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import kh.springboot.member.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor //여기에 붙는다
@SessionAttributes("loginUser")
@RequestMapping("/member/")
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
	
	private final BCryptPasswordEncoder bcrypt;
	
	@GetMapping("signIn")
	public String singIn() {
		System.out.println(bcrypt.encode("1234"));
		System.out.println(bcrypt.encode("pass01"));
		System.out.println(bcrypt.encode("pass02"));
		System.out.println(bcrypt.encode("1111"));
		return "login";
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
//	@PostMapping("/member/signIn")
//	public String login(Member m, HttpSession session){
//		//System.out.println("id4 " + m.getId());
//		//System.out.println("pwd4 " + m.getPwd());
//		Member loginUser = mService.login(m);
//		if(loginUser != null) {
//			session.setAttribute("loginUser", loginUser);
//			return "redirect:/home";
//		}else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}
//	}
	

	
//	@GetMapping("/member/logout")
//	public String logout(HttpSession session) {
//		session.invalidate();
//		return "redirect:/home";
//	}
	
	@GetMapping("enroll")
	public String enroll(){
		return "enroll";
	}
	
	@PostMapping("enroll")
	public String enroll(@ModelAttribute Member m,
						 @RequestParam("emailId") String emailId, @RequestParam("emailDomain") String emailDomain) {
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}

		m.setPwd(bcrypt.encode(m.getPwd()));

		int result = mService.insertMember(m);
		if(result > 0) {
			return "redirect:/home";
		}else {
			throw new MemberException("회원가입을 실패하였습니다.");
		}
	}
	
	//암호화 후 로그인
//	@PostMapping("/member/signIn")
//	public String login(Member m, HttpSession session){
//		Member loginUser = mService.login(m);
//		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			session.setAttribute("loginUser", loginUser);
//			return "redirect:/home";
//		}else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}
//	}
	
	/***** 요청 후 전달하고자 있는 데이터가 있는 경우*****/
	//1. Model 이용
	//		맵 형식(key, value)으로 request scope에 데이터를 담아 전달
	//내 정보 조회
//	@GetMapping("/member/myInfo")
//	public String myInfo(HttpSession session, Model model) {
//		Member loginUser = (Member)session.getAttribute("loginUser");
//		if(loginUser != null) {
//			String id = loginUser.getId();
//			
//			//내가 쓴 글과 내가 쓴 댓글을 한 번에 받아오는 법
//			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
//			model.addAttribute("list", list);
//		}
//		return "views/member/myInfo";
//	}
	
	//2. ModelAndView 이용
	//		Model + View
	@GetMapping("myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			String id = loginUser.getId();
			
			//내가 쓴 글과 내가 쓴 댓글을 한 번에 받아오는 법
			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
			mv.addObject("list", list);
			mv.setViewName("views/member/myInfo");
		}
		return mv;
	}
	
	
	// 암호화 후 로그인 + @SessionAttributes
	//		@SessionAttributes는 model에 attribute가 추가될 때 자동으로 키 값을 찾아 세션에 등록하는 어노테이션
	@PostMapping("signIn")
	public String login(Member m, Model model){
		Member loginUser = mService.login(m);
		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser", loginUser);
			return "redirect:/home";
		}else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	}
	
	// @SessionAttributes 추가 후 로그 아웃 구현
	
	@GetMapping("logout")
	public String logout(SessionStatus session) {
		session.setComplete();
		return "redirect:/home";
	}
	
	@GetMapping("edit")
	public String edit() {
		return "edit";
	}
	
	@PostMapping("edit")
	public String edit(@ModelAttribute Member m, @RequestParam("emailId") String emailId, 
					   @RequestParam("emailDomain") String emailDomain, Model model) {
		
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		
		int result = mService.updateMember(m);
		if(result>0) {
			Member loginUser = mService.login(m);
			model.addAttribute("loginUser", loginUser);
			return "redirect:/member/myInfo";
		}else {
			throw new MemberException("정보 수정을 실패하였습니다");
		}
		
	}
	@PostMapping("updatePassword")
	public String updatepassword(@RequestParam("currentPwd") String currentPwd,
								 @RequestParam("newPwd") String newPwd,
								 Model model){
		
		Member m = (Member)model.getAttribute("loginUser");
		
		if(m != null && bcrypt.matches(currentPwd, m.getPwd())) {
			
			m.setPwd(bcrypt.encode(newPwd));
			int result = mService.updatePwd(m);
			
			if(result>0) {
				model.addAttribute("loginUser",  mService.login(m));
				return "redirect:/home";
			}
		}
		throw new MemberException("비밀번호 수정 실패");
	}
	
	@GetMapping("delete")
	public String deleteUser(Model model, SessionStatus session) {
		int result = mService.deleteUser(((Member)model.getAttribute("loginUser")).getId());
		if(result>0) {
			return "redirect:/member/logout";
		}
		throw new MemberException("회원 탈퇴 실패");
	}
	
}
