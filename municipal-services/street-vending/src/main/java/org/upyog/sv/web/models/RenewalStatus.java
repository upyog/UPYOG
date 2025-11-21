package org.upyog.sv.web.models;

public enum RenewalStatus {

	ELIGIBLE_TO_RENEW, // when an application is eligible for renewal as set in scheduler
	RENEW_IN_PROGRESS, // when first draft call is made or straight to payment is clicked
	RENEW_APPLICATION_CREATED, // when renewed application is created and saved
	RENEWED;// when the application is renewed

}
