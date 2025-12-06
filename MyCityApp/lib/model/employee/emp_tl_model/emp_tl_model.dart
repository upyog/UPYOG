import 'package:json_annotation/json_annotation.dart';
import 'package:mobile_app/model/employee/status_map/status_map.dart';

part 'emp_tl_model.g.dart';

@JsonSerializable()
class EmpTlModel {
  @JsonKey(name: 'responseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'totalCount')
  int? totalCount;
  @JsonKey(name: 'nearingSlaCount')
  int? nearingSlaCount;
  @JsonKey(name: 'statusMap')
  List<StatusMap>? statusMap;
  @JsonKey(name: 'items')
  List<Item>? items;

  EmpTlModel();

  factory EmpTlModel.fromJson(Map<String, dynamic> json) =>
      _$EmpTlModelFromJson(json);

  Map<String, dynamic> toJson() => _$EmpTlModelToJson(this);
}

@JsonSerializable()
class Item {
  @JsonKey(name: 'ProcessInstance')
  ProcessInstance? processInstance;
  @JsonKey(name: 'businessObject')
  BusinessObject? businessObject;
  @JsonKey(name: 'serviceObject', includeFromJson: false)
  dynamic serviceObject;

  Item();

  factory Item.fromJson(Map<String, dynamic> json) => _$ItemFromJson(json);

  Map<String, dynamic> toJson() => _$ItemToJson(this);
}

@JsonSerializable()
class BusinessObject {
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'applicationNumber')
  String? applicationNumber;
  @JsonKey(name: 'validFrom')
  int? validFrom;
  @JsonKey(name: 'workflowCode')
  String? workflowCode;
  @JsonKey(name: 'commencementDate')
  int? commencementDate;
  @JsonKey(name: 'financialYear')
  String? financialYear;
  @JsonKey(name: 'licenseType')
  String? licenseType;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'tradeName')
  String? tradeName;
  @JsonKey(name: 'tradeLicenseDetail')
  TradeLicenseDetail? tradeLicenseDetail;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'applicationDate')
  int? applicationDate;
  @JsonKey(name: 'validTo')
  int? validTo;
  @JsonKey(name: 'status')
  String? status;

  BusinessObject();

  factory BusinessObject.fromJson(Map<String, dynamic> json) =>
      _$BusinessObjectFromJson(json);

  Map<String, dynamic> toJson() => _$BusinessObjectToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'createdTime')
  int? createdTime;
  @JsonKey(name: 'lastModifiedTime')
  int? lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class TradeLicenseDetail {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'additionalDetail')
  AdditionalDetail? additionalDetail;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'structureType')
  String? structureType;
  @JsonKey(name: 'operationalArea')
  int? operationalArea;
  @JsonKey(name: 'noOfEmployees')
  int? noOfEmployees;
  @JsonKey(name: 'tradeUnits')
  List<TradeUnit>? tradeUnits;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'applicationDocuments')
  List<ApplicationDocument>? applicationDocuments;
  @JsonKey(name: 'subOwnerShipCategory')
  String? subOwnerShipCategory;

  TradeLicenseDetail();

  factory TradeLicenseDetail.fromJson(Map<String, dynamic> json) =>
      _$TradeLicenseDetailFromJson(json);

  Map<String, dynamic> toJson() => _$TradeLicenseDetailToJson(this);
}

@JsonSerializable()
class AdditionalDetail {
  @JsonKey(name: "propertyId")
  String? propertyId;
  @JsonKey(name: "tradeGstNo")
  String? tradeGstNo;
  @JsonKey(name: "isSameAsPropertyOwner")
  String? isSameAsPropertyOwner;

  AdditionalDetail();

  factory AdditionalDetail.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: "street")
  String? street;
  @JsonKey(name: "tenantId")
  String? tenantId;
  @JsonKey(name: "locality")
  Locality? locality;
  @JsonKey(name: "id")
  String? id;
  @JsonKey(name: "doorNo")
  String? doorNo;
  @JsonKey(name: "landmark", includeIfNull: true)
  String? landmark;
  @JsonKey(name: "city")
  String? city;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class Locality {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'children')
  List<dynamic>? children;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'label')
  String? label;

  Locality();

  factory Locality.fromJson(Map<String, dynamic> json) =>
      _$LocalityFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityToJson(this);
}

@JsonSerializable()
class ApplicationDocument {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  ApplicationDocument();

  factory ApplicationDocument.fromJson(Map<String, dynamic> json) =>
      _$ApplicationDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$ApplicationDocumentToJson(this);
}

@JsonSerializable()
class Owner {
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'lastModifiedDate')
  int? lastModifiedDate;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'roles')
  List<Roles>? roles;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'userActive')
  bool? userActive;
  @JsonKey(name: 'createdDate')
  int? createdDate;
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'permanentAddress')
  String? permanentAddress;
  @JsonKey(name: 'relationship')
  String? relationship;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
}

@JsonSerializable()
class Roles {
  @JsonKey(name: 'id')
  dynamic id;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  Roles();

  factory Roles.fromJson(Map<String, dynamic> json) => _$RolesFromJson(json);

  Map<String, dynamic> toJson() => _$RolesToJson(this);
}

@JsonSerializable()
class TradeUnit {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'tradeType')
  String? tradeType;

  TradeUnit();

  factory TradeUnit.fromJson(Map<String, dynamic> json) =>
      _$TradeUnitFromJson(json);

  Map<String, dynamic> toJson() => _$TradeUnitToJson(this);
}

@JsonSerializable()
class ProcessInstance {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'businessId')
  String? businessId;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'moduleName')
  String? moduleName;
  @JsonKey(name: 'state')
  State? state;
  @JsonKey(name: 'comment')
  String? comment;
  @JsonKey(name: 'documents')
  dynamic documents;
  @JsonKey(name: 'assigner')
  Assigner? assigner;
  @JsonKey(name: 'assignes')
  dynamic assignes;
  @JsonKey(name: 'nextActions')
  List<Action>? nextActions;
  @JsonKey(name: 'stateSla')
  dynamic stateSla;
  @JsonKey(name: 'businesssServiceSla')
  int? businesssServiceSla;
  @JsonKey(name: 'previousStatus')
  dynamic previousStatus;
  @JsonKey(name: 'entity')
  dynamic entity;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'rating')
  int? rating;
  @JsonKey(name: 'escalated')
  bool? escalated;

  ProcessInstance();

  factory ProcessInstance.fromJson(Map<String, dynamic> json) =>
      _$ProcessInstanceFromJson(json);

  Map<String, dynamic> toJson() => _$ProcessInstanceToJson(this);
}

@JsonSerializable()
class Assigner {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'roles')
  List<Roles>? roles;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'uuid')
  String? uuid;

  Assigner();

  factory Assigner.fromJson(Map<String, dynamic> json) =>
      _$AssignerFromJson(json);

  Map<String, dynamic> toJson() => _$AssignerToJson(this);
}

@JsonSerializable()
class Action {
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'currentState')
  String? currentState;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'nextState')
  String? nextState;
  @JsonKey(name: 'roles')
  List<String>? roles;
  @JsonKey(name: 'active')
  dynamic active;

  Action();

  factory Action.fromJson(Map<String, dynamic> json) => _$ActionFromJson(json);

  Map<String, dynamic> toJson() => _$ActionToJson(this);
}

@JsonSerializable()
class State {
  @JsonKey(name: "auditDetails")
  dynamic auditDetails;
  @JsonKey(name: "uuid")
  String? uuid;
  @JsonKey(name: "tenantId")
  String? tenantId;
  @JsonKey(name: "businessServiceId")
  String? businessServiceId;
  @JsonKey(name: "sla")
  dynamic sla;
  @JsonKey(name: "state")
  String? state;
  @JsonKey(name: "applicationStatus")
  String? applicationStatus;
  @JsonKey(name: "docUploadRequired")
  bool? docUploadRequired;
  @JsonKey(name: "isStartState")
  bool? isStartState;
  @JsonKey(name: "isTerminateState")
  bool? isTerminateState;
  @JsonKey(name: "isStateUpdatable")
  dynamic isStateUpdatable;
  @JsonKey(name: "actions")
  List<Action>? actions;

  State();

  factory State.fromJson(Map<String, dynamic> json) => _$StateFromJson(json);

  Map<String, dynamic> toJson() => _$StateToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'api_id')
  String? apiId;
  @JsonKey(name: 'ver')
  String? ver;
  @JsonKey(name: 'ts')
  String? ts;
  @JsonKey(name: 'res_msg_id')
  String? resMsgId;
  @JsonKey(name: 'msg_id')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>
      _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}
