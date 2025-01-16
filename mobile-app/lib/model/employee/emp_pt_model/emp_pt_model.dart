import 'package:json_annotation/json_annotation.dart';
import 'package:mobile_app/model/employee/status_map/status_map.dart';

part 'emp_pt_model.g.dart';

@JsonSerializable()
class EmpPtModel {
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

  EmpPtModel();

  factory EmpPtModel.fromJson(Map<String, dynamic> json) =>
      _$EmpPtModelFromJson(json);

  Map<String, dynamic> toJson() => _$EmpPtModelToJson(this);
}

@JsonSerializable()
class Item {
  @JsonKey(name: 'ProcessInstance')
  ProcessInstance? processInstance;
  @JsonKey(name: 'businessObject')
  BusinessObject? businessObject;
  @JsonKey(name: 'serviceObject')
  dynamic serviceObject;

  Item();

  factory Item.fromJson(Map<String, dynamic> json) => _$ItemFromJson(json);

  Map<String, dynamic> toJson() => _$ItemToJson(this);
}

@JsonSerializable()
class BusinessObject {
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'landArea')
  int? landArea;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'creationReason')
  String? creationReason;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'noOfFloors')
  int? noOfFloors;
  @JsonKey(name: 'ownershipCategory')
  String? ownershipCategory;
  @JsonKey(name: 'AlternateUpdated')
  bool? alternateUpdated;
  @JsonKey(name: 'propertyType')
  String? propertyType;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'propertyId')
  String? propertyId;
  @JsonKey(name: 'isOldDataEncryptionRequest')
  bool? isOldDataEncryptionRequest;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'acknowldgementNumber')
  String? acknowledgementNumber;
  @JsonKey(name: 'usageCategory')
  String? usageCategory;

  BusinessObject();

  factory BusinessObject.fromJson(Map<String, dynamic> json) =>
      _$BusinessObjectFromJson(json);

  Map<String, dynamic> toJson() => _$BusinessObjectToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: 'city')
  String? city;
  @JsonKey(name: 'geoLocation')
  GeoLocation? geoLocation;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'locality')
  Locality? locality;
  @JsonKey(name: 'id')
  String? id;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class GeoLocation {
  @JsonKey(name: 'latitude')
  int? latitude;
  @JsonKey(name: 'longitude')
  int? longitude;

  GeoLocation();

  factory GeoLocation.fromJson(Map<String, dynamic> json) =>
      _$GeoLocationFromJson(json);

  Map<String, dynamic> toJson() => _$GeoLocationToJson(this);
}

@JsonSerializable()
class Locality {
  @JsonKey(name: 'area')
  String? area;
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
  @JsonKey(name: 'documentUid')
  String? documentUid;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'status')
  String? status;

  Document();

  factory Document.fromJson(Map<String, dynamic> json) =>
      _$DocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentToJson(this);
}

@JsonSerializable()
class Owner {
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'ownerType')
  String? ownerType;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'lastModifiedDate')
  int? lastModifiedDate;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'roles')
  List<Role>? roles;
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
  @JsonKey(name: 'ownerInfoUuid')
  String? ownerInfoUuid;
  @JsonKey(name: 'correspondenceAddress')
  String? correspondenceAddress;
  @JsonKey(name: 'createdDate')
  int? createdDate;
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'permanentAddress')
  String? permanentAddress;
  @JsonKey(name: 'relationship')
  String? relationship;
  @JsonKey(name: 'status')
  String? status;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
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
  dynamic comment;
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
