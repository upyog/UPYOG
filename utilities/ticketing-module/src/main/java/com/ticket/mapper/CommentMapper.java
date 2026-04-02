package com.ticket.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ticket.model.Comment;

public class CommentMapper implements RowMapper<Comment> {

	@Override
	public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
		Comment comment = new Comment();
		comment.setCommentId(rs.getInt("comment_id"));
		comment.setTktId(rs.getInt("tkt_id"));
		comment.setUserId(rs.getInt("user_id"));
		comment.setCommentDesc(rs.getString("comment_description"));
		comment.setDateTime(rs.getString("date_time"));
		comment.setStatus(rs.getInt("status"));
		comment.setUserName(rs.getString("user_name"));
		comment.setAttachment(rs.getString("attachment"));
		comment.setImageUrl(rs.getString("IMAGE_URL"));
		return comment;
	}
}
