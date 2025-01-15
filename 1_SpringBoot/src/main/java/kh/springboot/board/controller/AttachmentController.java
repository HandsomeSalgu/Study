package kh.springboot.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	@PostMapping("upForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board board = new Board();
		board.setBoardId(bId);
		Board b = bService.selectBoard(board);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		
		model.addAttribute("b",b).addAttribute("list", list).addAttribute("page", page);
		return "views/attm/edit";
	}
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page, 
							  @RequestParam("deleteAttm") String[] deleteAttm, @RequestParam("file") ArrayList<MultipartFile> files) {
		
		System.out.println(b);
		System.out.println(Arrays.toString(deleteAttm) + deleteAttm.length);
		for(MultipartFile mf : files) {
			System.out.println("fileName : " + mf.getOriginalFilename());
		}
		
		/*
		 * 	1. 새파일 o
		 * 		(1)기존 파일 모두 삭제	--> 기존 파일 모두 삭제 & 새 파일 저장
		 * 								새 파일 중에서 level 0, 1 저장
		 * 
		 * 								Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011412450233877958.png/0, 2025011412450234184796.jpg/1, 2025011412450235084074.txt/1]
										fileName : 1280.jpg
										
		 * 		(2)기존 파일 일부 삭제	--> 기존 파일 일부 삭제 & 새 파일 저장
		 * 								삭제할 파일의 level 검사 후
		 * 								level이 0인 파일이 삭제되면 다른 기존 파일의 레벨을 0으로 재지정
		 * 								새파일의 레벨은 모두 1로 지정
		 * 								
		 * 								썸네일 미삭제
		 * 								Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011412450233877958.png/0, , ]
										fileName : 1280.jpg
										
										썸네일 삭제
										Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011412450233877958.png/0, 2025011412450234184796.jpg/1, ]
										fileName : 1280.jpg

		 * 		(3)기존 파일 모두 유지	--> 새 파일 저장
		 * 								새파일의 레벨은 모두 1로 지정
		 * 								
		 * 								Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[, , ]
										fileName : 1280.jpg

		 * 
		 * 	2. 새파일x
		 * 		(1)기존 파일 모두 삭제
		 * 							--> 기존 파일 모두 삭제
		 * 								일반 게시판으로 이동 : board_type = 1
		 * 
		 * 								Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[2025011412450233877958.png/0, 2025011412450234184796.jpg/1, 2025011412450235084074.txt/1]
										fileName : 
		 * 
		 * 		(2)기존 파일 일부 삭제	--> 기존 파일 일부 삭제
		 * 								삭제할 파일의 level 검사 후 level이 0인 파일이 삭제되면
		 * 
		 * 								Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[, 2025011412450234184796.jpg/1, ]
										fileName : 
		 * 
		 * 								다른 기존 파일의 레벨을 0으로 재지정
		 * 		(3)기존 파일 모두 유지	--> board만 수정
		 * 
		 * 								Board(boardId=30, boardTitle=개구리는 귀여워, boardWriter=null, nickName=null, boardContent=개구리 만세, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
										[, , ]
										fileName :
		 * 
		 */
		
		b.setBoardType(2);
		
		//새로 넣는 파일이 있다면 list에 옮겨담기
		ArrayList<Attachment> list = new ArrayList<>();
		
		for(int i = 0; i<files.size(); i++) {
			MultipartFile upload = files.get(i);
			
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload);
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					a.setRefBoardId(b.getBoardId());
					
					list.add(a);
				}
			}
		}
		
		System.out.println(Arrays.toString(deleteAttm) + deleteAttm.length);
		
		
		// 삭제할 파일이 있다면 삭제할 파일의 이름과 레벨을 가각 delRename과 delLevel에 옮겨담기
		ArrayList<String> delRename = new ArrayList<>();
		ArrayList<Integer> delLevel = new ArrayList<>();

		for(String rename : deleteAttm) {
			if(!rename.equals("")) {
				String[] split = rename.split("/");
				delRename.add(split[0]);
				delLevel.add(Integer.parseInt(split[1]));
			}
		}
		
		
		
		int deleteAttmResult = 0;		// 파일 delete 후 결과 값
		int updateBoardResult = 0;		// 게시글 update 후 결과 값
		boolean existBeforeAttm = true;	// 이전 첨부파일이 존재하는지에 대한 여부
		
		if(!delRename.isEmpty()) { //저장했던 파일 중 하나라도 삭제하겠다고 한 경우
			deleteAttmResult = bService.deleteAttm(delRename);
			if(deleteAttmResult > 0 ) {
				for(String rename : delRename) {
					deleteFile(rename);
				}
			}
			
			if(deleteAttm.length != 0 && delRename.size() == deleteAttm.length) {	// 기존 파일을 모두 삭제
				existBeforeAttm = false;
				if(list.isEmpty()) {
					b.setBoardType(1);
				}
			}else {
				for(int level : delLevel) {
					if(level == 0) {
						bService.updateAttmLevel(b.getBoardId());
						break;
					}
				}
			}
		}
		
		
		
		for(int i = 0 ; i<list.size(); i++) {
			Attachment a = list.get(i);
			if(existBeforeAttm) {
				a.setAttmLevel(1);
			}else {
				if(i == 0) {
					a.setAttmLevel(0);
				}else {
					a.setAttmLevel(1);
				}
			}
		}
		
		updateBoardResult = bService.updateBoard(b);
		
		int updateAttmResult = 0;
		
		if(!list.isEmpty()) {
			updateAttmResult = bService.insertAttm(list);
		}
		
		if(updateBoardResult + updateAttmResult == list.size() +1) {
			if(!existBeforeAttm && updateAttmResult == 0) {
				return "redirect:/board/list";
			}else {
				return String.format("redirect:/attm/%d/%d", b.getBoardId(), page);
			}
		}else {
			throw new BoardException("첨부파일 게시글 수정을 실패하였습니다.");
		}
		
		
	}
	
//	@PostMapping("delete")
//	public String deleteBoard(@RequestParam("boardId") int bId) {
//		int result1 = bService.deleteBoard(bId);
//		int result2 = bService.statusNAttm(bId);
//		
//		if(result1 > 0 && result2 > 0) {
//			return "redirect:/attm/list";
//		}else {
//			throw new BoardException("첨부파일 게시글 삭제를 실패하였습니다.");
//		}
//	}
	
	
	
	
	
	
}
