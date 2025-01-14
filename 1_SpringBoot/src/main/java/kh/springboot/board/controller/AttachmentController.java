package kh.springboot.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attm")
@RequiredArgsConstructor
public class AttachmentController {

	private final BoardService bService;
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
		
		
		int listCount = bService.getListCount(2);
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9); 
		
		ArrayList<Board> bList = bService.selectBoardList(pi, 2);
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null);
		
		
		
		if(bList != null) {
			model.addAttribute("bList", bList).addAttribute("aList", aList).addAttribute("pi", pi).addAttribute("loc", request.getRequestURI());
			return "views/attm/list";
		}else {
			throw new BoardException("첨부파일 게시글 조회를 실패하였습니다");
		}
	}
	
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	@PostMapping("insert")
	@Transactional	//모든 게 다 잘 됐을 때, 즉 아무런 이상이 없으면 commit, 한개라도 문제가 있으면 rollback
	public String insertAttmBoard(@ModelAttribute Board b, @RequestParam("file") ArrayList<MultipartFile> files, HttpSession session) {
																	//파일 관련은 무조건 MultipartFile로 해야된다
		System.out.println("초기 b : " + b);
		
		String id = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardWriter(id);
		
		ArrayList<Attachment> list = new ArrayList<>(); // view에서 넘긴 파일 정보
		for(int i =0; i<files.size();i++) {
			MultipartFile upload = files.get(i);
//			System.out.println(upload.getOriginalFilename());
			if(!upload.getOriginalFilename().equals("")) {
			//upload.isEmpty()는 내용이 아무것도 없는 파일도 올리고 싶은데, 얘는 다 거르기 때문에 사용하지 않는다
				String[] returnArr = saveFile(upload);// 첨부파일 폴더에 파일 저장
				if(returnArr[1] != null) {
					//혹시라도 에러났을 때, null일 때 사용하기 위해
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					
					list.add(a);
				}
			}
		}
		
		for(int i = 0; i<list.size(); i++) {
			Attachment a = list.get(i);
			if(i == 0) {
				a.setAttmLevel(0);
			}else{
				a.setAttmLevel(1);
			}
		}
		
		System.out.println(list);
		
		int result1 = 0;
		int result2 = 0;
		
		if(list.isEmpty()) {
			b.setBoardType(1);
			result1 = bService.insertBoard(b);
		}else {
			b.setBoardType(2);
			result1 = bService.insertBoard(b);
			System.out.println("insert 후 b : " + b);
			
			for(Attachment a : list) {
				a.setRefBoardId(b.getBoardId());
			}
			
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() +1) {
			if(result2==0) {
				return "redirect:/board/list";
			}else {
				return "redirect:/attm/list";
			}
		}else {
			for(Attachment a:list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 작성을 실패하였습니다.");
		}
	}
	
	public void deleteFile(String renameName) {
		String SavePath = "d:\\dev\\uploadFiles";
		
		File f = new File(SavePath + "\\" + renameName);
		if(f.exists()) {
			f.delete();
		}
	}
	

	public String[] saveFile(MultipartFile upload) {
		String savePath = "d:\\dev\\uploadFiles";
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		//폴더 명이 없으면 폴더 명 생성
		
		// 같은 폴더에 같은 이름의 파일이 저장되지 않도록 rename -> 년월일시분초밀리랜덤수.확장자
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//날짜를 원하는 대로 가져오고 싶을 때 사용
		int ranNum = (int)(Math.random()*100000);
		String originFileName = upload.getOriginalFilename();
		String renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
		//lastIndexOf를 쓰는 이유
		// apple.png면 상관없지만, a.pp.le.png라고 할 수도 있기 때문에 마지막 확장자를 붙여줘야 때문이다
//		System.out.println(renameFileName);
		
		String renamePath = folder + "\\" + renameFileName;
		try {
			upload.transferTo(new File(renamePath));
		} catch (IllegalStateException | IOException e) {
			System.out.println("파일 전송 에러 : " + e.getMessage());
		}
		
		String[] returnArr = new String[2];
		returnArr[0] = savePath;
		returnArr[1] = renameFileName;
		
		return returnArr;
	}
	
	@GetMapping("/{id}/{page}")
	public ModelAndView selectAttachmentBoard(@PathVariable("id") int bId, @PathVariable("page") int page
									  ,HttpSession session, ModelAndView mv) {
		
		String id = null;
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			id = loginUser.getId();
		}
		
		Board board = new Board();
		board.setBoardId(bId);
		board.setBoardWriter(id);
		
		Board b = bService.selectBoard(board);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		
		if(b != null) {
			mv.addObject("board", b).addObject("page", page).addObject("list", list).setViewName("views/attm/detail");
			return mv;
		}else {
			throw new BoardException("첨부파일 게시글 상세보기를 실패하였습니다.");
		}
		
		
	}
	
	
	
}
