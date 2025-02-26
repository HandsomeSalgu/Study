package kh.springboot.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardMapper mapper;

	public int getListCount(int i) {
		return mapper.getListCount(i);
	}

	public ArrayList<Board> selectBoardList(PageInfo pi, int i) {
		int offset = (pi.getCurrentPage() - 1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectBoardList(i, rowBounds);	//첫번째는 보낼 인자, 두번째는 rowBounds
	}

	public int insertBoard(Board b) {
		return mapper.insertBoard(b);
	}

	public Board selectBoard(Board b) {
		Board board = mapper.selectBoard(b);

		if(board != null && !board.getBoardWriter().equals(b.getBoardWriter())) {
			int result = mapper.updateCount(b.getBoardId());
			if(result>0) {
				board.setBoardCount(board.getBoardCount()+1);
			}
		}
		
		return board;
	}

	public Board updateForm(int bId) {
		Board b = new Board();
		b.setBoardId(bId);
		return b = mapper.selectBoard(b);
	}

	public int updateBoard(Board b) {
		return mapper.updateBoard(b);
	}

	public int deleteBoard(int bId) {
		return mapper.deleteBoard(bId);
	}

	public ArrayList<Attachment> selectAttmBoardList(Integer bId) {
		return mapper.selectAttmBoardList(bId);
	}

	public int insertAttm(ArrayList<Attachment> list) {
		return mapper.insertAttm(list);
	}

	public int deleteAttm(ArrayList<String> delRename) {
		return mapper.deleteAttm(delRename);
	}

	public void updateAttmLevel(int boardId) {
		mapper.updateAttmLevel(boardId);
		
	}

	public ArrayList<Board> selectTop() {
		return mapper.selectTop();
	}

	public ArrayList<Reply> selectReplyList(int bId) {
		return mapper.selectReplyList(bId);
	}

	public int insertReply(Reply r) {
		return mapper.insertReply(r);
	}

	public int deleteReply(int replyId) {
		return mapper.deleteReply(replyId);
	}

	public int updateReply(Reply r) {
		return mapper.updateReply(r);
	}

	public ArrayList<Board> selectRecentBoards() {
		return mapper.selectRecentBoards();
	}

//	public int statusNAttm(int bId) {
//		return mapper.statusNAttm(bId);
//	}

}
