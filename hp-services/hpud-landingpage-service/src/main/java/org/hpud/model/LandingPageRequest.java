package org.hpud.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LandingPageRequest {
	
	private String id;
	private int count;
	private String browserName;
	private String ipAddress;
	private String status;
	private String msg;

}
