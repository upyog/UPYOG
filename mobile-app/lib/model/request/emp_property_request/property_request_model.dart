import 'package:json_annotation/json_annotation.dart';

part 'property_request_model.g.dart';

@JsonSerializable()
class PropertyRequest {
  @JsonKey(name: "tenantId")
  String? tenantId;
  @JsonKey(name: "address")
  AddressRequest? address;
  @JsonKey(name: "usageCategory")
  String? usageCategory;
  @JsonKey(name: "usageCategoryMajor")
  String? usageCategoryMajor;
  @JsonKey(name: "usageCategoryMinor")
  String? usageCategoryMinor;
  @JsonKey(name: "landArea")
  int? landArea;
  @JsonKey(name: "superBuiltUpArea")
  int? superBuiltUpArea;
  @JsonKey(name: "propertyType")
  String? propertyType;
  @JsonKey(name: "noOfFloors")
  int? noOfFloors;
  @JsonKey(name: "ownershipCategory")
  String? ownershipCategory;
  @JsonKey(name: "additionalDetails")
  PropertyRequestAdditionalDetails? additionalDetails;
  @JsonKey(name: "owners")
  List<OwnerRequest>? owners;
  @JsonKey(name: "channel")
  String? channel;
  @JsonKey(name: "creationReason")
  String? creationReason;
  @JsonKey(name: "source")
  String? source;
  @JsonKey(name: "units")
  List<PropertyRequestUnit>? units;
  @JsonKey(name: "documents")
  List<DocumentRequest>? documents;
  @JsonKey(name: "applicationStatus")
  String? applicationStatus;
  @JsonKey(name: "institution", includeIfNull: false)
  InstitutionRequest? institution;

  PropertyRequest();

  factory PropertyRequest.fromJson(Map<String, dynamic> json) =>
      _$PropertyRequestFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyRequestToJson(this);
}

@JsonSerializable()
class PropertyRequestAdditionalDetails {
  @JsonKey(name: "primaryOwner")
  String? primaryOwner;
  @JsonKey(name: "unit")
  List<AdditionalDetailsUnitRequest>? unit;
  @JsonKey(name: "ageOfProperty")
  AgeOfPropertyRequest? ageOfProperty;
  @JsonKey(name: "structureType")
  AgeOfPropertyRequest? structureType;
  @JsonKey(name: "electricity")
  String? electricity;
  @JsonKey(name: "uid")
  String? uid;
  @JsonKey(name: "owners")
  List<OwnerRequest>? owners;

  PropertyRequestAdditionalDetails();

  factory PropertyRequestAdditionalDetails.fromJson(
    Map<String, dynamic> json,
  ) =>
      _$PropertyRequestAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() =>
      _$PropertyRequestAdditionalDetailsToJson(this);
}

@JsonSerializable()
class AgeOfPropertyRequest {
  @JsonKey(name: "i18nKey")
  String? i18NKey;
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "code")
  String? code;
  @JsonKey(name: "active")
  bool? active;

  AgeOfPropertyRequest();

  factory AgeOfPropertyRequest.fromJson(Map<String, dynamic> json) =>
      _$AgeOfPropertyRequestFromJson(json);

  Map<String, dynamic> toJson() => _$AgeOfPropertyRequestToJson(this);
}

@JsonSerializable()
class OwnerRequest {
  @JsonKey(name: "name", includeIfNull: false)
  String? name;
  @JsonKey(name: "gender", includeIfNull: false)
  String? gender;
  @JsonKey(name: "fatherOrHusbandName", includeIfNull: false)
  String? fatherOrHusbandName;
  @JsonKey(name: "relationship", includeIfNull: false)
  String? relationship;
  @JsonKey(name: "mobileNumber", includeIfNull: false)
  String? mobileNumber;
  @JsonKey(name: "designation", includeIfNull: false)
  String? designation;
  @JsonKey(name: "altContactNumber", includeIfNull: false)
  String? altContactNumber;
  @JsonKey(name: "emailId", includeIfNull: false)
  String? emailId;
  @JsonKey(name: "correspondenceAddress", includeIfNull: false)
  String? correspondenceAddress;
  @JsonKey(name: "isCorrespondenceAddress", includeIfNull: false)
  bool? isCorrespondenceAddress;
  @JsonKey(name: "ownerType", includeIfNull: false)
  String? ownerType;
  @JsonKey(name: "additionalDetails", includeIfNull: false)
  OwnerAdditionalDetailsRequest? additionalDetails;
  @JsonKey(name: "documents", includeIfNull: false)
  List<DocumentRequest>? documents;

  OwnerRequest();

  factory OwnerRequest.fromJson(Map<String, dynamic> json) =>
      _$OwnerRequestFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerRequestToJson(this);
}

@JsonSerializable()
class OwnerAdditionalDetailsRequest {
  @JsonKey(name: "ownerSequence")
  int? ownerSequence;
  @JsonKey(name: "ownerName")
  String? ownerName;

  OwnerAdditionalDetailsRequest();

  factory OwnerAdditionalDetailsRequest.fromJson(Map<String, dynamic> json) =>
      _$OwnerAdditionalDetailsRequestFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerAdditionalDetailsRequestToJson(this);
}

@JsonSerializable()
class DocumentRequest {
  @JsonKey(name: "documentType")
  String? documentType;
  @JsonKey(name: "fileStoreId")
  String? fileStoreId;
  @JsonKey(name: "documentUid")
  String? documentUid;

  DocumentRequest();

  factory DocumentRequest.fromJson(Map<String, dynamic> json) =>
      _$DocumentRequestFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentRequestToJson(this);
}

@JsonSerializable()
class AdditionalDetailsUnitRequest {
  @JsonKey(name: "RentedMonths")
  String? rentedMonths;
  @JsonKey(name: "NonRentedMonthsUsage")
  dynamic nonRentedMonthsUsage;
  @JsonKey(name: "floorNo")
  dynamic floorNo;

  AdditionalDetailsUnitRequest({
    this.rentedMonths,
    this.nonRentedMonthsUsage,
    this.floorNo,
  });

  factory AdditionalDetailsUnitRequest.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsUnitRequestFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsUnitRequestToJson(this);
}

@JsonSerializable()
class AddressRequest {
  @JsonKey(name: "pincode", includeIfNull: false)
  String? pincode;
  @JsonKey(name: "city")
  String? city;
  @JsonKey(name: "locality")
  LocalityRequest? locality;
  @JsonKey(name: "street", includeIfNull: false)
  String? street;
  @JsonKey(name: "doorNo", includeIfNull: false)
  String? doorNo;

  AddressRequest();

  factory AddressRequest.fromJson(Map<String, dynamic> json) =>
      _$AddressRequestFromJson(json);

  Map<String, dynamic> toJson() => _$AddressRequestToJson(this);
}

@JsonSerializable()
class LocalityRequest {
  @JsonKey(name: "code")
  String? code;
  @JsonKey(name: "area")
  String? area;

  LocalityRequest();

  factory LocalityRequest.fromJson(Map<String, dynamic> json) =>
      _$LocalityRequestFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityRequestToJson(this);
}

@JsonSerializable()
class InstitutionRequest {
  @JsonKey(name: "name")
  String? name;
  @JsonKey(name: "type")
  String? type;
  @JsonKey(name: "designation")
  String? designation;
  @JsonKey(name: "nameOfAuthorizedPerson")
  String? nameOfAuthorizedPerson;
  @JsonKey(name: "tenantId")
  String? tenantId;

  InstitutionRequest();

  factory InstitutionRequest.fromJson(Map<String, dynamic> json) =>
      _$InstitutionRequestFromJson(json);

  Map<String, dynamic> toJson() => _$InstitutionRequestToJson(this);
}

@JsonSerializable()
class PropertyRequestUnit {
  @JsonKey(name: "occupancyType", includeIfNull: false)
  String? occupancyType;
  @JsonKey(name: "rentedMonths", includeIfNull: false)
  String? rentedMonths;
  @JsonKey(name: "floorNo", includeIfNull: false)
  String? floorNo;
  @JsonKey(name: "constructionDetail", includeIfNull: false)
  ConstructionDetailRequest? constructionDetail;
  @JsonKey(name: "tenantId", includeIfNull: false)
  String? tenantId;
  @JsonKey(name: "usageCategory", includeIfNull: false)
  String? usageCategory;
  @JsonKey(name: "arv", includeIfNull: false)
  String? arv;

  PropertyRequestUnit();

  factory PropertyRequestUnit.fromJson(Map<String, dynamic> json) =>
      _$PropertyRequestUnitFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyRequestUnitToJson(this);
}

@JsonSerializable()
class ConstructionDetailRequest {
  @JsonKey(name: "builtUpArea")
  String? builtUpArea;

  ConstructionDetailRequest();

  factory ConstructionDetailRequest.fromJson(Map<String, dynamic> json) =>
      _$ConstructionDetailRequestFromJson(json);

  Map<String, dynamic> toJson() => _$ConstructionDetailRequestToJson(this);
}
