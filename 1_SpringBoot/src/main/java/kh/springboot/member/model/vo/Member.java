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
public class Member {
	private String id;
	private String pwd;
	private String name;
	private String nickName;
	private String email;
	private String gender;
	private int age;
	private String phone;
	private String address;
	private Date enrollDate;
	private Date updateDate;
	private String memberStatus;
	private String isAdmin;
}
