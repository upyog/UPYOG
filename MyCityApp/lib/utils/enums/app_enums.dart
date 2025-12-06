// ignore_for_file: constant_identifier_names

enum RequestType { GET, PUT, POST, DELETE }

enum ExceptionType {
  UNAUTHORIZED,
  BADREQUEST,
  INVALIDINPUT,
  FETCHDATA,
  OTHER,
  CONNECTIONISSUE,
  RUNTIMEERROR,
  SERVICEUNAVAILABLE
}

enum TradeAppType { NEW, RENEWAL, NONE }

extension TradeLicenseAppType on TradeAppType {
  static const actionName = {
    TradeAppType.NEW: 'NEW',
    TradeAppType.RENEWAL: 'RENEWAL',
    TradeAppType.NONE: 'NONE',
  };

  String get name => actionName[this]!;
}

enum BpaAppType {
  BUILDING_PLAN_SCRUTINY,
  BUILDING_OC_PLAN_SCRUTINY,
}

extension BpaAppTypeExt on BpaAppType {
  static const actionName = {
    BpaAppType.BUILDING_PLAN_SCRUTINY: "BUILDING_PLAN_SCRUTINY",
    BpaAppType.BUILDING_OC_PLAN_SCRUTINY: "BUILDING_OC_PLAN_SCRUTINY",
  };
  String get name => actionName[this]!;
}

enum NotificationType {
  SEARCH,
  UPDATE,
  COUNT,
}

extension NotificationTypeExtension on NotificationType {
  static const actionName = {
    NotificationType.SEARCH: "SEARCH",
    NotificationType.UPDATE: "UPDATE",
    NotificationType.COUNT: "COUNT",
  };
  String get name => actionName[this]!;
}

enum TimelineStateType {
  CLOSEDAFTERRESOLUTION,
  RESOLVED,
  PENDINGATLME,
  PENDINGFORASSIGNMENT,
}

enum WsStatus {
  DISCONNECTION_EXECUTED,
  CONNECTION_ACTIVATED,
  PENDING_FOR_DISCONNECTION_EXECUTION,
  PENDING_FOR_CONNECTION_ACTIVATION,
  APPROVED,
  PENDING_SANC_FEE_PAYMENT,
  APPROVAL_INPROGRESS,
  PENDING_FOR_PAYMENT,
  INACTIVE,
  ACTIVE,
  PENDING_APPROVAL_FOR_CONNECTION
}

extension WsStatusExtension on WsStatus {
  static const actionName = {
    WsStatus.DISCONNECTION_EXECUTED: 'DISCONNECTION_EXECUTED',
    WsStatus.CONNECTION_ACTIVATED: 'CONNECTION_ACTIVATED',
    WsStatus.PENDING_FOR_DISCONNECTION_EXECUTION:
        'PENDING_FOR_DISCONNECTION_EXECUTION',
    WsStatus.PENDING_FOR_CONNECTION_ACTIVATION:
        'PENDING_FOR_CONNECTION_ACTIVATION',
    WsStatus.APPROVED: 'APPROVED',
    WsStatus.PENDING_SANC_FEE_PAYMENT: 'PENDING_SANC_FEE_PAYMENT',
    WsStatus.APPROVAL_INPROGRESS: 'APPROVAL_INPROGRESS',
    WsStatus.PENDING_FOR_PAYMENT: 'PENDING_FOR_PAYMENT',
    WsStatus.INACTIVE: 'Inactive',
    WsStatus.ACTIVE: 'Active',
    WsStatus.PENDING_APPROVAL_FOR_CONNECTION: 'PENDING_APPROVAL_FOR_CONNECTION',
  };

  String get name => actionName[this]!;
}

enum BpaStatus {
  PENDING_FEE,
  CITIZEN_APPROVAL_INPROCESS,
  APPROVED,
  PENDING_APPL_FEE_PAYMENT,
  PENDING_SANC_FEE_PAYMENT,
  DOC_VERIFICATION_INPROGRESS,
  NOC_VERIFICATION_INPROGRESS,
  FIELD_INSPECTION_INPROGRESS,
  APPROVAL_INPROGRESS,
  PENDING_APPL_FEE,
  PENDING_SANC_FEE,
  INPROGRESS,
}

extension BpaStatusExtension on BpaStatus {
  static const actionName = {
    BpaStatus.PENDING_FEE: 'PENDING_FEE',
    BpaStatus.CITIZEN_APPROVAL_INPROCESS: 'CITIZEN_APPROVAL_INPROCESS',
    BpaStatus.APPROVED: 'APPROVED',
    BpaStatus.PENDING_APPL_FEE_PAYMENT: 'PENDING_APPL_FEE_PAYMENT',
    BpaStatus.PENDING_SANC_FEE_PAYMENT: 'PENDING_SANC_FEE_PAYMENT',
    BpaStatus.DOC_VERIFICATION_INPROGRESS: 'DOC_VERIFICATION_INPROGRESS',
    BpaStatus.NOC_VERIFICATION_INPROGRESS: 'NOC_VERIFICATION_INPROGRESS',
    BpaStatus.FIELD_INSPECTION_INPROGRESS: 'FIELDINSPECTION_INPROGRESS',
    BpaStatus.APPROVAL_INPROGRESS: 'APPROVAL_INPROGRESS',
    BpaStatus.PENDING_APPL_FEE: 'PENDING_APPL_FEE',
    BpaStatus.PENDING_SANC_FEE: 'PENDING_SANC_FEE',
    BpaStatus.INPROGRESS: 'INPROGRESS',
  };

  String get name => actionName[this]!;
}

enum FileExtType {
  pdf,
  dxf,
  png,
  jpg,
  jpeg,
  xml,
  docs,
  none,
}

enum BusinessService {
  TL,
  WS_ONE_TIME_FEE,
  SW_ONE_TIME_FEE,
  WS,
  SW,
  PT,
  OBPS,
  BPA_NC_SAN_FEE,
  BPA_NC_APP_FEE,
  BPA_LOW_RISK_PERMIT_FEE,
  PT_MUTATION,
  WS_RECONNECTION,
  SW_RECONNECTION,
  BPA_NC_OC_APP_FEE,
  FIRENOC,
  FSM_TRIP_CHARGES,

  //Challans
  GAS_BALLOON_ADVERTISEMENT,
  BURNING_OF_WASTE_CHALLAN_FEE,
  HOARDINGS,
  LIGHT_WALA_BOARD,
  UNIPOLLS,
  WALL_PAINT_ADVERTISEMENT,
  PARKING_FEE,
  SANTITATION_DUMPING_GARBAGE,
  COLLECTION_AND_DEMOLITION_WASTE_CHALLAN_FEE,

  //noc
  FIRE_NOC,
}

extension BusinessServiceExtension on BusinessService {
  static const actionName = {
    BusinessService.TL: 'TL',
    BusinessService.WS_ONE_TIME_FEE: 'WS.ONE_TIME_FEE',
    BusinessService.SW_ONE_TIME_FEE: 'SW.ONE_TIME_FEE',
    BusinessService.WS: 'WS', // Water Service
    BusinessService.SW: 'SW', // Sewerage Service
    BusinessService.PT: 'PT', // PropertyTax Service
    BusinessService.OBPS: 'OBPS', // Building Plan Service
    BusinessService.BPA_NC_SAN_FEE: 'BPA.NC_SAN_FEE', // Building Plan Service
    BusinessService.BPA_NC_APP_FEE: 'BPA.NC_APP_FEE', // Building Plan Service
    BusinessService.BPA_LOW_RISK_PERMIT_FEE:
        'BPA.LOW_RISK_PERMIT_FEE', // Building Plan Service
    BusinessService.PT_MUTATION: 'PT.MUTATION', // PropertyTax Service
    BusinessService.WS_RECONNECTION: 'WSReconnection', // Water Service
    BusinessService.SW_RECONNECTION: 'SWReconnection', // Sewerage Service
    BusinessService.BPA_NC_OC_APP_FEE: 'BPA.NC_OC_APP_FEE', // Sewerage Service
    BusinessService.FIRENOC: 'FIRENOC', // Sewerage Service
    BusinessService.FSM_TRIP_CHARGES: 'FSM.TRIP_CHARGES', // Sewerage Service

    //Challans
    BusinessService.GAS_BALLOON_ADVERTISEMENT: 'ADVT.Gas_Balloon_Advertisement',
    BusinessService.BURNING_OF_WASTE_CHALLAN_FEE:
        'CH.Burning_of_Waste_Challan_fee',
    BusinessService.HOARDINGS: 'ADVT.Hoardings',
    BusinessService.LIGHT_WALA_BOARD: 'ADVT.Light_Wala_Board',
    BusinessService.UNIPOLLS: 'ADVT.Unipolls',
    BusinessService.WALL_PAINT_ADVERTISEMENT: 'ADVT.Wall_Paint_Advertisement',
    BusinessService.PARKING_FEE: 'ADVT.Parking_Fee',
    BusinessService.SANTITATION_DUMPING_GARBAGE:
        'CH.Santitation_dumping_garbage',
    BusinessService.COLLECTION_AND_DEMOLITION_WASTE_CHALLAN_FEE:
        'CH.Collection_and_Demolition_Waste_Challan_fee',

    //noc
    BusinessService.FIRE_NOC: 'FIRENOC',
  };

  String get name => actionName[this]!;
}

enum PaymentStatus {
  DEPOSITED,
}

enum PdfKey {
  tlReceipt,
  tlCertificate,
  ptmutationCertificate,
  ws_sewerage_disconnection_notice,
  ws_onetime_receipt,
  building_permit,
  building_permit_low,
  bpa_receipt,
  occupancy_certificate,
  property_receipt,
  pt_recept,
  fsm_receipt,
  consolidated_receipt,
}

extension PdfKeyExtension on PdfKey {
  static const actionName = {
    PdfKey.tlReceipt: 'tradelicense-receipt',
    PdfKey.tlCertificate: 'tlcertificate',
    PdfKey.ptmutationCertificate: 'ptmutationcertificate',
    PdfKey.ws_sewerage_disconnection_notice: 'ws-seweragedisconnectionnotice',
    PdfKey.ws_onetime_receipt: 'ws-ws-onetime-receipt',
    PdfKey.building_permit: 'buildingpermit',
    PdfKey.building_permit_low: 'buildingpermit-low',
    PdfKey.bpa_receipt: 'bpa-receipt',
    PdfKey.occupancy_certificate: 'occupancy-certificate',
    PdfKey.property_receipt: 'property-receipt',
    PdfKey.pt_recept: 'pt-receipt',
    PdfKey.fsm_receipt: 'fsm-receipt',
    PdfKey.consolidated_receipt: 'consolidatedreceipt',
  };

  String get name => actionName[this]!;
}

enum SearchType {
  CONNECTION,
}

enum UserType { EMPLOYEE, CITIZEN }

extension UserTypeExtension on UserType {
  static const actionName = {
    UserType.EMPLOYEE: 'EMPLOYEE',
    UserType.CITIZEN: 'citizen',
  };

  String get name => actionName[this]!;
}

//Grievance status
enum GrievanceStatus {
  PENDING,
  PENDING_SUPERVISOR,
  RESOLVED,
  CLOSED_SOLUTION,
  CLOSED_REJECTION,
  REJECTED,
}

// PENDINGATSUPERVISOR

// const closedStatus = ["RESOLVED", "REJECTED", "CLOSEDAFTERREJECTION", "CLOSEDAFTERRESOLUTION"];

extension GrievanceStatusExtension on GrievanceStatus {
  static const actionName = {
    GrievanceStatus.PENDING: 'PENDINGFORASSIGNMENT',
    GrievanceStatus.PENDING_SUPERVISOR: 'PENDINGATSUPERVISOR',
    GrievanceStatus.RESOLVED: 'RESOLVED',
    GrievanceStatus.CLOSED_SOLUTION: 'CLOSEDAFTERRESOLUTION',
    GrievanceStatus.CLOSED_REJECTION: 'CLOSEDAFTERREJECTION',
    GrievanceStatus.REJECTED: 'REJECTED',
  };

  String get name => actionName[this]!;
}

//TradeLisence status
enum TradeLicenseStatus {
  APPLIED,
  INITIATED,
  PENDING_APPROVAL,
  REJECTED,
  APPROVED,
  CITIZEN_ACTION_REQUIRED,
  PENDING_PAYMENT,
}

extension TradeLicenseStatusExtension on TradeLicenseStatus {
  static const actionName = {
    TradeLicenseStatus.APPLIED: 'APPLIED',
    TradeLicenseStatus.APPROVED: 'APPROVED',
    TradeLicenseStatus.INITIATED: 'INITIATED',
    TradeLicenseStatus.PENDING_APPROVAL: 'PENDINGAPPROVAL',
    TradeLicenseStatus.REJECTED: 'REJECTED',
    TradeLicenseStatus.CITIZEN_ACTION_REQUIRED: 'CITIZENACTIONREQUIRED',
    TradeLicenseStatus.PENDING_PAYMENT: 'PENDINGPAYMENT',
  };

  String get name => actionName[this]!;
}

//FireNoc
enum FireNocStatus {
  PENDING_PAYMENT,
  INITIATED,
  DOCUMENT_VERIFY,
  FIELD_INSPECTION,
  PENDING_APPROVAL,
  APPROVED
}

extension FireNocStatusExtension on FireNocStatus {
  static const actionName = {
    FireNocStatus.PENDING_PAYMENT: 'PENDINGPAYMENT',
    FireNocStatus.INITIATED: 'INITIATED',
    FireNocStatus.DOCUMENT_VERIFY: 'DOCUMENTVERIFY',
    FireNocStatus.FIELD_INSPECTION: 'FIELDINSPECTION',
    FireNocStatus.PENDING_APPROVAL: 'PENDINGAPPROVAL',
    FireNocStatus.APPROVED: 'APPROVED',
  };

  String get name => actionName[this]!;
}

//Grievance closed status list
var closedStatusList = [
  GrievanceStatus.CLOSED_SOLUTION.name,
  GrievanceStatus.CLOSED_REJECTION.name,
];

//Grievance resolved status list
var resolvedStatusList = [
  GrievanceStatus.RESOLVED.name,
];

// Grievance reject status list
var rejectedStatusList = [
  GrievanceStatus.REJECTED.name,
];

// // All grievance status list
// var allGrievanceStatusList = [
//   GrievanceStatus.PENDING.name,
//   GrievanceStatus.PENDING_SUPERVISOR.name,
//   GrievanceStatus.RESOLVED.name,
//   GrievanceStatus.CLOSED_SOLUTION.name,
//   GrievanceStatus.CLOSED_REJECTION.name,
//   GrievanceStatus.REJECTED.name,
// ];

//Application Status
enum ApplicationStatus {
  ACTIVE('ACTIVE'),
  INACTIVE('INACTIVE'),
  IN_WORKFLOW('INWORKFLOW');

  final String value;

  const ApplicationStatus(this.value);
}

enum CreationReason {
  CREATE,
  MUTATION,
}

//Fsm status
enum FsmStatus {
  PENDING_APPL_FEE_PAYMENT_CITIZEN('PENDING_APPL_FEE_PAYMENT_CITIZEN'),
  PENDING_APPL_FEE_PAYMENT('PENDING_APPL_FEE_PAYMENT'),
  DSO_INPROGRESS('DSO_INPROGRESS'),

  ASSING_DSO('ASSING_DSO'),
  PENDING_DSO_APPROVAL('PENDING_DSO_APPROVAL'),
  COMPLETED('COMPLETED'),
  CITIZEN_FEEDBACK_PENDING('CITIZEN_FEEDBACK_PENDING');

  final String value;
  const FsmStatus(this.value);
}
