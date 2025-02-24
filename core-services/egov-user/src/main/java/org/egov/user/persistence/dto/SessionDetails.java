package org.egov.user.persistence.dto;

import java.io.Serializable;

public class SessionDetails implements Serializable{
	
	private static final long serialVersionUID = 7094572260034458543L;

	private String id;
	private Long LastAccessedTime;
	private Long CreationTIme;
	private int MaxInactiveInterval;

	public SessionDetails(String id, Long lastAccessedTime, Long creationTIme, int maxInactiveInterval) {
		super();
		this.id = id;
		LastAccessedTime = lastAccessedTime;
		CreationTIme = creationTIme;
		MaxInactiveInterval = maxInactiveInterval;
	}

}
