package kh.springboot.ajax.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@RestController // Controller + ResponseBody
@RequestMapping({"/member", "/board", "/admin"})
@SessionAttributes("loginUser")
@RequiredArgsConstructor
public class AjaxController {
	
	private final MemberService mService;
	private final BoardService bService;
	private final JavaMailSender mailSender;
	
	@GetMapping("checkValue")
	public int checkValue(@RequestParam("value") String value, @RequestParam("column") String column) {
		
		HashMap<String, String> map = new HashMap<>();
		map.put("value", value);
		map.put("column", column);
		
		int count = mService.checkValue(map);
		
		return count;
	}
	
	@PutMapping("profile")
	public int updateProfile(@RequestParam(value="profile", required=false) MultipartFile profile, Model model) {
		//System.out.println(profile);
		
		Member m = (Member)model.getAttribute("loginUser");
		
		String savePath = "d:\\dev\\profiles";
		File folder = new File(savePath);
		if(!folder.exists()) folder.mkdirs();	// if문이 한줄이면 중괄호 생략 가능 두줄이상부터는 중괄호 생략 불가능.. 근데 중괄호 없는 것보다 있는게 좋긴함
		
		if(m.getProfile() != null) {
			File f = new File(savePath + "\\" + m.getProfile());
			f.delete();
		}
		
		String renameFileName = null;
		if(profile != null) {
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*1000000);
		String originFileName = profile.getOriginalFilename();
		 renameFileName= sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
		
		try {
			profile.transferTo(new File(folder + "\\" + renameFileName));
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", m.getId());
		map.put("profile", renameFileName);
		
		int result = mService.updateProfile(map);
		if(result > 0) {
			m.setProfile(renameFileName);
			model.addAttribute("loginUser", m);
		}
		
		return result;
		
		// Ensure that the compiler uses the '-parameters' flag. 이런 오류가 뜨면?
		
	}
	
	@GetMapping("echeck")
	public String checkEmail(@RequestParam("email") String email) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		
		String subject = "[SpringBoot] 이메일 확인";
		String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br>";
		body += "<div style='border: 3px solid green; text-align: center; font-size: 15px;>본 메일은 이메일을 확인하기 위해 발송되었습니다.<br>";
		body +="아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br><br>";
		
		String random ="";
		for(int i = 0; i<5; i++) {
			random += (int)(Math.random() * 10);
		}
		
		body += "<span style='font-size: 30px; text-decoration: underline;'><b>" + random + "</b></span><br></div>";
		
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		
		try {
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(body, true);
			mimeMessageHelper.setTo(email);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mailSender.send(mimeMessage);
		return random;
	}

	@PostMapping("list")
	public int insertTodo(@ModelAttribute TodoList todo) {
		return mService.insertTodo(todo);
	}
	
	@PutMapping("list")
	public int updateTodo(@ModelAttribute TodoList todo) {
		return mService.updateTodo(todo);
	}
	
	@DeleteMapping("list")
	public int ldelete(@ModelAttribute TodoList todo) {
		return mService.ldelete(todo);
	}	
	
	@GetMapping("top")	
	public ArrayList<Board> selectTop(HttpServletResponse response) {
		// HttpMessageConverter : body에다가 메세지를 전달
		// 기본 문자 : StringHttpMessageConverter
		// 기본 객체 : MappingJackson2HttpMessageConverter -> application/json
		// responsebody를 사용했다는 가정하에 return을 list로만 해줘도 GSON을 사용하는 것이다
		ArrayList<Board> list = bService.selectTop();
		return list;
	}
	
	@PostMapping(value="reply")
	public ArrayList<Reply> insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
		int result = bService.insertReply(r);
		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
		
		return list;
	}
	
	@DeleteMapping("reply")
	public int deleteReply(@ModelAttribute Reply r) {
		
		int result = bService.deleteReply(r.getReplyId());
		
		return result;
		
	}
	
	@PutMapping("reply")
	public int updateReply(@ModelAttribute Reply r) {
		
		int result = bService.updateReply(r);
		
		return result;
		
	}
	
	//fetch 같은 경우에는 @RequestParam을 쓸 수가 없다
	//JSON으로 보내기 때문에 @RequestBody로 받아야 된다!
	// 이게 지금 무슨 말이야 이거 아니야 열심히 설명했더니 지금 이런 식으로 써놨어? -신우 쌤 ㅎㅎ-
	@PutMapping("members")
	public int updateMember(@RequestBody HashMap<String, String> map) {
		System.out.println(map);
		
		if(map.get("column").equals("NICKNAME")) {
			int count = mService.checkValue(map);
			if(count != 0) {
				return -1;
			}
		}else if(map.get("column").equals("STATUS") || map.get("column").equals("ADMIN")) {
			map.put("column", map.get("column").equals("STATUS") ? "member_status" : "is_admin");
		}
		
		return mService.updateMemberItem(map);
	}
	
	@PutMapping("status")
	public int updateBoard(@RequestBody HashMap<String, String> map) {
		return bService.updateBoardStatus(map);
	}
	
   @GetMapping("weather")
      public String getWeather() {
         StringBuilder sb = new StringBuilder();
         try {
           StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /* URL */
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=njBDcLFRnehOXEyLQ%2Bqu1oPuNvu2WWvS9dcTeGtjuGO1M530dZo9%2FZGMVabsOAGKSRec15TVpnT8tHtf%2FId%2Brw%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 페이지번호 */
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /* 한 페이지 결과 수 */
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /* 요청자료형식(XML/JSON) Default: XML */
            
            //오늘 날짜 형식과 시간
            //- Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm");
            String now = sdf.format(new Date());
            String[] dayTime = now.split(" ");//띄어쓰기로 구분
            
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(dayTime[0], "UTF-8")); /* ‘21년 6월 28일발표 */
            
            //발표시간
            int[] baseTime = {200, 500, 800, 1100, 1400, 1700, 2000, 2300};
            int index = 99;//전의 index를 가져오기 위해
            for(int i = 0; i < baseTime.length; i++) {
               if(Integer.parseInt(dayTime[1]) < baseTime[i]) {
                  index = i - 1;
                  
                  if(i == 0) {
                     index = i;
                  }
                  
                  break;
               }
            }
            
            if(index == 99) {
               dayTime[1] = "2300";
            }
            
            dayTime[1] = ("0" + baseTime[index]).substring(("0" + baseTime[index]).length()-4);
            
            
            
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(dayTime[1], "UTF-8")); /* 05시 발표 */
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("60", "UTF-8")); /* 예보지점의 X 좌표값 */
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /* 예보지점의 Y 좌표값 */
           
            URL url = (new URI(urlBuilder.toString())).toURL();/* URL의 취소줄 : 메소드나 생성자가 제공되지 않음 */
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            //System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
               rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
               rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            
            String line;
            while ((line = rd.readLine()) != null) {
               sb.append(line);
            }
            rd.close();
            conn.disconnect();
            System.out.println(sb.toString());
         }catch(Exception e) {
            e.printStackTrace();
         }
         
         return sb.toString();
         
      }
}
