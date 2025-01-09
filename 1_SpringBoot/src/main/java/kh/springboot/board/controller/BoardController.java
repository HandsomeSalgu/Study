package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.exception.BoardException;
import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
	
	private final BoardService bService;
	
	@GetMapping("list")	//알아서 /를 붙여줘서 없어도 상관없다
	public String selectList(@RequestParam(value= "page", defaultValue="1") int currentPage, 
							 Model model, HttpServletRequest request) {
		int listCount = bService.getListCount(1);	//얘는 보드타입 1번인 일반 게시물을 가지고 온다는 인자
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);	//일반 게시물은 5개씩 보기 때문에 5라고 넣어준다
		ArrayList<Board> list = bService.selectBoardList(pi, 1);	//1은 보드 타입
		System.out.println(list);
		model.addAttribute("list",list).addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		// getRequestURI() : /board/list
		// getRequestURL() : http://localhost:8080/cuteChang/board/list
		return "list";
	}
	
	@GetMapping("write")
	public String writeBoard() {
		return "write";
	}
	
	@PostMapping("insert")
	public String insertBoard(@ModelAttribute Board b,
							  HttpSession session) {

		b.setBoardWriter(((Member)session.getAttribute("loginUser")).getId());
		b.setBoardType(1);

		int result = bService.insertBoard(b);
		if(result>0) {
			return "redirect:/board/list";
		}else {
			throw new BoardException("게시글이 등록되지 않았습니다");	
		}
		
	}
}