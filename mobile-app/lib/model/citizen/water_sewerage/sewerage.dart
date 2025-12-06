import 'package:json_annotation/json_annotation.dart';

part 'sewerage.g.dart';

@JsonSerializable()
class Sewerage {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'SewerageConnections')
  List<SewerageConnection>? sewerageConnections;
  @JsonKey(name: 'TotalCount')
  int? totalCount;

  Sewerage();

  factory Sewerage.fromJson(Map<String, dynamic> json) =>
      _$SewerageFromJson(json);

  Map<String, dynamic> toJson() => _$SewerageToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  String? ver;
  @JsonKey(name: 'ts')
  String? ts;
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
class SewerageConnection {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'propertyId')
  String? propertyId;
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
  @JsonKey(name: 'documents', includeIfNull: false)
  List<SDocument>? documents;
  @JsonKey(name: 'plumberInfo', includeIfNull: false)
  List<PlumberInfo>? plumberInfo;
  @JsonKey(name: 'roadType')
  dynamic roadType;
  @JsonKey(name: 'roadCuttingArea')
  int? roadCuttingArea;
  @JsonKey(name: 'roadCuttingInfo', includeIfNull: false)
  List<RoadCuttingInfoElement>? roadCuttingInfo;
  @JsonKey(name: 'connectionExecutionDate')
  int? connectionExecutionDate;
  @JsonKey(name: 'connectionCategory')
  dynamic connectionCategory;
  @JsonKey(name: 'connectionType')
  dynamic connectionType;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;
  @JsonKey(name: 'processInstance')
  ProcessInstance? processInstance;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'connectionHolders')
  List<ConnectionHolder>? connectionHolders;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'dateEffectiveFrom')
  int? dateEffectiveFrom;
  @JsonKey(name: 'oldApplication')
  bool? oldApplication;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'disconnectionExecutionDate')
  int? disconnectionExecutionDate;
  @JsonKey(name: 'proposedWaterClosets')
  int? proposedWaterClosets;
  @JsonKey(name: 'proposedToilets')
  int? proposedToilets;
  @JsonKey(name: 'noOfWaterClosets')
  int? noOfWaterClosets;
  @JsonKey(name: 'noOfToilets')
  int? noOfToilets;
  @JsonKey(name: 'isDisconnectionTemporary')
  bool? isDisconnectionTemporary;
  @JsonKey(name: 'disconnectionReason')
  dynamic disconnectionReason;
  @JsonKey(name: 'comment', includeIfNull: false)
  String? comment;
  @JsonKey(name: 'assignes', includeIfNull: false)
  List<Assignes>? assignes;
  @JsonKey(name: 'assignee', includeIfNull: false)
  List<String>? assignee;
  @JsonKey(name: 'wfDocuments', includeIfNull: false)
  List<WfDocument>? wfDocuments;
  @JsonKey(name: 'action', includeIfNull: false)
  String? action;

  SewerageConnection();

  factory SewerageConnection.fromJson(Map<String, dynamic> json) =>
      _$SewerageConnectionFromJson(json);

  Map<String, dynamic> toJson() => _$SewerageConnectionToJson(this);
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
class RoadCuttingInfoElement {
  @JsonKey(name: 'id', includeIfNull: false)
  String? id;
  @JsonKey(name: 'roadType')
  String? roadType;
  @JsonKey(name: 'roadCuttingArea')
  int? roadCuttingArea;
  @JsonKey(name: 'auditDetails', includeIfNull: false)
  dynamic auditDetails;
  @JsonKey(name: 'status', includeIfNull: false)
  String? status;

  RoadCuttingInfoElement();

  factory RoadCuttingInfoElement.fromJson(Map<String, dynamic> json) =>
      _$RoadCuttingInfoElementFromJson(json);

  Map<String, dynamic> toJson() => _$RoadCuttingInfoElementToJson(this);
}

@JsonSerializable()
class Assignes {
  @JsonKey(name: 'uuid')
  String? uuid;

  Assignes();

  factory Assignes.fromJson(Map<String, dynamic> json) =>
      _$AssignesFromJson(json);

  Map<String, dynamic> toJson() => _$AssignesToJson(this);
}

@JsonSerializable()
class WfDocument {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileName')
  String? fileName;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  WfDocument();

  factory WfDocument.fromJson(Map<String, dynamic> json) =>
      _$WfDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$WfDocumentToJson(this);
}

@JsonSerializable()
class SDocument {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentUid')
  String? documentUid;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'status')
  String? status;

  SDocument();

  factory SDocument.fromJson(Map<String, dynamic> json) =>
      _$SDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$SDocumentToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'locality')
  String? locality;
  @JsonKey(name: 'appCreatedDate')
  int? appCreatedDate;
  @JsonKey(name: 'detailsProvidedBy')
  dynamic detailsProvidedBy;
  @JsonKey(name: 'initialMeterReading')
  dynamic initialMeterReading;
  @JsonKey(name: 'adhocPenalty')
  dynamic adhocPenalty;
  @JsonKey(name: 'adhocRebate')
  dynamic adhocRebate;
  @JsonKey(name: 'adhocPenaltyReason')
  dynamic adhocPenaltyReason;
  @JsonKey(name: 'adhocPenaltyComment')
  dynamic adhocPenaltyComment;
  @JsonKey(name: 'adhocRebateReason')
  dynamic adhocRebateReason;
  @JsonKey(name: 'adhocRebateComment')
  dynamic adhocRebateComment;
  @JsonKey(name: 'estimationFileStoreId')
  dynamic estimationFileStoreId;
  @JsonKey(name: 'sanctionFileStoreId')
  dynamic sanctionFileStoreId;
  @JsonKey(name: 'estimationLetterDate')
  dynamic estimationLetterDate;

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
class ProcessInstance {
  @JsonKey(name: 'id', includeIfNull: false)
  dynamic id;
  @JsonKey(name: 'tenantId', includeIfNull: false)
  dynamic tenantId;
  @JsonKey(name: 'businessService', includeIfNull: false)
  String? businessService;
  @JsonKey(name: 'businessId', includeIfNull: false)
  dynamic businessId;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'moduleName', includeIfNull: false)
  String? moduleName;
  @JsonKey(name: 'state', includeIfNull: false)
  dynamic state;
  @JsonKey(name: 'comment')
  dynamic comment;
  @JsonKey(name: 'documents')
  List<SwDocument>? documents;
  @JsonKey(name: 'assignes', includeIfNull: false)
   List<Assignes>? assignes;

  ProcessInstance();

  factory ProcessInstance.fromJson(Map<String, dynamic> json) =>
      _$ProcessInstanceFromJson(json);

  Map<String, dynamic> toJson() => _$ProcessInstanceToJson(this);
}

@JsonSerializable()
class ConnectionHolder {
  @JsonKey(name: 'id')
  dynamic id;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'password')
  dynamic password;
  @JsonKey(name: 'salutation')
  dynamic salutation;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'altContactNumber')
  dynamic altContactNumber;
  @JsonKey(name: 'pan')
  dynamic pan;
  @JsonKey(name: 'aadhaarNumber')
  dynamic aadhaarNumber;
  @JsonKey(name: 'permanentAddress')
  dynamic permanentAddress;
  @JsonKey(name: 'permanentCity')
  dynamic permanentCity;
  @JsonKey(name: 'permanentPinCode')
  dynamic permanentPinCode;
  @JsonKey(name: 'correspondenceCity')
  dynamic correspondenceCity;
  @JsonKey(name: 'correspondencePinCode')
  dynamic correspondencePinCode;
  @JsonKey(name: 'correspondenceAddress')
  String? correspondenceAddress;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'dob')
  dynamic dob;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'locale')
  dynamic locale;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'signature')
  dynamic signature;
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'roles')
  List<Role>? roles;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'bloodGroup')
  dynamic bloodGroup;
  @JsonKey(name: 'identificationMark')
  dynamic identificationMark;
  @JsonKey(name: 'photo')
  dynamic photo;
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'createdDate')
  int? createdDate;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'lastModifiedDate')
  int? lastModifiedDate;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'ownerInfoUuid')
  dynamic ownerInfoUuid;
  @JsonKey(name: 'isPrimaryOwner')
  dynamic isPrimaryOwner;
  @JsonKey(name: 'ownerShipPercentage')
  dynamic ownerShipPercentage;
  @JsonKey(name: 'ownerType')
  String? ownerType;
  @JsonKey(name: 'institutionId')
  dynamic institutionId;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'documents')
  dynamic documents;
  @JsonKey(name: 'relationship')
  String? relationship;

  ConnectionHolder();
  factory ConnectionHolder.fromJson(Map<String, dynamic> json) =>
      _$ConnectionHolderFromJson(json);

  Map<String, dynamic> toJson() => _$ConnectionHolderToJson(this);
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
class SwDocument {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileName')
  String? fileName;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  SwDocument();

  factory SwDocument.fromJson(Map<String, dynamic> json) =>
      _$SwDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$SwDocumentToJson(this);
}
