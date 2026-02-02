package org.egov.user.web.contract;

import java.awt.image.BufferedImage;

import lombok.Data;

@Data
public class Captcha {
	
	private String captchaUuid;
	private String captcha;
}
