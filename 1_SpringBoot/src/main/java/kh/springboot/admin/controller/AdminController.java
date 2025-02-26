package kh.springboot.admin.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final BoardService bService;
	
	@GetMapping("home")
	public String moveToMainAdmin(Model model) {
		
		File f = new File("D:/logs/member");
		File[] files = f.listFiles();
		
		TreeMap<String, Integer> dateCount = new TreeMap<String, Integer>();
		BufferedReader br = null;
		//TreeMap을 정렬을 쓰기 위해 만든 것이다
		try {
			for(File file: files) {
				//System.out.println(file);
				br = new BufferedReader(new FileReader(file));
				String data;
				while((data = br.readLine()) != null) {
					//readLine()은 줄바꿈이 이뤄지기 전 한 줄을 읽는다
//					System.out.println(data);
					String date = data.split(" ")[0];
					if(dateCount.containsKey(date)) {
						dateCount.put(date, dateCount.get(date) + 1);
					}else {
						dateCount.put(date, 1);
					}
				}
			} 
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<Board> list = bService.selectRecentBoards();
		for(Board b : list) {
			System.out.println(b);
		}
		model.addAttribute("list", list);
		model.addAttribute("dateCount", dateCount);
		
		return "admin";
	}
}
