package com.ticket.dao;

import java.util.List;

import com.ticket.model.Comment;

public interface CommentDao {

	public List<Comment> getCommentDetails(int tktId);

	public int addComment(Comment comment);
	
	public int addCommentWithAttachment(Comment comment);
	
	
}
