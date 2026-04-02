package com.ticket.model;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;
	private int commentId;
	private int tktId;
	private int userId;
	private String commentDesc;
	private String dateTime;
	private int status;
	private String userName;
	private MultipartFile commentFile;
	private String attachment;
	private int read_status;
	private String imageUrl;
	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Comment(int commentId, int tktId, int userId, String commentDesc, String dateTime, int status,
			String userName, MultipartFile commentFile, String attachment, int read_status, String imageUrl) {
		super();
		this.commentId = commentId;
		this.tktId = tktId;
		this.userId = userId;
		this.commentDesc = commentDesc;
		this.dateTime = dateTime;
		this.status = status;
		this.userName = userName;
		this.commentFile = commentFile;
		this.attachment = attachment;
		this.read_status = read_status;
		this.imageUrl = imageUrl;
	}
	@Override
	public String toString() {
		return "Comment [commentId=" + commentId + ", tktId=" + tktId + ", userId=" + userId + ", commentDesc="
				+ commentDesc + ", dateTime=" + dateTime + ", status=" + status + ", userName=" + userName
				+ ", commentFile=" + commentFile + ", attachment=" + attachment + ", read_status=" + read_status
				+ ", imageUrl=" + imageUrl + "]";
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public int getTktId() {
		return tktId;
	}
	public void setTktId(int tktId) {
		this.tktId = tktId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getCommentDesc() {
		return commentDesc;
	}
	public void setCommentDesc(String commentDesc) {
		this.commentDesc = commentDesc;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public MultipartFile getCommentFile() {
		return commentFile;
	}
	public void setCommentFile(MultipartFile commentFile) {
		this.commentFile = commentFile;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	public int getRead_status() {
		return read_status;
	}
	public void setRead_status(int read_status) {
		this.read_status = read_status;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	


	
}
