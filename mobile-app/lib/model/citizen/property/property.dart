import 'package:json_annotation/json_annotation.dart';

part 'property.g.dart';

@JsonSerializable()
class Properties {
  @JsonKey(name: 'Properties')
  List<Property>? properties;
  @JsonKey(name: 'count')
  int? count;

  Properties();

  factory Properties.fromJson(Map<String, dynamic> json) =>
      _$PropertiesFromJson(json);

  Map<String, dynamic> toJson() => _$PropertiesToJson(this);
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
  dynamic usageCategory;
  @JsonKey(name: 'noOfFloors')
  int? noOfFloors;
  @JsonKey(name: 'landArea')
  double? landArea;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'superBuiltUpArea')
  dynamic superBuiltUpArea;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'units')
  dynamic units;
  @JsonKey(name: 'dueAmount')
  dynamic dueAmount;
  @JsonKey(name: 'dueAmountYear')
  dynamic dueAmountYear;
  @JsonKey(name: '')
  PropertyAdditionalDetails? additionalDetails;
  @JsonKey(name: 'additionalDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'workflow')
  dynamic workflow;
  @JsonKey(name: 'alternateUpdated')
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
  @JsonKey(name: 'uid')
  String? uid;
  @JsonKey(name: 'basement1')
  dynamic basement1;
  @JsonKey(name: 'basement2')
  dynamic basement2;
  @JsonKey(name: 'builtUpArea')
  dynamic builtUpArea;
  @JsonKey(name: 'electricity')
  String? electricity;
  @JsonKey(name: 'inflammable')
  bool? inflammable;
  @JsonKey(name: 'propertyType')
  PropertyType? propertyType;
  @JsonKey(name: 'subusagetype')
  dynamic subusagetype;
  @JsonKey(name: 'heightAbove36Feet')
  bool? heightAbove36Feet;
  @JsonKey(name: 'subusagetypeofrentedarea')
  dynamic subusagetypeofrentedarea;
  @JsonKey(name: 'isAnyPartOfThisFloorUnOccupied')
  dynamic isAnyPartOfThisFloorUnOccupied;

  PropertyAdditionalDetails();

  factory PropertyAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$PropertyAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyAdditionalDetailsToJson(this);
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
  dynamic landmark;
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
  dynamic pinCode;
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
  double? latitude;
  @JsonKey(name: 'longitude')
  double? longitude;

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
  dynamic alternateMobileNumber;
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
