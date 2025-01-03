package kh.springboot.member.model.service;

import org.springframework.stereotype.Service;

import kh.springboot.member.model.mapper.MemberMapper;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberMapper mapper;

	public Member login(Member m) {
		return mapper.login(m);
	}

}
