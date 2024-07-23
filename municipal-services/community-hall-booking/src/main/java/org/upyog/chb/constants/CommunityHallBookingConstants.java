package org.upyog.chb.constants;

import java.math.BigDecimal;

public class CommunityHallBookingConstants {


	public static final String COMMUNITY_HALL_BOOKING_INIT_CREATED = "Your community hall booking details saved successfully";
	
	public static final String COMMUNITY_HALL_BOOKING_CREATED = "Your community hall booking created successfully";
	
	public static final String COMMUNITY_HALL_BOOKING_UPDATED = "Your community hall booking updated successfully";

	public static final String COMMUNITY_HALL_BOOKING_LIST = "Your community hall booking list fetched successfully";
	
	public static final String COMMUNITY_HALL_AVIALABILITY_SEARCH = "Hall availability details fetched sussessfully";
	
	public static final String COMMUNITY_HALL_DEMAND_ESTIMATION = "Hall booking amount estimation details fetched sussessfully";

	public static final String INVALID_TENANT = "INVALID TENANT";

	// mdms path codes
	public static final String CHB_JSONPATH_CODE = "$.MdmsRes.CHB";
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";

	public static final String COMMON_MASTERS_MODULE = "common-masters";

	public static final String CHB_PURPOSE = "Purpose";
	public static final String CHB_SPECIAL_CATEGORY = "SpecialCategory";
	public static final String CHB_CALCULATION_TYPE = "CalculationType";
	public static final String CHB_COMMNUITY_HALLS = "CommunityHalls";
	public static final String CHB_HALL_CODES = "HallCode";
	
	//Check what is the role of common fields config
	public static final String CHB_DOCUMENTS = "Documents";
	
	

	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
	public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenant";

	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String CITIZEN = "CITIZEN";
	public static final String INVALID_SEARCH = "INVALID SEARCH";
	public static final String CHB_WORKFLOW_ERROR = "CHB_WORKFLOW_ERROR";
	
	
	// NOTIFICATION PLACEHOLDER
	public static final String NOTIFICATION_LOCALE = "en_IN";
	
	public static final String NOTIFICATION_MODULE_NAME = "rainmaker-chb-notification";

	public static final String APPLICANT_NAME = "{APPLICANT_NAME}";

	public static final String BOOKING_NO = "{BOOKING_NO}";

	public static final String COMMUNITY_HALL_NAME = "{COMMUNITY_HALL_NAME}";

	public static final String CHB_PERMISSION_LETTER_LINK = "{CHB_PERMISSION_LETTER_LINK}";

	public static final String CHB_PAYMENT_LINK = "{CHB_PAYMENT_LINK}";


	// Notification Enhancement
	public static final String CHANNEL_NAME_SMS = "SMS";

	public static final String CHANNEL_NAME_EVENT = "EVENT";

	public static final String CHANNEL_NAME_EMAIL = "EMAIL";

	public static final String MODULE = "module";

	public static final String ACTION = "action";

	public static final String CHANNEL_LIST = "channelList";

	public static final String CHANNEL = "Channel";

	public static final String LOCALIZATION_CODES_JSONPATH = "$.messages.*.code";

	public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages.*.message";

	// EVENT PAY
	public static final String EVENT_PAY_TENANTID = "$tenantId";

	public static final String EVENT_PAY_BUSINESSSERVICE = "$businessService";

	
	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";
	
	public static final String USREVENTS_EVENT_NAME = "CHB";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-CHB";
	
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	public static final String RENT_CALCULATION_TYPE = "RENT";
	
	public static final String CGST_CALCULATION_TYPE = "CGST";
	
	public static final String SGST_CALCULATION_TYPE = "SGST";
	

}
