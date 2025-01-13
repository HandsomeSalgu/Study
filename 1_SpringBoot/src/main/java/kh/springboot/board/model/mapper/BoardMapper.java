package kh.springboot.board.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;

@Mapper
public interface BoardMapper {

	int getListCount(int i);

	ArrayList<Board> selectBoardList(int i, RowBounds rowbounds);

	int insertBoard(Board b);

	Board selectBoard(Board b);

	int updateCount(int boardId);

	int updateBoard(Board b);

	int deleteBoard(int bId);

	ArrayList<Attachment> selectAttmBoardList();

	int insertAttm(ArrayList<Attachment> list);



}
