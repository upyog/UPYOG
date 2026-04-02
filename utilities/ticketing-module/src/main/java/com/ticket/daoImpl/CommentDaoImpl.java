package com.ticket.daoImpl;

import java.util.ArrayList;
import java.util.List;

import com.ticket.configuration.HibernateConfig;
import com.ticket.dao.CommentDao;
import com.ticket.mapper.CommentMapper;
import com.ticket.model.Comment;

public class CommentDaoImpl extends HibernateConfig implements CommentDao{

	@Override
	public List<Comment> getCommentDetails(int tktId) {
		List<Comment> list = new ArrayList<Comment>();
		try {
		String SQL = "SELECT tktCmt.*, tktU.user_name AS user_name FROM tkt_comment AS tktCmt INNER JOIN tkt_user AS tktU ON tktCmt.user_id=tktU.user_id WHERE tkt_id=?";
			list = getJdbcTemplate().query(SQL, new CommentMapper(), tktId);
			return list;
		}
		catch (Exception e) {
			return list;
		}
		
	}
	
	@Override
	public int addComment(Comment comment) {
		int result=0;
		try {
		String sql = "INSERT into tkt_comment (tkt_id, user_id, comment_description) VALUES (?, ?, ?)";
		result = getJdbcTemplate().update(sql, new Object[] {comment.getTktId(), comment.getUserId(), comment.getCommentDesc()});
		return result;
	}
		catch (Exception e) {
			return result;
		}
	}

	@Override
	public int addCommentWithAttachment(Comment comment) {
		int result=0;
		try {
		String sql = "INSERT into tkt_comment (tkt_id, user_id, comment_description, attachment,IMAGE_URL) VALUES (?, ?, ?, ?, ?)";
		result = getJdbcTemplate().update(sql, new Object[] {comment.getTktId(), comment.getUserId(), comment.getCommentDesc(), comment.getAttachment(), comment.getImageUrl()});
		return result;
	}
		catch (Exception e) {
			return result;
		}
	}
}
