import 'package:json_annotation/json_annotation.dart';

part 'bpa_model.g.dart';

@JsonSerializable()
class Bpa {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'BPA')
  List<BpaElement>? bpaele;
  @JsonKey(name: 'Count')
  int? count;

  Bpa();

  factory Bpa.fromJson(Map<String, dynamic> json) => _$BpaFromJson(json);

  Map<String, dynamic> toJson() => _$BpaToJson(this);
}

@JsonSerializable()
class BpaElement {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'applicationNo')
  String? applicationNo;
  @JsonKey(name: 'approvalNo')
  String? approvalNo;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'edcrNumber')
  String? edcrNumber;
  @JsonKey(name: 'riskType')
  String? riskType;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'landId')
  String? landId;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'approvalDate')
  int? approvalDate;
  @JsonKey(name: 'applicationDate')
  int? applicationDate;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'landInfo')
  LandInfo? landInfo;
  @JsonKey(name: 'workflow', includeIfNull: false)
  Workflow? workflow;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'additionalDetails')
  BpaAdditionalDetails? additionalDetails;
  @JsonKey(name: 'comment', includeIfNull: false)
  String? comment;
  @JsonKey(name: 'action', includeIfNull: false)
  String? action;
  @JsonKey(name: 'assignee', includeIfNull: false)
  List<String>? assignee;
  @JsonKey(name: 'wfDocuments', includeIfNull: false)
  List<WfDocument>? wfDocuments;

  BpaElement();

  factory BpaElement.fromJson(Map<String, dynamic> json) =>
      _$BpaElementFromJson(json);

  Map<String, dynamic> toJson() => _$BpaElementToJson(this);
}

@JsonSerializable()
class Workflow {
  @JsonKey(name: 'comments')
  String? comments;
  @JsonKey(name: 'comment')
  String? comment;
  @JsonKey(name: 'assignee')
  dynamic assignee;
  @JsonKey(name: 'assignes')
  dynamic assignes;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'varificationDocuments', includeIfNull: false)
  List<VerificationDocument>? verificationDocuments;

  Workflow();

  factory Workflow.fromJson(Map<String, dynamic> json) =>
      _$WorkflowFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowToJson(this);
}

@JsonSerializable()
class VerificationDocument {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileName')
  String? fileName;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  VerificationDocument();

  factory VerificationDocument.fromJson(Map<String, dynamic> json) =>
      _$VerificationDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$VerificationDocumentToJson(this);
}

@JsonSerializable()
class BpaAdditionalDetails {
  @JsonKey(name: 'propertyID', includeIfNull: false)
  String? propertyId;
  @JsonKey(name: 'serviceType', includeIfNull: false)
  String? serviceType;
  @JsonKey(name: 'GISPlaceName', includeIfNull: false)
  String? gisPlaceName;
  @JsonKey(name: 'applicationType', includeIfNull: false)
  String? applicationType;
  @JsonKey(name: 'typeOfArchitect', includeIfNull: false)
  String? typeOfArchitect;
  @JsonKey(name: 'fieldinspection_pending', includeIfNull: false)
  List<FieldInspectionPending>? fieldInspectionPending;
  @JsonKey(name: 'propertyAcknowldgementNumber', includeIfNull: false)
  String? propertyAcknowledgementNumber;
  @JsonKey(name: 'holdingNo', includeIfNull: false)
  String? holdingNo;
  @JsonKey(
    name: 'registrationDetails',
    includeIfNull: false,
  )
  String? registrationDetails;
  @JsonKey(name: 'validityDate', includeIfNull: false)
  int? validityDate;
  @JsonKey(
    name: 'pendingapproval',
    includeIfNull: false,
  )
  List<String>? pendingApproval;
  @JsonKey(name: 'landId', includeIfNull: false)
  String? landId;
  @JsonKey(name: 'permitNumber', includeIfNull: false)
  String? permitNumber;
  @JsonKey(name: 'remarks', includeIfNull: false)
  String? remarks;
  @JsonKey(
    name: 'boundaryWallLength',
    includeIfNull: false,
  )
  String? boundaryWallLength;

  BpaAdditionalDetails();

  factory BpaAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$BpaAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$BpaAdditionalDetailsToJson(this);
}

@JsonSerializable()
class FieldInspectionPending {
  @JsonKey(name: 'docs')
  List<Doc>? docs;
  @JsonKey(name: 'date')
  String? date;
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
  @JsonKey(name: 'remarks', includeIfNull: false)
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
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'title')
  String? title;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentUid')
  String? documentUid;
  @JsonKey(name: 'url')
  String? url;
  @JsonKey(name: 'additionalDetails', includeIfNull: false)
  DocumentAdditionalDetails? additionalDetails;
  @JsonKey(name: 'auditDetails', includeIfNull: false)
  String? auditDetails;

  Document();

  factory Document.fromJson(Map<String, dynamic> json) =>
      _$DocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentToJson(this);
}

@JsonSerializable()
class DocumentAdditionalDetails {
  @JsonKey(name: 'uploadedBy')
  String? uploadedBy;
  @JsonKey(name: 'uploadedTime')
  double? uploadedTime;

  DocumentAdditionalDetails();

  factory DocumentAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$DocumentAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentAdditionalDetailsToJson(this);
}

@JsonSerializable()
class LandInfo {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'landUId')
  String? landUId;
  @JsonKey(name: 'landUniqueRegNo')
  String? landUniqueRegNo;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'ownershipCategory')
  String? ownershipCategory;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'institution')
  String? institution;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'documents')
  String? documents;
  @JsonKey(name: 'unit')
  List<Unit>? unit;
  @JsonKey(name: 'additionalDetails')
  String? additionalDetails;
  @JsonKey(name: 'auditDetails', includeIfNull: false)
  AuditDetails? auditDetails;

  LandInfo();

  factory LandInfo.fromJson(Map<String, dynamic> json) =>
      _$LandInfoFromJson(json);

  Map<String, dynamic> toJson() => _$LandInfoToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'doorNo')
  String? doorNo;
  @JsonKey(name: 'plotNo')
  String? plotNo;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'landmark')
  String? landmark;
  @JsonKey(name: 'city')
  String? city;
  @JsonKey(name: 'district')
  String? district;
  @JsonKey(name: 'region')
  String? region;
  @JsonKey(name: 'state')
  String? state;
  @JsonKey(name: 'country')
  String? country;
  @JsonKey(name: 'pincode')
  String? pincode;
  @JsonKey(name: 'additionDetails')
  String? additionDetails;
  @JsonKey(name: 'buildingName')
  String? buildingName;
  @JsonKey(name: 'street')
  String? street;
  @JsonKey(name: 'locality')
  Locality? locality;
  @JsonKey(name: 'geoLocation')
  GeoLocation? geoLocation;
  @JsonKey(name: 'auditDetails', includeIfNull: false)
  AuditDetails? auditDetails;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
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
class GeoLocation {
  @JsonKey(name: 'latitude')
  double? latitude;
  @JsonKey(name: 'longitude')
  double? longitude;
  @JsonKey(name: 'additionalDetails')
  double? additionalDetails;

  GeoLocation();

  factory GeoLocation.fromJson(Map<String, dynamic> json) =>
      _$GeoLocationFromJson(json);

  Map<String, dynamic> toJson() => _$GeoLocationToJson(this);
}

@JsonSerializable()
class Locality {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'label')
  String? label;
  @JsonKey(name: 'latitude')
  String? latitude;
  @JsonKey(name: 'longitude')
  String? longitude;
  @JsonKey(name: 'children')
  List<dynamic>? children;
  @JsonKey(name: 'materializedPath')
  String? materializedPath;

  Locality();

  factory Locality.fromJson(Map<String, dynamic> json) =>
      _$LocalityFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityToJson(this);
}

_boolToString(bool? value) {
  if (value == null) {
    return null;
  }
  return value.toString();
}

_stringToBool(value) {
  if (value == null) {
    return null;
  }

  if (value is String) {
    return value == 'true';
  }
  return value;
}

@JsonSerializable()
class Owner {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'ownerId')
  String? ownerId;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'correspondenceAddress')
  String? correspondenceAddress;
  @JsonKey(
    name: 'isPrimaryOwner',
    toJson: _boolToString,
    fromJson: _stringToBool,
  )
  bool? isPrimaryOwner;
  @JsonKey(name: 'ownerShipPercentage')
  String? ownerShipPercentage;
  @JsonKey(name: 'ownerType')
  String? ownerType;
  @JsonKey(name: 'status')
  bool? status;
  @JsonKey(name: 'institutionId')
  String? institutionId;
  @JsonKey(name: 'documents')
  String? documents;
  @JsonKey(name: 'relationship')
  String? relationship;
  @JsonKey(name: 'additionalDetails')
  String? additionalDetails;
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'password')
  String? password;
  @JsonKey(name: 'salutation')
  String? salutation;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'altContactNumber')
  String? altContactNumber;
  @JsonKey(name: 'pan')
  String? pan;
  @JsonKey(name: 'aadhaarNumber')
  String? aadhaarNumber;
  @JsonKey(name: 'permanentAddress')
  String? permanentAddress;
  @JsonKey(name: 'permanentCity')
  String? permanentCity;
  @JsonKey(name: 'permanentPinCode')
  String? permanentPinCode;
  @JsonKey(name: 'correspondenceCity')
  String? correspondenceCity;
  @JsonKey(name: 'correspondencePinCode')
  String? correspondencePinCode;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'dob')
  int? dob;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'locale')
  String? locale;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'signature')
  String? signature;
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'roles')
  List<Role>? roles;
  @JsonKey(name: 'bloodGroup')
  String? bloodGroup;
  @JsonKey(name: 'identificationMark')
  String? identificationMark;
  @JsonKey(name: 'photo')
  String? photo;
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'createdDate')
  String? createdDate;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'lastModifiedDate')
  String? lastModifiedDate;
  @JsonKey(name: 'otpReference')
  String? otpReference;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
}

@JsonSerializable()
class Role {
  @JsonKey(name: 'id')
  String? id;
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
class Unit {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'floorNo')
  String? floorNo;
  @JsonKey(name: 'unitType')
  String? unitType;
  @JsonKey(name: 'usageCategory')
  String? usageCategory;
  @JsonKey(name: 'occupancyType')
  String? occupancyType;
  @JsonKey(name: 'occupancyDate')
  int? occupancyDate;
  @JsonKey(name: 'additionalDetails')
  String? additionalDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  Unit();

  factory Unit.fromJson(Map<String, dynamic> json) => _$UnitFromJson(json);

  Map<String, dynamic> toJson() => _$UnitToJson(this);
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
