package org.egov.ptr.web.contracts.alfresco;

public class AADocumentDetails {
	public String id;
	private Attachment attachment;
	public String type;
	public String subject;
	public String dprId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDprId() {
		return dprId;
	}

	public void setDprId(String dprId) {
		this.dprId = dprId;
	}

}
