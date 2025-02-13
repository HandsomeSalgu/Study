package kh.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kh.springboot.board.controller.BoardController;
import kh.springboot.board.model.vo.Board;
import lombok.RequiredArgsConstructor;

@SpringBootTest
//@RequiredArgsConstructor : 컨테이너로 들어가는 게 아니라 생성자 주입을 할 수 밖에 없다
public class ApplicationTests {
	
	//여기는 autowired 밖에 사용못함
	@Autowired
	private BoardController controller;
	
	@BeforeAll
	public static void startTest() {
		System.out.println("창이를 시작합니다.");
	}
	
	@Test
	public void contextLoads() {
		Board b = new Board(1, "test....", "user01", "건강최고", "....test", 0, 
					new Date(new java.util.Date().getTime()), 
					new Date(new java.util.Date().getTime()), "Y", 1);
		
		// assertEquals() : 두 값을 비교해서 일치 여부 판단
		assertEquals("redirect:/board/1/1", controller.updateBoard(b, 1));
	}
	
	//assertArrayEquals() : 두 배열을 비교하여 일치 여부 판단
	//assertNotNull() : assertNull() : 객체의 null 여부 확인
	//assertTrue() / assertFalse() : 특정 조건이 true인지 false 인지 판단
	
	@Test
	public void test2() {
		assertEquals("", "");
	}
	
	@AfterAll
	public static void endTest() {
		System.out.println("창이를 종료합니다.");
	}
	
	//BeforeAll, AfterAll은 무조건 static이여야 한다

}