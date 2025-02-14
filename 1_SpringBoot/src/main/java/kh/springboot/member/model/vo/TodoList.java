package kh.springboot.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor //전체생성자
@NoArgsConstructor //기본생성자
@ToString
@Setter
@Getter
public class TodoList {
	private int todoNum;
	private String todo;
	private String writer;
	private String status;
	private String important;
}
