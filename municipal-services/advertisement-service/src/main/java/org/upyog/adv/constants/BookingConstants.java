/**
 * This class defines constants used throughout the Advertisement Booking Service.
 * These constants include success messages, error messages, MDMS JSON paths,
 * and keys for encryption. Using constants ensures consistency and avoids
 * hardcoding values directly in the code.
 */
package org.upyog.adv.constants;

import java.math.BigDecimal;

public class BookingConstants {
	public static final String BOOKING_CREATED = "Your advertisement booking created successfully";
	public static final String ADVERTISEMENT_BOOKING_LIST = "Your advertisement booking list fetched successfully";

	public static final String INVALID_TENANT = "INVALID TENANT";

	public static final String ADD_TYPE = "AdType";

	public static final String FACE_AREA = "FaceArea";

	public static final String LOCATION = "Location";

	public static final String DOCUMENTS = "Documents";

	public static final String COMMON_MASTERS_MODULE = "common-masters";

	public static final String ADVERTISEMENT_AVAILABILITY_SEARCH = "Advertisement booking availability details fetched sussessfully";

	// mdms json paths
	public static final String ADV_JSONPATH_CODE = "$.MdmsRes.Advertisement";
	public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";

	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
	public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenant";
	public static final String INVALID_SEARCH = "INVALID SEARCH";
	public static final String MULTIPLE_HALL_CODES_ERROR = "MULTIPLE_HALL_CODES_ERROR";
	public static final String EMPTY_DOCUMENT_ERROR = "EMPTY_DOCUMENT_ERROR";
	public static final String INVALID_BOOKING_DATE = "INVALID_BOOKING_DATE";
	public static final String INVALID_BOOKING_DATE_RANGE = "INVALID_BOOKING_DATE_RANGE";
	public static final String DUPLICATE_DOCUMENT_UPLOADED = "DUPLICATE_DOCUMENT_UPLOADED";

	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String CITIZEN = "CITIZEN";

	public static final String ADVERTISEMENT_BOOKING_UPDATED = "Your advertisement booking updated successfully";

	public static final String ADV_APPLICANT_DETAIL_ENCRYPTION_KEY = "ADVApplicantDetail";

	public static final String ADV_APPLICANT_DETAIL_PLAIN_DECRYPTION_KEY = "ADVApplicantDetailDecrypDisabled";

	public static final String ADV_APPLICANT_DETAIL_PLAIN_DECRYPTION_PURPOSE = "ADVApplicantDetailDecryptionDisabled";

	public static final String ADV_CALCULATION_TYPE = "CalculationType";

	public static final String ADV_TAX_AMOUNT = "TaxAmount";

	public static final String ADVERTISEMENT_DEMAND_ESTIMATION = "Advertisement booking amount estimation details fetched sussessfully";

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	public static final String BILLING_SERVICE = "BillingService";

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
	
	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";
	
	public static final String USREVENTS_EVENT_NAME = "ADV";

	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-ADV";
	
	// NOTIFICATION PLACEHOLDER
	public static final String NOTIFICATION_LOCALE = "en_IN";
	
	public static final String NOTIFICATION_MODULE_NAME = "rainmaker-adv-notification";
	
	public static final String NOTIFICATION_PAY_NOW = "PAY NOW";
	
	public static final String NOTIFICATION_DOWNLOAD_RECEIPT = "Download Receipt";
	
	public static final String NOTIFICATION_ACTION = "{Action Button}";
	
	public static final String NOTIFICATION_ACTION_BUTTON = "{/Action Button}";

	public static final String APPLICANT_NAME = "{APPLICANT_NAME}";

	public static final String BOOKING_NO = "{BOOKING_NO}";

	public static final String COMMUNITY_HALL_NAME = "{COMMUNITY_HALL_NAME}";

	public static final String CHB_PERMISSION_LETTER_LINK = "{CHB_PERMISSION_LETTER_LINK}";

	public static final String CHB_PAYMENT_LINK = "{CHB_PAYMENT_LINK}";
	
	public static final String BOOKING_CREATED_STATUS = "BOOKING_CREATED";
	
	public static final String BOOKED_STATUS = "BOOKED";
	
	public static final String DRAFT_DISCARDED = "Draft discarded";
	
	public static final String ACTIVE = "active";
	
	public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
	
	public static final String NOTIFICATION_EMAIL = "{EMAIL_ID}";


}
