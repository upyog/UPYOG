// ignore_for_file: constant_identifier_names

class Url {
  static const String MDMS = 'egov-mdms-service/v1/_search';
  static const String LOCALIZATION = 'localization/messages/v1/_search';
  static const String GRIEVANCES = 'pgr-services/v2/request/_search';
  static const String TIMELINE_HISTORY =
      'egov-workflow-v2/egov-wf/process/_search';
  static const String FILES = 'filestore/v1/files';
  static const String FILES_URL = 'filestore/v1/files/url';
  static const String TL_SERVICES = 'tl-services/v1/_search';
  static const String PROPERTY = 'property-services/property/_search';
  static const String FETCH_BILL = 'billing-service/bill/v2/_fetchbill';
  static const String SEARCH_BILL = 'billing-service/bill/v2/_search';
  static const String DEMAND_BILL = 'billing-service/demand/_search';
  static const String CREATE_BILL = 'pg-service/transaction/v1/_create';
  static const String CITIZEN_PAYMENT_SUCCESS =
      'digit-ui/citizen/payment/success';
  static const String PAYMENT_UPDATE = 'pg-service/transaction/v1/_update';
  static const String COLLECTION = 'collection-services/payments/';
  static const String PT_MY_PAYMENTS =
      'collection-services/payments/PT/_search';
  static const String PT_MY_BILLS = 'collection-services/payments/PT/_search';
  static const String PDF_SERVICE = 'pdf-service/v1/_create';
  static const String BIRTH_URL =
      'birth-death-services/birth/_searchapplications';
  static const String DEATH_URL =
      'birth-death-services/death/_searchapplications';
  static const String WS_WC_SERVICES = 'ws-services/wc/_search';
  static const String SW_SWC_SERVICES = 'sw-services/swc/_search';
  static const String WATER_CONSUMPTION =
      'ws-calculator/meterConnection/_search';
  static const String BPA_URL = 'bpa-services/v1/bpa/_search';
  static const String BPAREG_URL = 'tl-services/v1/BPAREG/_search';
  static const String EDCR_URL = 'edcr/rest/dcr/scrutinydetails';
  static const String BPA_NOC_DETAIL = 'noc-services/v1/noc/_search';
  static const String COMPARISON = 'edcr/rest/dcr/occomparison';
  static const String NOTIFICATION_SEARCH = 'egov-user-event/v1/events/_search';
  static const String NOTIFICATION_UPDATE =
      'egov-user-event/v1/events/lat/_update';
  static const String NOTIFICATION_COUNT =
      'egov-user-event/v1/events/notifications/_count';
  static const String FIRE_NOC = 'firenoc-services/v1/_search';
  static const String BILL_GENIE =
      'egov-searcher/bill-genie/mcollectbills/_get';
  static const String FSM = 'fsm/v1/_search';
  /* -------------------------------------------------------------------------- */
  /*                                For employee                                */
  /* -------------------------------------------------------------------------- */
  static const String INBOX_URL = 'inbox/v1/_search';
  static const String EMP_TL_ACTION_UPDATE = 'tl-services/v1/_update';
  static const String EMP_WS_ACTION_UPDATE = 'ws-services/wc/_update';
  static const String EMP_SW_ACTION_UPDATE = 'sw-services/swc/_update';
  static const String BPA_ACTION_UPDATE = 'bpa-services/v1/bpa/_update';
  static const String NOC_ACTION_UPDATE = 'firenoc-services/v1/_update';
  static const String WORKFLOW_BUSINESS_SERVICES =
      'egov-workflow-v2/egov-wf/businessservice/_search';
  static const String EMP_PT_UPDATE = 'property-services/property/_update';
  static const String EGOV_HRMS = 'egov-hrms/employees/_search';
  static const String EMP_UPDATE_PASSWORD = 'user/password/_update';

  static const String LOCALITY_URL =
      'egov-location/location/v11/boundarys/_search';

  static const String WS_ESTIMATE = 'ws-calculator/waterCalculator/_estimate';
  static const String SW_ESTIMATE =
      'sw-calculator/sewerageCalculator/_estimate';

  //Challans
  static const String CHALLAN_CREATE_URL = 'echallan-services/eChallan/v1/_create';

  //EMP-FireNoc
  static const String EMP_FIRE_NOC_LOCALITY = 'egov-searcher/locality/fireNoc/_get';
}

class UserUrl {
  static const String USER_OTP = 'user-otp/v1/_send';
  static const String CREATE_USER = 'user/citizen/_create';
  static const String AUTHENTICATE = 'user/oauth/token';
  static const String USER_PROFILE = 'user/_search';
  static const String USER_PROFILE_UPDATE = 'user/profile/_update';
}
