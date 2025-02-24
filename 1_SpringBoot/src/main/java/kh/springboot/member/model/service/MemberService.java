package kh.springboot.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import kh.springboot.member.model.mapper.MemberMapper;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberMapper mapper;

	public Member login(Member m) {
		return mapper.login(m);
	}

	public int insertMember(Member m) {
		return mapper.insertMember(m);
	}

	public ArrayList<HashMap<String, Object>> selectMyList(String id) {
		return mapper.selectMyList(id);
	}

	public int updateMember(Member m) {
		return mapper.updateMember(m);
	}

	public int updatePwd(Member m) {
		return mapper.updatePwd(m);
	}

	public int deleteUser(String id) {
		return mapper.deleteUser(id);
	}

	public int checkValue(HashMap<String, String> map) {
		return mapper.checkValue(map);
	}

	public int updateProfile(HashMap<String, String> map) {
		return mapper.updateProfile(map);
	}

	public ArrayList<TodoList> selectTodoList(String id) {
		return mapper.selectTodoList(id);
	}

	public int insertTodo(TodoList todo) {
		return mapper.insertTodo(todo);
	}

	public int updateTodo(TodoList todo) {
		return mapper.updateTodo(todo);
	}

	public int ldelete(TodoList todo) {
		return mapper.ldelete(todo);
	}

}
