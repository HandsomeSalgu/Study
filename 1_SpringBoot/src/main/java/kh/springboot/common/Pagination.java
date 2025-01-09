package kh.springboot.common;

import kh.springboot.board.model.vo.PageInfo;

public class Pagination {
	public static PageInfo getPageInfo(int currentPage, int listCount, int boardLimit) {
		
		//일반게시물 같은 경우 5개씩 게시글을 나타게 할려고 한다
		//첨부파일게시물은 9개씩 보여줄려고 한다
		//그래서 게시물 종류에 따른 boardLimit를 5라고 넘길건지 9라고 넘길건지 결정하기 위해 
		int pageLimit = 10;
		
		int maxPage = (int)Math.ceil((double)listCount / boardLimit);
		
		int startPage = (currentPage - 1) / pageLimit * pageLimit + 1;
		
		int endPage = startPage + pageLimit - 1;
		if(maxPage < endPage) {
			endPage = maxPage;
		}
		return new PageInfo(currentPage, listCount, pageLimit, maxPage, startPage, endPage, boardLimit);
	}
}
