package kh.springboot.board.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;

@Mapper
public interface BoardMapper {

	int getListCount(int i);

	ArrayList<Board> selectBoardList(int i, RowBounds rowbounds);

	int insertBoard(Board b);

	Board selectBoard(Board b);

	int updateCount(int boardId);

	int updateBoard(Board b);

	int deleteBoard(int bId);

	ArrayList<Attachment> selectAttmBoardList(Integer bId);

	int insertAttm(ArrayList<Attachment> list);

	int deleteAttm(ArrayList<String> delRename);

	void updateAttmLevel(int boardId);

	ArrayList<Board> selectTop();

	ArrayList<Reply> selectReplyList(int bId);

	int insertReply(Reply r);

	int deleteReply(int replyId);

	int updateReply(Reply r);

	ArrayList<Board> selectRecentBoards();

	int updateBoardStatus(HashMap<String, String> map);

	ArrayList<Attachment> selectAllAttm();

//	int statusNAttm(int bId);



}
