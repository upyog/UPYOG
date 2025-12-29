import 'package:json_annotation/json_annotation.dart';

part 'emp_property_action_request_model.g.dart';

@JsonSerializable()
class PropertyActionRequest {
  @JsonKey(name: "id")
  String? id;
  @JsonKey(name: "propertyId")
  String? propertyId;
  @JsonKey(name: "surveyId")
  dynamic surveyId;
  @JsonKey(name: "linkedProperties")
  dynamic linkedProperties;
  @JsonKey(name: "tenantId")
  String? tenantId;
  @JsonKey(name: "accountId")
  String? accountId;
  @JsonKey(name: "oldPropertyId")
  dynamic oldPropertyId;
  @JsonKey(name: "status")
  String? status;
  @JsonKey(name: "address")
  Address? address;
  @JsonKey(name: "acknowldgementNumber")
  String? acknowldgementNumber;
  @JsonKey(name: "propertyType")
  String? propertyType;
  @JsonKey(name: "ownershipCategory")
  String? ownershipCategory;
  @JsonKey(name: "owners")
  List<Owner>? owners;
  @JsonKey(name: "institution")
  dynamic institution;
  @JsonKey(name: "creationReason")
  String? creationReason;
  @JsonKey(name: "usageCategory")
  String? usageCategory;
  @JsonKey(name: "noOfFloors")
  int? noOfFloors;
  @JsonKey(name: "landArea")
  int? landArea;
  @JsonKey(name: "superBuiltUpArea")
  dynamic superBuiltUpArea;
  @JsonKey(name: "source")
  String? source;
  @JsonKey(name: "channel")
  String? channel;
  @JsonKey(name: "documents")
  List<PropertyDocument>? documents;
  @JsonKey(name: "units")
  List<Unit>? units;
  @JsonKey(name: "dueAmount")
  dynamic dueAmount;
  @JsonKey(name: "dueAmountYear")
  dynamic dueAmountYear;
  @JsonKey(name: "additionalDetails")
  PropertyAdditionalDetails? additionalDetails;
  @JsonKey(name: "auditDetails")
  AuditDetails? auditDetails;
  @JsonKey(name: "workflow", includeIfNull: false)
  Workflow? workflow;
  @JsonKey(name: "AlternateUpdated")
  bool? alternateUpdated;
  @JsonKey(name: "isOldDataEncryptionRequest")
  bool? isOldDataEncryptionRequest;

  PropertyActionRequest();

  factory PropertyActionRequest.fromJson(Map<String, dynamic> json) =>
      _$PropertyActionRequestFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyActionRequestToJson(this);
}

@JsonSerializable()
class PropertyAdditionalDetails {
  @JsonKey(name: "uid")
  String? uid;
  @JsonKey(name: "remarks")
  String? remarks;
  @JsonKey(name: "basement1")
  dynamic basement1;
  @JsonKey(name: "basement2")
  dynamic basement2;
  @JsonKey(name: "noOfFloors")
  NoO? noOfFloors;
  @JsonKey(name: "builtUpArea")
  dynamic builtUpArea;
  @JsonKey(name: "caseDetails")
  String? caseDetails;
  @JsonKey(name: "electricity")
  String? electricity;
  @JsonKey(name: "inflammable")
  bool? inflammable;
  @JsonKey(name: "marketValue")
  int? marketValue;
  @JsonKey(name: "documentDate")
  int? documentDate;
  @JsonKey(name: "propertyType")
  PropertyType? propertyType;
  @JsonKey(name: "subusagetype")
  dynamic subusagetype;
  @JsonKey(name: "ageOfProperty")
  AgeOfProperty? ageOfProperty;
  @JsonKey(name: "documentValue")
  String? documentValue;
  @JsonKey(name: "structureType")
  AgeOfProperty? structureType;
  @JsonKey(name: "documentNumber")
  String? documentNumber;
  @JsonKey(name: "noOofBasements")
  NoO? noOofBasements;
  @JsonKey(name: "applicationStatus")
  String? applicationStatus;
  @JsonKey(name: "heightAbove36Feet")
  bool? heightAbove36Feet;
  @JsonKey(name: "isMutationInCourt")
  String? isMutationInCourt;
  @JsonKey(name: "reasonForTransfer")
  String? reasonForTransfer;
  @JsonKey(name: "previousPropertyUuid")
  String? previousPropertyUuid;
  @JsonKey(name: "Subusagetypeofrentedarea")
  dynamic subusagetypeofrentedarea;
  @JsonKey(name: "isPropertyUnderGovtPossession")
  String? isPropertyUnderGovtPossession;
  @JsonKey(name: "IsAnyPartOfThisFloorUnOccupied")
  dynamic isAnyPartOfThisFloorUnOccupied;

  PropertyAdditionalDetails();

  factory PropertyAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$PropertyAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyAdditionalDetailsToJson(this);
}

@JsonSerializable()
class AgeOfProperty {
  @JsonKey(name: "code")
  String? code;
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "active")
  bool? active;
  @JsonKey(name: "i18nKey")
  String? i18NKey;

  AgeOfProperty();

  factory AgeOfProperty.fromJson(Map<String, dynamic> json) =>
      _$AgeOfPropertyFromJson(json);

  Map<String, dynamic> toJson() => _$AgeOfPropertyToJson(this);
}

@JsonSerializable()
class NoO {
  @JsonKey(name: "code")
  int? code;
  @JsonKey(name: "i18nKey")
  String? i18NKey;

  NoO();

  factory NoO.fromJson(Map<String, dynamic> json) => _$NoOFromJson(json);

  Map<String, dynamic> toJson() => _$NoOToJson(this);
}

@JsonSerializable()
class PropertyType {
  @JsonKey(name: "code")
  String? code;
  @JsonKey(name: "i18nKey")
  String? i18NKey;

  PropertyType();

  factory PropertyType.fromJson(Map<String, dynamic> json) =>
      _$PropertyTypeFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyTypeToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: "tenantId")
  String? tenantId;
  @JsonKey(name: "doorNo")
  String? doorNo;
  @JsonKey(name: "plotNo")
  dynamic plotNo;
  @JsonKey(name: "id")
  String? id;
  @JsonKey(name: "landmark")
  String? landmark;
  @JsonKey(name: "city")
  String? city;
  @JsonKey(name: "district")
  dynamic district;
  @JsonKey(name: "region")
  dynamic region;
  @JsonKey(name: "state")
  dynamic state;
  @JsonKey(name: "country")
  dynamic country;
  @JsonKey(name: "pincode")
  String? pincode;
  @JsonKey(name: "buildingName")
  dynamic buildingName;
  @JsonKey(name: "street")
  String? street;
  @JsonKey(name: "locality")
  Locality? locality;
  @JsonKey(name: "geoLocation")
  GeoLocation? geoLocation;
  @JsonKey(name: "additionalDetails")
  dynamic additionalDetails;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class GeoLocation {
  @JsonKey(name: "latitude")
  int? latitude;
  @JsonKey(name: "longitude")
  int? longitude;

  GeoLocation();

  factory GeoLocation.fromJson(Map<String, dynamic> json) =>
      _$GeoLocationFromJson(json);

  Map<String, dynamic> toJson() => _$GeoLocationToJson(this);
}

@JsonSerializable()
class Locality {
  @JsonKey(name: "code")
  String? code;
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "label")
  String? label;
  @JsonKey(name: "latitude")
  dynamic latitude;
  @JsonKey(name: "longitude")
  dynamic longitude;
  @JsonKey(name: "area")
  String? area;
  @JsonKey(name: "children")
  List<dynamic>? children;
  @JsonKey(name: "materializedPath")
  dynamic materializedPath;

  Locality();

  factory Locality.fromJson(Map<String, dynamic> json) =>
      _$LocalityFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: "createdBy")
  String? createdBy;
  @JsonKey(name: "lastModifiedBy")
  String? lastModifiedBy;
  @JsonKey(name: "createdTime")
  int? createdTime;
  @JsonKey(name: "lastModifiedTime")
  int? lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class PropertyDocument {
  @JsonKey(name: "id")
  String? id;
  @JsonKey(name: "documentType")
  String? documentType;
  @JsonKey(name: "fileStoreId")
  String? fileStoreId;
  @JsonKey(name: "documentUid")
  dynamic documentUid;
  @JsonKey(name: "auditDetails")
  dynamic auditDetails;
  @JsonKey(name: "status")
  String? status;

  PropertyDocument();

  factory PropertyDocument.fromJson(Map<String, dynamic> json) =>
      _$PropertyDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyDocumentToJson(this);
}

@JsonSerializable()
class Owner {
  @JsonKey(name: "id")
  dynamic id;
  @JsonKey(name: "uuid")
  String? uuid;
  @JsonKey(name: "userName")
  String? userName;
  @JsonKey(name: "password")
  dynamic password;
  @JsonKey(name: "salutation")
  dynamic salutation;
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "gender")
  String? gender;
  @JsonKey(name: "mobileNumber")
  String? mobileNumber;
  @JsonKey(name: "emailId")
  String? emailId;
  @JsonKey(name: "altContactNumber")
  dynamic altContactNumber;
  @JsonKey(name: "pan")
  dynamic pan;
  @JsonKey(name: "aadhaarNumber")
  dynamic aadhaarNumber;
  @JsonKey(name: "permanentAddress")
  String? permanentAddress;
  @JsonKey(name: "permanentCity")
  dynamic permanentCity;
  @JsonKey(name: "permanentPinCode")
  dynamic permanentPinCode;
  @JsonKey(name: "correspondenceCity")
  dynamic correspondenceCity;
  @JsonKey(name: "correspondencePinCode")
  dynamic correspondencePinCode;
  @JsonKey(name: "correspondenceAddress")
  dynamic correspondenceAddress;
  @JsonKey(name: "active")
  bool? active;
  @JsonKey(name: "dob")
  dynamic dob;
  @JsonKey(name: "pwdExpiryDate")
  int? pwdExpiryDate;
  @JsonKey(name: "locale")
  dynamic locale;
  @JsonKey(name: "type")
  String? type;
  @JsonKey(name: "signature")
  dynamic signature;
  @JsonKey(name: "accountLocked")
  bool? accountLocked;
  @JsonKey(name: "roles")
  List<Role>? roles;
  @JsonKey(name: "fatherOrHusbandName")
  String? fatherOrHusbandName;
  @JsonKey(name: "bloodGroup")
  dynamic bloodGroup;
  @JsonKey(name: "identificationMark")
  dynamic identificationMark;
  @JsonKey(name: "photo")
  dynamic photo;
  @JsonKey(name: "createdBy")
  String? createdBy;
  @JsonKey(name: "createdDate")
  int? createdDate;
  @JsonKey(name: "lastModifiedBy")
  String? lastModifiedBy;
  @JsonKey(name: "lastModifiedDate")
  int? lastModifiedDate;
  @JsonKey(name: "tenantId")
  String? tenantId;
  @JsonKey(name: "alternatemobilenumber")
  dynamic alternatemobilenumber;
  @JsonKey(name: "ownerInfoUuid")
  String? ownerInfoUuid;
  @JsonKey(name: "isPrimaryOwner")
  dynamic isPrimaryOwner;
  @JsonKey(name: "ownerShipPercentage")
  dynamic ownerShipPercentage;
  @JsonKey(name: "ownerType")
  String? ownerType;
  @JsonKey(name: "institutionId")
  dynamic institutionId;
  @JsonKey(name: "status")
  String? status;
  @JsonKey(name: "documents")
  List<PropertyDocument>? documents;
  @JsonKey(name: "additionalDetails")
  OwnerAdditionalDetails? additionalDetails;
  @JsonKey(name: "relationship")
  String? relationship;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
}

@JsonSerializable()
class OwnerAdditionalDetails {
  @JsonKey(name: "ownerName")
  String? ownerName;
  @JsonKey(name: "ownerSequence")
  int? ownerSequence;

  OwnerAdditionalDetails();

  factory OwnerAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$OwnerAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerAdditionalDetailsToJson(this);
}

@JsonSerializable()
class Role {
  @JsonKey(name: "id")
  dynamic id;
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "code")
  String? code;
  @JsonKey(name: "tenantId")
  String? tenantId;

  Role();

  factory Role.fromJson(Map<String, dynamic> json) => _$RoleFromJson(json);

  Map<String, dynamic> toJson() => _$RoleToJson(this);
}

@JsonSerializable()
class Unit {
  @JsonKey(name: "id")
  String? id;
  @JsonKey(name: "tenantId")
  dynamic tenantId;
  @JsonKey(name: "floorNo")
  int? floorNo;
  @JsonKey(name: "unitType")
  dynamic unitType;
  @JsonKey(name: "usageCategory")
  String? usageCategory;
  @JsonKey(name: "occupancyType")
  String? occupancyType;
  @JsonKey(name: "active")
  bool? active;
  @JsonKey(name: "occupancyDate")
  int? occupancyDate;
  @JsonKey(name: "constructionDetail")
  ConstructionDetail? constructionDetail;
  @JsonKey(name: "additionalDetails")
  dynamic additionalDetails;
  @JsonKey(name: "auditDetails")
  dynamic auditDetails;
  @JsonKey(name: "arv")
  dynamic arv;

  Unit();

  factory Unit.fromJson(Map<String, dynamic> json) => _$UnitFromJson(json);

  Map<String, dynamic> toJson() => _$UnitToJson(this);
}

@JsonSerializable()
class ConstructionDetail {
  @JsonKey(name: "carpetArea")
  dynamic carpetArea;
  @JsonKey(name: "builtUpArea")
  int? builtUpArea;
  @JsonKey(name: "plinthArea")
  dynamic plinthArea;
  @JsonKey(name: "superBuiltUpArea")
  dynamic superBuiltUpArea;
  @JsonKey(name: "constructionType")
  dynamic constructionType;
  @JsonKey(name: "constructionDate")
  dynamic constructionDate;
  @JsonKey(name: "dimensions")
  dynamic dimensions;

  ConstructionDetail();

  factory ConstructionDetail.fromJson(Map<String, dynamic> json) =>
      _$ConstructionDetailFromJson(json);

  Map<String, dynamic> toJson() => _$ConstructionDetailToJson(this);
}

@JsonSerializable()
class Workflow {
  @JsonKey(name: "action")
  String? action;
  @JsonKey(name: "comment")
  String? comment;
  @JsonKey(name: "businessService")
  String? businessService;
  @JsonKey(name: "moduleName")
  String? moduleName;
  @JsonKey(name: "assignes")
  List<Assignee>? assignes;
  @JsonKey(name: "documents")
  List<WorkflowDocument>? documents;

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
  @JsonKey(name: "documentType")
  String? documentType;
  @JsonKey(name: "fileName")
  String? fileName;
  @JsonKey(name: "fileStoreId")
  String? fileStoreId;

  WorkflowDocument();

  factory WorkflowDocument.fromJson(Map<String, dynamic> json) =>
      _$WorkflowDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowDocumentToJson(this);
}
