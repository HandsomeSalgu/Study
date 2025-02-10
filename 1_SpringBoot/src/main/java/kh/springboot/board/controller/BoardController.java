package kh.springboot.board.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
//RequestMapping({"/board", "/attm"}) 이라고 해도 되는데, 매번 if문으로 board일 때 attm인지를 나눠야 되기 때문에 상당히 번거로운 작업이 된다
public class BoardController {
	
	private final BoardService bService;
	
	@GetMapping("list")	//알아서 /를 붙여줘서 없어도 상관없다
	public String selectList(@RequestParam(value= "page", defaultValue="1") int currentPage, 
							 Model model, HttpServletRequest request) {
		int listCount = bService.getListCount(1);	//얘는 보드타입 1번인 일반 게시물을 가지고 온다는 인자
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);	//일반 게시물은 5개씩 보기 때문에 5라고 넣어준다
		ArrayList<Board> list = bService.selectBoardList(pi, 1);	//1은 보드 타입

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
	
	@GetMapping("/{id}/{page}")	//앞에는 id, 뒤에는 page라는 값을 뽑아오겠다
	public ModelAndView selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page,
							HttpSession session, ModelAndView mv) {
		// 글 상세 조회 + 조회수가 수정 되어야 한다
		//			   내가 내 글을 조회 or 비회원 조회 -> 조회수 올라가지 않음
		//			   ㄴ 현재 로그인한 사람의 아이디 필요
		
		// bId, id를 service에 넘겨서 글쓴이 비교 로직 작성
		
		// 게시글이 존재하면, 게시글 데이터(b)와 페이지(page)를 /views/board/detail.html로 전달
		// 															ㄴ write.html 수정해서 사용
		// 게시글이 존재하지 않으면, 사용자 정의 예외 발생
		Board board = new Board();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			board.setBoardWriter(loginUser.getId());
		}
		
		board.setBoardId(bId);

		Board b = bService.selectBoard(board);
		ArrayList<Reply> list = bService.selectReplyList(bId);
		
		
		if(b != null) {
			mv.addObject("list", list);
			mv.addObject("board", b).addObject("page", page).setViewName("detail");
			return mv;
		}else {
			throw new BoardException("게시글이 존재하지 않습니다");
		}
	}
	
	@PostMapping("upForm")
	public ModelAndView updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page,
							HttpSession session, ModelAndView mv) {

		Board b = bService.updateForm(bId);
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null && loginUser.getId().equals(b.getBoardWriter())) {
			mv.addObject("b", b).addObject("page", page).setViewName("views/board/edit");
			return mv;
		}else {
			throw new BoardException("수정 권한이 없습니다");
		}
	}
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page) {
		
	
		
		
			b.setBoardType(1);
			int result = bService.updateBoard(b);
			
			if(result>0) {
//				return "redirect:/board/" + b.getBoardId() + "/" + page;
				return String.format("redirect:/board/%d/%d", b.getBoardId(), page);
			}else {
				throw new BoardException("수정되지 않았습니다");
			}
		
		
	}
	
	@PostMapping("delete")
	public String deleteBoard(@RequestParam("boardId") int bId, HttpSession session, HttpServletRequest request) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null && loginUser.getId().equals((bService.updateForm(bId)).getBoardWriter())) {
			int result = bService.deleteBoard(bId);
			if(result > 0) {
//				return "redirect:/" + (request.getRequestURI().contains("board") ? "board" : "attm") + "/list";
				return "redirect:/" + (request.getHeader("referer").contains("board") ? "board" : "attm") + "/list";
				//getHeader는 전에 있던 url을 보는 것, getRequestURI는 현재 urI를 참고하기 때문에 board로 넘어갈 수 밖에 없다
			}else {
				throw new BoardException("삭제되지 않았습니다");
			}
		}else{
			throw new BoardException("삭제 권한이 없습니다");
		}
	}
	
//	//resoponse에 content파일을 관리할 수 있게 하는 속성
//	@GetMapping(value="top", produces = "application/json; charset=UTF-8")					
//	@ResponseBody
//	public String selectTop(/*HttpServletResponse response*/) {
//		ArrayList<Board> list = bService.selectTop();
//		
//		System.out.println(list);
//		
//		JSONArray array = new JSONArray();
//		for(Board b : list) {
//			JSONObject json = new JSONObject();
//			json.put("boardId", b.getBoardId());
//			json.put("boardTitle", b.getBoardTitle());
//			json.put("nickName", b.getNickName());
//			json.put("modifyDate", b.getModifyDate());
//			json.put("boardCount", b.getBoardCount());
//			
//			array.put(json);
//		}
//		
////		response.setContentType("application/json; charset=UTF-8");
//		
//		return array.toString();
//	}
	
	@GetMapping("top")
	public void selectTop(HttpServletResponse response) {
		ArrayList<Board> list = bService.selectTop();
		response.setContentType("application/json; charset=UTF-8");
//		Gson gson = new Gson();
//		GsonBuilder gb = new GsonBuilder();
//		GsonBuilder dfgb = gb.setDateFormat("yyyy-MM-dd");
//		Gson gson = dfgb.create();
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create();
		try {
			gson.toJson(list, response.getWriter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//JSON 버전
//	@GetMapping("rinsert")
//	@ResponseBody
//	public String insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
//		System.out.println(r);
//		int result = bService.insertReply(r);
//		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//			
//			response.setContentType("application/json; charset=UTF-8");
//			
//			JSONArray array = new JSONArray();
//			for(Reply reply : list) {
//				JSONObject json = new JSONObject();
//				json.put("replyContent", reply.getReplyContent());
//				json.put("nickName", reply.getNickName());
//				json.put("replyModifyDate", reply.getReplyModifyDate() + "");
//				
//				array.put(json);
//			}
//			
//			return array.toString();
//	}
	
	//GSON 버전
//	@GetMapping("rinsert")
//	public void insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
//		int result = bService.insertReply(r);
//		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
//		response.setContentType("application/json; charset=UTF-8");
//		
//		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
//		Gson gson = gb.create();
//		
//		try {
//			gson.toJson(list, response.getWriter());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	//jackson 버전
	@GetMapping(value="rinsert", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
		int result = bService.insertReply(r);
		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		
		ObjectMapper om = new ObjectMapper();
		
		om.setDateFormat(sdf);
		
		String strJson = null;
		try {
			strJson = om.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return strJson;
	}
	
	@GetMapping("rdelete")
	@ResponseBody
	public int deleteReply(@ModelAttribute Reply r) {
		
		int result = bService.deleteReply(r.getReplyId());
		
		return result;
		
	}
	
	@GetMapping("rupdate")
	@ResponseBody
	public int updateReply(@ModelAttribute Reply r) {
		
		int result = bService.updateReply(r);
		
		return result;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}