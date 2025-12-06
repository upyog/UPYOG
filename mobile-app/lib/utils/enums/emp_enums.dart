/* -------------------------------------------------------------------------- */
/*                                For Employee                                */
/* -------------------------------------------------------------------------- */

enum InboxStatus {
  FIELD_INSPECTION, //TL
  PENDING_FIELD_INSPECTION,
  FIELD_INSPECTION_INPROGRESS,
  IN_WORKFLOW,
  FIELDINSPECTION_PENDING, //BPA state
  DOC_VERIFIED, // PT
}

extension InboxStatusExtension on InboxStatus {
  static const actionNames = {
    InboxStatus.FIELD_INSPECTION: "FIELDINSPECTION",
    InboxStatus.PENDING_FIELD_INSPECTION: "PENDING_FOR_FIELD_INSPECTION",
    InboxStatus.FIELD_INSPECTION_INPROGRESS: "FIELDINSPECTION_INPROGRESS",
    InboxStatus.IN_WORKFLOW: "INWORKFLOW",
    InboxStatus.FIELDINSPECTION_PENDING: "FIELDINSPECTION_PENDING",
    InboxStatus.DOC_VERIFIED: "DOCVERIFIED",
  };
  String get name => actionNames[this]!;
}

enum ModulesEmp {
  TL_SERVICES,
  WS_SERVICES,
  SW_SERVICES,
  BPA_SERVICES,
  PT_SERVICES,
  WS_CONSUMPTION,
  FIRE_NOC,
}

extension ModulesEmpExtension on ModulesEmp {
  static const actionNames = {
    ModulesEmp.TL_SERVICES: "tl-services",
    ModulesEmp.WS_SERVICES: "ws-services",
    ModulesEmp.SW_SERVICES: "sw-services",
    ModulesEmp.BPA_SERVICES: "bpa-services",
    ModulesEmp.PT_SERVICES: "PT",
    ModulesEmp.WS_CONSUMPTION: "ws-calculator",
    ModulesEmp.FIRE_NOC: "fireNoc",
  };
  String get name => actionNames[this]!;
}

enum BusinessServicesEmp {
  //For TL
  NewTL,
  DIRECT_RENEWAL,
  EDIT_RENEWAL,

  //For WS
  NEW_WS1,
  MODIFY_WS_CONNECTION,
  DISCONNECT_WS_CONNECTION,

  //For SW
  NEW_SW1,
  MODIFY_SW_CONNECTION,
  DISCONNECT_SW_CONNECTION,

  //For BPA/OBPS
  BPA_LOW,
  BPA,
  BPA_OC,

  //For PT
  PT_CREATE,
  PT_MUTATION,
  PT_UPDATE,
}

extension BusinessServicesEmpExtension on BusinessServicesEmp {
  static const actionNames = {
    //For TL
    BusinessServicesEmp.NewTL: "NewTL",
    BusinessServicesEmp.DIRECT_RENEWAL: "DIRECTRENEWAL",
    BusinessServicesEmp.EDIT_RENEWAL: "EDITRENEWAL",

    //For WS
    BusinessServicesEmp.NEW_WS1: "NewWS1",
    BusinessServicesEmp.MODIFY_WS_CONNECTION: "ModifyWSConnection",
    BusinessServicesEmp.DISCONNECT_WS_CONNECTION: "DisconnectWSConnection",

    //For SW
    BusinessServicesEmp.NEW_SW1: "NewSW1",
    BusinessServicesEmp.MODIFY_SW_CONNECTION: "ModifySWConnection",
    BusinessServicesEmp.DISCONNECT_SW_CONNECTION: "DisconnectSWConnection",

    //For BPA/OBPS
    BusinessServicesEmp.BPA_LOW: "BPA_LOW",
    BusinessServicesEmp.BPA: "BPA",
    BusinessServicesEmp.BPA_OC: "BPA_OC",

    //For PT
    BusinessServicesEmp.PT_CREATE: "PT.CREATE",
    BusinessServicesEmp.PT_MUTATION: "PT.MUTATION",
    BusinessServicesEmp.PT_UPDATE: "PT.UPDATE",
  };
  String get name => actionNames[this]!;
}

enum InspectorType {
  TL_FIELD_INSPECTOR,
  NOC_FIELD_INSPECTOR,
  BPA_FIELD_INSPECTOR,
  PT_FIELD_INSPECTOR,
  WS_FIELD_INSPECTOR,
  SW_FIELD_INSPECTOR,
  UC_EMP_INSPECTOR,
}

extension InspectorTypeExtension on InspectorType {
  static const actionNames = {
    InspectorType.TL_FIELD_INSPECTOR: "TL_FIELD_INSPECTOR",
    InspectorType.NOC_FIELD_INSPECTOR: "NOC_FIELD_INSPECTOR",
    InspectorType.BPA_FIELD_INSPECTOR: "BPA_FIELD_INSPECTOR",
    InspectorType.PT_FIELD_INSPECTOR: "PT_FIELD_INSPECTOR",
    InspectorType.WS_FIELD_INSPECTOR: "WS_FIELD_INSPECTOR",
    InspectorType.SW_FIELD_INSPECTOR: "SW_FIELD_INSPECTOR",
    InspectorType.UC_EMP_INSPECTOR: "UC_EMP",
  };
  String get name => actionNames[this]!;
}

enum BaseAction {
  sendBackToCitizen,
  forward,
  sendBack,
  approve,
  reject,
  revocate,
  verifyForward
}

extension BaseActionExtension on BaseAction {
  static const actionNames = {
    BaseAction.sendBackToCitizen: "SENDBACKTOCITIZEN",
    BaseAction.forward: "FORWARD",
    BaseAction.sendBack: "SENDBACK",
    BaseAction.approve: "APPROVE",
    BaseAction.reject: "REJECT",
    BaseAction.revocate: "REVOCATE",
    BaseAction.verifyForward: "VERIFY_AND_FORWARD",
  };
  String get name => actionNames[this]!;
}
