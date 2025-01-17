import 'package:json_annotation/json_annotation.dart';

part 'my_properties.g.dart';

@JsonSerializable()
class PtMyProperties {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'Properties')
  List<Property>? properties;
  @JsonKey(name: 'count')
  int? count;

  PtMyProperties();

  factory PtMyProperties.fromJson(Map<String, dynamic> json) =>
      _$PtMyPropertiesFromJson(json);

  Map<String, dynamic> toJson() => _$PtMyPropertiesToJson(this);
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
class Property {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'propertyId')
  String? propertyId;
  @JsonKey(name: 'surveyId')
  dynamic surveyId;
  @JsonKey(name: 'linkedProperties')
  dynamic linkedProperties;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'oldPropertyId')
  dynamic oldPropertyId;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'acknowldgementNumber')
  String? acknowledgementNumber;
  @JsonKey(name: 'propertyType')
  String? propertyType;
  @JsonKey(name: 'ownershipCategory')
  String? ownershipCategory;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'institution')
  dynamic institution;
  @JsonKey(name: 'creationReason')
  String? creationReason;
  @JsonKey(name: 'usageCategory')
  String? usageCategory;
  @JsonKey(name: 'noOfFloors')
  int? noOfFloors;
  @JsonKey(name: 'landArea')
  double? landArea;
  @JsonKey(name: 'superBuiltUpArea')
  dynamic superBuiltUpArea;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'units')
  List<Unit>? units;
  @JsonKey(name: 'dueAmount')
  dynamic dueAmount;
  @JsonKey(name: 'dueAmountYear')
  dynamic dueAmountYear;
  @JsonKey(name: 'additionalDetails')
  PropertyAdditionalDetails? additionalDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'workflow')
  Workflow? workflow;
  @JsonKey(name: 'AlternateUpdated')
  bool? alternateUpdated;
  @JsonKey(name: 'isOldDataEncryptionRequest')
  bool? isOldDataEncryptionRequest;

  Property();

  factory Property.fromJson(Map<String, dynamic> json) =>
      _$PropertyFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyToJson(this);
}

@JsonSerializable()
class PropertyAdditionalDetails {
  @JsonKey(name: 'uid', includeIfNull: false)
  String? uid;
  @JsonKey(name: 'basement1', includeIfNull: false)
  dynamic basement1;
  @JsonKey(name: 'basement2', includeIfNull: false)
  dynamic basement2;
  @JsonKey(name: 'noOfFloors', includeIfNull: false)
  dynamic noOfFloors;
  @JsonKey(name: 'builtUpArea', includeIfNull: false)
  dynamic builtUpArea;
  @JsonKey(name: 'electricity', includeIfNull: false)
  String? electricity;
  @JsonKey(name: 'inflammable', includeIfNull: false)
  bool? inflammable;
  @JsonKey(name: 'propertyType', includeIfNull: false)
  PropertyType? propertyType;
  @JsonKey(name: 'subusagetype', includeIfNull: false)
  dynamic subusagetype;
  // @JsonKey(name: 'ageOfProperty')
  // AgeOfProperty? ageOfProperty;
  @JsonKey(name: 'structureType', includeIfNull: false)
  dynamic structureType;
  @JsonKey(name: 'noOofBasements', includeIfNull: false)
  NoO? noOofBasements;
  @JsonKey(name: 'heightAbove36Feet', includeIfNull: false)
  bool? heightAbove36Feet;
  @JsonKey(name: 'Subusagetypeofrentedarea', includeIfNull: false)
  dynamic subusagetypeofrentedarea;
  @JsonKey(name: 'IsAnyPartOfThisFloorUnOccupied', includeIfNull: false)
  dynamic isAnyPartOfThisFloorUnOccupied;
  @JsonKey(name: 'owners', includeIfNull: false)
  List<OwnerAd>? owners;
  @JsonKey(name: 'applicationStatus', includeIfNull: false)
  String? applicationStatus;
  @JsonKey(name: 'isRainwaterHarvesting', includeIfNull: false)
  bool? isRainwaterHarvesting;

  PropertyAdditionalDetails();

  factory PropertyAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$PropertyAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyAdditionalDetailsToJson(this);
}

@JsonSerializable()
class AgeOfProperty {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'i18nKey')
  String? i18NKey;

  AgeOfProperty();

  factory AgeOfProperty.fromJson(Map<String, dynamic> json) =>
      _$AgeOfPropertyFromJson(json);

  Map<String, dynamic> toJson() => _$AgeOfPropertyToJson(this);
}

@JsonSerializable()
class NoO {
  @JsonKey(name: 'code')
  int? code;

  @JsonKey(name: 'i18nKey')
  String? i18NKey;

  NoO();

  factory NoO.fromJson(Map<String, dynamic> json) => _$NoOFromJson(json);

  Map<String, dynamic> toJson() => _$NoOToJson(this);
}

@JsonSerializable()
class PropertyType {
  @JsonKey(name: 'code')
  String? code;

  @JsonKey(name: 'i18nKey')
  String? i18NKey;

  PropertyType();

  factory PropertyType.fromJson(Map<String, dynamic> json) =>
      _$PropertyTypeFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyTypeToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'doorNo')
  String? doorNo;
  @JsonKey(name: 'plotNo')
  dynamic plotNo;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'landmark')
  String? landmark;
  @JsonKey(name: 'city')
  String? city;
  @JsonKey(name: 'district')
  dynamic district;
  @JsonKey(name: 'region')
  dynamic region;
  @JsonKey(name: 'state')
  dynamic state;
  @JsonKey(name: 'country')
  dynamic country;
  @JsonKey(name: 'pincode')
  dynamic pincode;
  @JsonKey(name: 'buildingName')
  dynamic buildingName;
  @JsonKey(name: 'street')
  String? street;
  @JsonKey(name: 'locality')
  Locality? locality;
  @JsonKey(name: 'geoLocation')
  GeoLocation? geoLocation;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;

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
  @JsonKey(name: 'children')
  List<dynamic>? children;
  @JsonKey(name: 'materializedPath')
  dynamic materializedPath;

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
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentUid')
  dynamic documentUid;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'status')
  String? status;

  Document();

  factory Document.fromJson(Map<String, dynamic> json) =>
      _$DocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentToJson(this);
}

@JsonSerializable()
class OwnerAd {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'documents')
  List<dynamic>? documents;
  @JsonKey(name: 'ownerType')
  String? ownerType;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'permanentAddress')
  String? permanentAddress;
  @JsonKey(name: 'additionalDetails')
  OwnerAdditionalDetailsPt? additionalDetails;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'sameAsPropertyAddress')
  bool? sameAsPropertyAddress;

  OwnerAd();

  factory OwnerAd.fromJson(Map<String, dynamic> json) =>
      _$OwnerAdFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerAdToJson(this);
}

@JsonSerializable()
class OwnerAdditionalDetailsPt {
  @JsonKey(name: 'ownerName')
  String? ownerName;
  @JsonKey(name: 'ownerSequence')
  int? ownerSequence;

  OwnerAdditionalDetailsPt();

  factory OwnerAdditionalDetailsPt.fromJson(Map<String, dynamic> json) =>
      _$OwnerAdditionalDetailsPtFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerAdditionalDetailsPtToJson(this);
}

@JsonSerializable()
class Owner {
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
  String? permanentAddress;
  @JsonKey(name: 'permanentCity')
  dynamic permanentCity;
  @JsonKey(name: 'permanentPinCode')
  dynamic permanentPinCode;
  @JsonKey(name: 'correspondenceCity')
  dynamic correspondenceCity;
  @JsonKey(name: 'correspondencePinCode')
  dynamic correspondencePinCode;
  @JsonKey(name: 'correspondenceAddress')
  dynamic correspondenceAddress;
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
  @JsonKey(name: 'alternatemobilenumber')
  dynamic alternatemobilenumber;
  @JsonKey(name: 'ownerInfoUuid')
  String? ownerInfoUuid;
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
  List<Document>? documents;
  @JsonKey(name: 'additionalDetails')
  OwnerAdditionalDetails? additionalDetails;
  @JsonKey(name: 'relationship')
  String? relationship;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
}

@JsonSerializable()
class OwnerAdditionalDetails {
  @JsonKey(name: 'ownerName')
  String? ownerName;
  @JsonKey(name: 'ownerSequence')
  int? ownerSequence;

  OwnerAdditionalDetails();

  factory OwnerAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$OwnerAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerAdditionalDetailsToJson(this);
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
class Unit {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  dynamic tenantId;
  @JsonKey(name: 'floorNo')
  int? floorNo;
  @JsonKey(name: 'unitType')
  dynamic unitType;
  @JsonKey(name: 'usageCategory')
  String? usageCategory;
  @JsonKey(name: 'occupancyType')
  String? occupancyType;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'occupancyDate')
  int? occupancyDate;
  @JsonKey(name: 'constructionDetail')
  ConstructionDetail? constructionDetail;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'arv')
  dynamic arv;

  Unit();

  factory Unit.fromJson(Map<String, dynamic> json) => _$UnitFromJson(json);

  Map<String, dynamic> toJson() => _$UnitToJson(this);
}

@JsonSerializable()
class ConstructionDetail {
  @JsonKey(name: 'carpetArea')
  dynamic carpetArea;
  @JsonKey(name: 'builtUpArea')
  int? builtUpArea;
  @JsonKey(name: 'plinthArea')
  dynamic plinthArea;
  @JsonKey(name: 'superBuiltUpArea')
  dynamic superBuiltUpArea;
  @JsonKey(name: 'constructionType')
  dynamic constructionType;
  @JsonKey(name: 'constructionDate')
  dynamic constructionDate;
  @JsonKey(name: 'dimensions')
  dynamic dimensions;

  ConstructionDetail();

  factory ConstructionDetail.fromJson(Map<String, dynamic> json) =>
      _$ConstructionDetailFromJson(json);

  Map<String, dynamic> toJson() => _$ConstructionDetailToJson(this);
}

@JsonSerializable()
class Workflow {
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'comment', includeIfNull: false)
  String? comment;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'moduleName')
  String? moduleName;
  @JsonKey(name: 'assignes', includeIfNull: false)
  List<Assignee>? assignees;
  @JsonKey(name: 'documents', includeIfNull: false)
  List<WorkflowDocument>? wfDocuments;

  Workflow();

  factory Workflow.fromJson(Map<String, dynamic> json) =>
      _$WorkflowFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowToJson(this);
}

@JsonSerializable()
class Assignee {
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'name')
  String? name;

  Assignee();

  factory Assignee.fromJson(Map<String, dynamic> json) =>
      _$AssigneeFromJson(json);

  Map<String, dynamic> toJson() => _$AssigneeToJson(this);
}

@JsonSerializable()
class WorkflowDocument {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileName')
  String? fileName;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  WorkflowDocument();

  factory WorkflowDocument.fromJson(Map<String, dynamic> json) =>
      _$WorkflowDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowDocumentToJson(this);
}
