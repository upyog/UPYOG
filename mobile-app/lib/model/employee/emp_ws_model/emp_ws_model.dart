import 'package:json_annotation/json_annotation.dart';
import 'package:mobile_app/model/employee/status_map/status_map.dart';

part 'emp_ws_model.g.dart';

@JsonSerializable()
class EmpWsModel {
  @JsonKey(name: 'responseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'totalCount')
  int? totalCount;
  @JsonKey(name: 'nearingSlaCount')
  int? nearingSlaCount;
  @JsonKey(name: 'statusMap')
  List<StatusMap>? statusMap;
  @JsonKey(name: 'items')
  List<WsItem>? wsItems;

  EmpWsModel();

  factory EmpWsModel.fromJson(Map<String, dynamic> json) =>
      _$EmpWsModelFromJson(json);

  Map<String, dynamic> toJson() => _$EmpWsModelToJson(this);
}

@JsonSerializable()
class WsItem {
  @JsonKey(name: 'ProcessInstance', includeFromJson: false)
  dynamic processInstance;
  @JsonKey(name: 'businessObject')
  BusinessObject? businessObject;
  @JsonKey(name: 'serviceObject', includeFromJson: false)
  dynamic serviceObject;

  WsItem();

  factory WsItem.fromJson(Map<String, dynamic> json) => _$WsItemFromJson(json);

  Map<String, dynamic> toJson() => _$WsItemToJson(this);
}

@JsonSerializable()
class BusinessObject {
  @JsonKey(name: 'serviceSLA')
  int? serviceSla;
  @JsonKey(name: 'Data')
  Data? data;

  BusinessObject();

  factory BusinessObject.fromJson(Map<String, dynamic> json) =>
      _$BusinessObjectFromJson(json);

  Map<String, dynamic> toJson() => _$BusinessObjectToJson(this);
}

@JsonSerializable()
class Data {
  @JsonKey(name: 'workflow')
  Workflow? workflow;
  @JsonKey(name: 'applicationNo')
  String? applicationNo;
  @JsonKey(name: 'applicationStatus')
  String? applicationStatus;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'connectionNo')
  dynamic connectionNo;
  @JsonKey(name: 'oldConnectionNo')
  dynamic oldConnectionNo;
  @JsonKey(name: 'plumberInfo')
  List<PlumberInfo>? plumberInfo;
  @JsonKey(name: 'roadCuttingInfo')
  List<RoadCuttingInfo>? roadCuttingInfo;
  @JsonKey(name: 'connectionHolders')
  dynamic connectionHolders;
  @JsonKey(name: 'roadType')
  dynamic roadType;
  @JsonKey(name: 'roadCuttingArea')
  int? roadCuttingArea;
  @JsonKey(name: 'connectionExecutionDate')
  int? connectionExecutionDate;
  @JsonKey(name: 'connectionCategory')
  dynamic connectionCategory;
  @JsonKey(name: 'connectionType')
  String? connectionType;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'propertyId')
  String? propertyId;
  @JsonKey(name: 'propertyUsageType')
  String? propertyUsageType;
  @JsonKey(name: 'rainWaterHarvesting')
  String? rainWaterHarvesting;
  @JsonKey(name: 'waterSource')
  String? waterSource;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'meterId')
  String? meterId;
  @JsonKey(name: 'meterInstallationDate')
  int? meterInstallationDate;
  @JsonKey(name: 'proposedPipeSize')
  double? proposedPipeSize;
  @JsonKey(name: 'proposedTaps')
  int? proposedTaps;
  @JsonKey(name: 'pipeSize')
  double? pipeSize;
  @JsonKey(name: 'noOfTaps')
  int? noOfTaps;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'dateEffectiveFrom')
  int? dateEffectiveFrom;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'timestamp')
  DateTime? timestamp;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'history')
  List<History>? history;
  @JsonKey(name: 'ownerMobileNumbers')
  List<String>? ownerMobileNumbers;
  @JsonKey(name: 'ward')
  Ward? ward;
  @JsonKey(name: 'tenantData')
  TenantData? tenantData;

  Data();

  factory Data.fromJson(Map<String, dynamic> json) => _$DataFromJson(json);

  Map<String, dynamic> toJson() => _$DataToJson(this);
}

@JsonSerializable()
class RoadCuttingInfo {
  @JsonKey(name: "id", includeIfNull: false)
  String? id;
  @JsonKey(name: "roadType")
  String? roadType;
  @JsonKey(name: "roadCuttingArea")
  int? roadCuttingArea;
  @JsonKey(name: "auditDetails", includeIfNull: false)
  dynamic auditDetails;
  @JsonKey(name: "status", includeIfNull: false)
  String? status;

  RoadCuttingInfo();

  factory RoadCuttingInfo.fromJson(Map<String, dynamic> json) =>
      _$RoadCuttingInfoFromJson(json);

  Map<String, dynamic> toJson() => _$RoadCuttingInfoToJson(this);
}

@JsonSerializable()
class PlumberInfo {
  @JsonKey(name: "id", includeIfNull: false)
  String? id;
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "licenseNo")
  String? licenseNo;
  @JsonKey(name: "mobileNumber")
  String? mobileNumber;
  @JsonKey(name: "gender", includeIfNull: false)
  dynamic gender;
  @JsonKey(name: "fatherOrHusbandName", includeIfNull: false)
  dynamic fatherOrHusbandName;
  @JsonKey(name: "correspondenceAddress", includeIfNull: false)
  dynamic correspondenceAddress;
  @JsonKey(name: "relationship", includeIfNull: false)
  dynamic relationship;
  @JsonKey(name: "additionalDetails", includeIfNull: false)
  dynamic additionalDetails;
  @JsonKey(name: "auditDetails", includeIfNull: false)
  dynamic auditDetails;

  PlumberInfo();

  factory PlumberInfo.fromJson(Map<String, dynamic> json) =>
      _$PlumberInfoFromJson(json);

  Map<String, dynamic> toJson() => _$PlumberInfoToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'adhocPenaltyComment')
  dynamic adhocPenaltyComment;
  @JsonKey(name: 'adhocPenaltyReason')
  dynamic adhocPenaltyReason;
  @JsonKey(name: 'adhocRebateReason')
  dynamic adhocRebateReason;
  @JsonKey(name: 'estimationLetterDate')
  dynamic estimationLetterDate;
  @JsonKey(name: 'adhocRebate')
  dynamic adhocRebate;
  @JsonKey(name: 'detailsProvidedBy')
  dynamic detailsProvidedBy;
  @JsonKey(name: 'locality')
  String? locality;
  @JsonKey(name: 'estimationFileStoreId')
  dynamic estimationFileStoreId;
  @JsonKey(name: 'sanctionFileStoreId')
  dynamic sanctionFileStoreId;
  @JsonKey(name: 'adhocRebateComment')
  dynamic adhocRebateComment;
  @JsonKey(name: 'adhocPenalty')
  dynamic adhocPenalty;
  @JsonKey(name: 'ownerName')
  String? ownerName;
  @JsonKey(name: 'appCreatedDate')
  int? appCreatedDate;
  @JsonKey(name: 'initialMeterReading')
  int? initialMeterReading;

  AdditionalDetails();

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'lastModifiedTime')
  int? lastModifiedTime;
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'createdTime')
  int? createdTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class History {
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
  HistoryState? state;
  @JsonKey(name: 'comment')
  String? comment;
  @JsonKey(name: 'documents')
  dynamic documents;
  @JsonKey(name: 'assigner')
  Assigner? assigner;
  @JsonKey(name: 'assignes')
  dynamic assignees;
  @JsonKey(name: 'nextActions')
  List<dynamic>? nextActions;
  @JsonKey(name: 'stateSla')
  int? stateSla;
  @JsonKey(name: 'businesssServiceSla')
  int? businessServiceSla;
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

  History();

  factory History.fromJson(Map<String, dynamic> json) =>
      _$HistoryFromJson(json);

  Map<String, dynamic> toJson() => _$HistoryToJson(this);
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
  dynamic emailId;
  @JsonKey(name: 'roles')
  List<Role>? roles;
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
class Role {
  @JsonKey(name: 'id')
  dynamic id;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  Role();

  factory Role.fromJson(Map<String, dynamic> json) => _$RoleFromJson(json);

  Map<String, dynamic> toJson() => _$RoleToJson(this);
}

@JsonSerializable()
class HistoryState {
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'businessServiceId')
  String? businessServiceId;
  @JsonKey(name: 'sla')
  int? sla;
  @JsonKey(name: 'state')
  String? state;
  @JsonKey(name: 'applicationStatus')
  String? applicationStatus;
  @JsonKey(name: 'docUploadRequired')
  bool? docUploadRequired;
  @JsonKey(name: 'isStartState')
  bool? isStartState;
  @JsonKey(name: 'isTerminateState')
  bool? isTerminateState;
  @JsonKey(name: 'isStateUpdatable')
  dynamic isStateUpdatable;
  @JsonKey(name: 'actions')
  List<Action>? actions;

  HistoryState();

  factory HistoryState.fromJson(Map<String, dynamic> json) =>
      _$HistoryStateFromJson(json);

  Map<String, dynamic> toJson() => _$HistoryStateToJson(this);
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
class TenantData {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'description')
  String? description;
  @JsonKey(name: 'pincode')
  List<int>? pincode;
  @JsonKey(name: 'logoId')
  String? logoId;
  @JsonKey(name: 'imageId')
  dynamic imageId;
  @JsonKey(name: 'domainUrl')
  String? domainUrl;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'twitterUrl')
  dynamic twitterUrl;
  @JsonKey(name: 'facebookUrl')
  dynamic facebookUrl;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'officeTimings')
  OfficeTimings? officeTimings;
  @JsonKey(name: 'city')
  City? city;
  @JsonKey(name: 'address')
  String? address;
  @JsonKey(name: 'contactNumber')
  String? contactNumber;

  TenantData();

  factory TenantData.fromJson(Map<String, dynamic> json) =>
      _$TenantDataFromJson(json);

  Map<String, dynamic> toJson() => _$TenantDataToJson(this);
}

@JsonSerializable()
class City {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'localName')
  dynamic localName;
  @JsonKey(name: 'districtCode')
  String? districtCode;
  @JsonKey(name: 'districtName')
  dynamic districtName;
  @JsonKey(name: 'districtTenantCode')
  String? districtTenantCode;
  @JsonKey(name: 'regionName')
  dynamic regionName;
  @JsonKey(name: 'ulbGrade')
  String? ulbGrade;
  @JsonKey(name: 'longitude')
  double? longitude;
  @JsonKey(name: 'latitude')
  double? latitude;
  @JsonKey(name: 'shapeFileLocation')
  dynamic shapeFileLocation;
  @JsonKey(name: 'captcha')
  dynamic captcha;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'ddrName')
  String? ddrName;

  City();

  factory City.fromJson(Map<String, dynamic> json) => _$CityFromJson(json);

  Map<String, dynamic> toJson() => _$CityToJson(this);
}

@JsonSerializable()
class OfficeTimings {
  @JsonKey(name: 'Mon - Fri')
  String? monFri;

  OfficeTimings();

  factory OfficeTimings.fromJson(Map<String, dynamic> json) =>
      _$OfficeTimingsFromJson(json);

  Map<String, dynamic> toJson() => _$OfficeTimingsToJson(this);
}

@JsonSerializable()
class Ward {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'label')
  String? label;
  @JsonKey(name: 'latitude')
  dynamic latitude;
  @JsonKey(name: 'longitude')
  dynamic longitude;
  @JsonKey(name: 'area')
  String? area;
  @JsonKey(name: 'pincode')
  dynamic pinCode;
  @JsonKey(name: 'boundaryNum')
  int? boundaryNum;
  @JsonKey(name: 'children')
  List<Ward>? children;

  Ward();

  factory Ward.fromJson(Map<String, dynamic> json) => _$WardFromJson(json);

  Map<String, dynamic> toJson() => _$WardToJson(this);
}

@JsonSerializable()
class Workflow {
  @JsonKey(name: 'state')
  WorkflowState? state;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'assignes')
  List<dynamic>? assignes;

  Workflow();

  factory Workflow.fromJson(Map<String, dynamic> json) =>
      _$WorkflowFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowToJson(this);
}

@JsonSerializable()
class WorkflowState {
  WorkflowState();

  factory WorkflowState.fromJson(Map<String, dynamic> json) =>
      _$WorkflowStateFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowStateToJson(this);
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
