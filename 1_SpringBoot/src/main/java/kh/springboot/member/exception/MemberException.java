package kh.springboot.member.exception;

public class MemberException extends RuntimeException {
	//RuntimeExcption 예외처리를 굳이 하지 않아도 되게 만들어준다
	public MemberException() {}
	public MemberException(String msg) {
		super(msg);
	}
}
