package com.biz.bbs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.biz.bbs.domain.CommentVO;

public interface CommentDao {

	@Select("SELECT * FROM tbl_cmt WHERE cmt_p_id =#{cmt_p_id}")
	public List<CommentVO> selectAll(long cmt_p_id);
	
}
