package org.upyog.chb.constants;

import java.math.BigDecimal;

/**
 * This class defines constants used throughout the Community Hall Booking module.
 * 
 * Purpose:
 * - To centralize all constant values for better maintainability and readability.
 * - To avoid hardcoding values directly in the codebase.
 * 
 * Categories of Constants:
 * 1. User Messages:
 *    - Messages displayed to users for various actions like booking creation, updates, etc.
 * 
 * 2. JSON Path Codes:
 *    - Paths used for extracting data from MDMS (Master Data Management System) responses.
 * 
 * 3. Master Data Keys:
 *    - Keys used to identify specific master data modules or fields in the MDMS.
 * 
 * Usage:
 * - These constants are referenced across the application wherever the corresponding
 *   values are required, ensuring consistency and reducing duplication.
 */

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
	public static final String DUPLICATE_DOCUMENT_UPLOADED = "DUPLICATE_DOCUMENT_UPLOADED";
	public static final String MULTIPLE_HALL_CODES_ERROR = "MULTIPLE_HALL_CODES_ERROR";
	public static final String EMPTY_DOCUMENT_ERROR = "EMPTY_DOCUMENT_ERROR";
	public static final String INVALID_BOOKING_DATE = "INVALID_BOOKING_DATE";
	public static final String INVALID_BOOKING_DATE_RANGE= "INVALID_BOOKING_DATE_RANGE";
	
	
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
	
	public static final String SERVICE = "service";
	
	public static final String BILLING_SERVICE = "BillingService";
	
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
	
	public static final String SECURITY_DEPOSIT = "SECURITY_DEPOSIT";
	
	
	public static final String CHB_APPLICANT_DETAIL_ENCRYPTION_KEY = "CHBApplicantDetail";
	
	public static final String CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY = "CHBApplicantDetailDecrypDisabled";

	public static final String CHB_APPLICANT_DETAIL_PLAIN_DECRYPTION_PURPOSE = "CHBApplicantDetailDecryptionDisabled";
	
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	// Workflow related constants

	public static final String CHB_STATUS_BOOKED = "BOOKED";

	public static final String CHB_ACTION_MOVETOEMPLOYEE = "MOVETOEMPLOYEE";

	public static final String CHB_REFUND_WORKFLOW_BUSINESSSERVICE = "booking-refund";

	public static final String CHB_REFUND_WORKFLOW_MODULENAME = "chb-services";

	public static final String CHB_TENANTID = "pg";
		
    public static final String NOTIFICATION_PAY_NOW = "PAY NOW";
	
	public static final String NOTIFICATION_DOWNLOAD_RECEIPT = "Download Receipt";
	
	public static final String NOTIFICATION_ACTION = "{Action Button}";
	
	public static final String NOTIFICATION_ACTION_BUTTON = "{/Action Button}";
	
	public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";
	
	public static final String MESSAGE_TEXT = "MESSAGE_TEXT";

}
