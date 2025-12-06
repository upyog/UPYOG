import 'package:json_annotation/json_annotation.dart';
import 'package:mobile_app/model/employee/status_map/status_map.dart';

part 'emp_bpa_model.g.dart';

@JsonSerializable()
class EmpBpaModel {
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

  EmpBpaModel();

  factory EmpBpaModel.fromJson(Map<String, dynamic> json) =>
      _$EmpBpaModelFromJson(json);

  Map<String, dynamic> toJson() => _$EmpBpaModelToJson(this);
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
  @JsonKey(name: 'approvalDate')
  int? approvalDate;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'applicationNo')
  String? applicationNo;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;
  @JsonKey(name: 'landInfo')
  LandInfo? landInfo;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'landId')
  String? landId;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'edcrNumber')
  String? edcrNumber;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'applicationDate')
  int? applicationDate;
  @JsonKey(name: 'status')
  String? status;

  BusinessObject();

  factory BusinessObject.fromJson(Map<String, dynamic> json) =>
      _$BusinessObjectFromJson(json);

  Map<String, dynamic> toJson() => _$BusinessObjectToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'serviceType')
  String? serviceType;
  @JsonKey(name: 'typeOfArchitect')
  String? typeOfArchitect;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'propertyAcknowldgementNumber')
  String? propertyAcknowledgementNumber;
  @JsonKey(name: 'fieldinspection_pending')
  List<FieldInspectionPending>? fieldInspectionPending;
  @JsonKey(name: 'propertyID')
  String? propertyId;

  AdditionalDetails();

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsToJson(this);
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
class Document {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  Document();

  factory Document.fromJson(Map<String, dynamic> json) =>
      _$DocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentToJson(this);
}

@JsonSerializable()
class LandInfo {
  @JsonKey(name: 'unit')
  List<Unit>? unit;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'ownershipCategory')
  String? ownershipCategory;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'source')
  String? source;

  LandInfo();

  factory LandInfo.fromJson(Map<String, dynamic> json) =>
      _$LandInfoFromJson(json);

  Map<String, dynamic> toJson() => _$LandInfoToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: 'city')
  String? city;
  @JsonKey(name: 'geoLocation')
  GeoLocation? geoLocation;
  @JsonKey(name: 'street')
  String? street;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'locality')
  Locality? locality;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'landmark')
  String? landmark;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class GeoLocation {
  GeoLocation();

  factory GeoLocation.fromJson(Map<String, dynamic> json) =>
      _$GeoLocationFromJson(json);

  Map<String, dynamic> toJson() => _$GeoLocationToJson(this);
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
class Owner {
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'roles')
  List<OwnerRole>? roles;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'ownerId')
  String? ownerId;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'isPrimaryOwner')
  bool? isPrimaryOwner;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'status')
  bool? status;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
}

@JsonSerializable()
class OwnerRole {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;

  OwnerRole();

  factory OwnerRole.fromJson(Map<String, dynamic> json) =>
      _$OwnerRoleFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerRoleToJson(this);
}

@JsonSerializable()
class Unit {
  @JsonKey(name: 'unitType')
  String? unitType;
  @JsonKey(name: 'occupancyType')
  String? occupancyType;
  @JsonKey(name: 'occupancyDate')
  int? occupancyDate;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'floorNo')
  String? floorNo;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'usageCategory')
  String? usageCategory;

  Unit();

  factory Unit.fromJson(Map<String, dynamic> json) => _$UnitFromJson(json);

  Map<String, dynamic> toJson() => _$UnitToJson(this);
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
  List<AssignerRole>? roles;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  String? uuid;

  Assigner();

  factory Assigner.fromJson(Map<String, dynamic> json) =>
      _$AssignerFromJson(json);

  Map<String, dynamic> toJson() => _$AssignerToJson(this);
}

@JsonSerializable()
class AssignerRole {
  @JsonKey(name: 'id')
  dynamic id;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  AssignerRole();

  factory AssignerRole.fromJson(Map<String, dynamic> json) =>
      _$AssignerRoleFromJson(json);

  Map<String, dynamic> toJson() => _$AssignerRoleToJson(this);
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
  bool? active;

  Action();

  factory Action.fromJson(Map<String, dynamic> json) => _$ActionFromJson(json);

  Map<String, dynamic> toJson() => _$ActionToJson(this);
}

@JsonSerializable()
class State {
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'businessServiceId')
  String? businessServiceId;
  @JsonKey(name: 'sla')
  dynamic sla;
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

  State();

  factory State.fromJson(Map<String, dynamic> json) => _$StateFromJson(json);

  Map<String, dynamic> toJson() => _$StateToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  dynamic ver;
  @JsonKey(name: 'ts')
  dynamic ts;
  @JsonKey(name: 'resMsgId')
  String? resMsgId;
  @JsonKey(name: 'msgId')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>
      _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}

@JsonSerializable()
class FieldInspectionPending {
  @JsonKey(name: 'docs')
  List<Doc>? docs;
  @JsonKey(name: 'date')
  DateTime? date;
  @JsonKey(name: 'questions')
  List<Question>? questions;
  @JsonKey(name: 'time')
  String? time;

  FieldInspectionPending();

  factory FieldInspectionPending.fromJson(Map<String, dynamic> json) =>
      _$FieldInspectionPendingFromJson(json);

  Map<String, dynamic> toJson() => _$FieldInspectionPendingToJson(this);
}

@JsonSerializable()
class Doc {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'fileStore')
  String? fileStore;
  @JsonKey(name: 'fileName')
  String? fileName;
  @JsonKey(name: 'dropDownValues')
  DropDownValues? dropDownValues;

  Doc();

  factory Doc.fromJson(Map<String, dynamic> json) => _$DocFromJson(json);

  Map<String, dynamic> toJson() => _$DocToJson(this);
}

@JsonSerializable()
class DropDownValues {
  @JsonKey(name: 'value')
  String? value;

  DropDownValues();

  factory DropDownValues.fromJson(Map<String, dynamic> json) =>
      _$DropDownValuesFromJson(json);

  Map<String, dynamic> toJson() => _$DropDownValuesToJson(this);
}

@JsonSerializable()
class Question {
  @JsonKey(name: 'remarks')
  String? remarks;
  @JsonKey(name: 'question')
  String? question;
  @JsonKey(name: 'value')
  String? value;

  Question();

  factory Question.fromJson(Map<String, dynamic> json) =>
      _$QuestionFromJson(json);

  Map<String, dynamic> toJson() => _$QuestionToJson(this);
}
