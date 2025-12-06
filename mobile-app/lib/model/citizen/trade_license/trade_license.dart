import 'package:json_annotation/json_annotation.dart';

part 'trade_license.g.dart';

@JsonSerializable()
class TradeLicense {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'Licenses')
  List<License>? licenses;
  @JsonKey(name: 'Count')
  int? count;
  @JsonKey(name: 'applicationsIssued')
  int? applicationsIssued;
  @JsonKey(name: 'applicationsRenewed')
  int? applicationsRenewed;
  @JsonKey(name: 'applicationValidity')
  int? applicationValidity;

  TradeLicense();

  factory TradeLicense.fromJson(Map<String, dynamic> json) =>
      _$TradeLicenseFromJson(json);

  Map<String, dynamic> toJson() => _$TradeLicenseToJson(this);
}

@JsonSerializable()
class License {
  @JsonKey(name: 'comment')
  String? comment;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'licenseType')
  String? licenseType;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'workflowCode')
  String? workflowCode;
  @JsonKey(name: 'licenseNumber')
  dynamic licenseNumber;
  @JsonKey(name: 'applicationNumber')
  String? applicationNumber;
  @JsonKey(name: 'oldLicenseNumber')
  dynamic oldLicenseNumber;
  @JsonKey(name: 'propertyId')
  dynamic propertyId;
  @JsonKey(name: 'oldPropertyId')
  dynamic oldPropertyId;
  @JsonKey(name: 'accountId')
  dynamic accountId;
  @JsonKey(name: 'tradeName')
  String? tradeName;
  @JsonKey(name: 'applicationDate')
  int? applicationDate;
  @JsonKey(name: 'commencementDate')
  int? commencementDate;
  @JsonKey(name: 'issuedDate')
  dynamic issuedDate;
  @JsonKey(name: 'financialYear')
  String? financialYear;
  @JsonKey(name: 'validFrom')
  int? validFrom;
  @JsonKey(name: 'validTo')
  int? validTo;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'assignee')
  List<String>? assignee;
  @JsonKey(name: 'wfDocuments')
  List<WfDocument>? wfDocuments;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'tradeLicenseDetail')
  TradeLicenseDetail? tradeLicenseDetail;
  @JsonKey(name: 'calculation')
  dynamic calculation;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'fileStoreId')
  dynamic fileStoreId;

  License();

  factory License.fromJson(Map<String, dynamic> json) =>
      _$LicenseFromJson(json);

  Map<String, dynamic> toJson() => _$LicenseToJson(this);
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
  @JsonKey(name: 'surveyNo')
  dynamic surveyNo;
  @JsonKey(name: 'subOwnerShipCategory')
  String? subOwnerShipCategory;
  @JsonKey(name: 'structureType')
  String? structureType;
  @JsonKey(name: 'operationalArea')
  double? operationalArea;
  @JsonKey(name: 'noOfEmployees')
  int? noOfEmployees;
  @JsonKey(name: 'adhocExemption')
  dynamic adhocExemption;
  @JsonKey(name: 'adhocPenalty')
  dynamic adhocPenalty;
  @JsonKey(name: 'adhocExemptionReason')
  dynamic adhocExemptionReason;
  @JsonKey(name: 'adhocPenaltyReason')
  dynamic adhocPenaltyReason;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'tradeUnits')
  List<TradeUnit>? tradeUnits;
  @JsonKey(name: 'accessories', includeIfNull: true)
  List<Accessories?>? accessories;
  @JsonKey(name: 'applicationDocuments')
  List<ApplicationDocument>? applicationDocuments;
  @JsonKey(name: 'verificationDocuments')
  dynamic verificationDocuments;
  @JsonKey(name: 'additionalDetail')
  AdditionalDetail? additionalDetail;
  @JsonKey(name: 'institution')
  dynamic institution;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  TradeLicenseDetail();

  factory TradeLicenseDetail.fromJson(Map<String, dynamic> json) =>
      _$TradeLicenseDetailFromJson(json);

  Map<String, dynamic> toJson() => _$TradeLicenseDetailToJson(this);
}

@JsonSerializable()
class Accessories {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'accessoryCategory')
  String? accessoryCategory;
  @JsonKey(name: 'uom')
  String? uom;
  @JsonKey(name: 'uomValue')
  String? uomValue;
  @JsonKey(name: 'count')
  int? count;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;

  Accessories();

  factory Accessories.fromJson(Map<String, dynamic> json) =>
      _$AccessoriesFromJson(json);

  Map<String, dynamic> toJson() => _$AccessoriesToJson(this);
}

@JsonSerializable()
class AdditionalDetail {
  @JsonKey(name: 'propertyId')
  String? propertyId;
  @JsonKey(name: 'tradeGstNo')
  String? tradeGstNo;
  @JsonKey(name: 'isSameAsPropertyOwner')
  String? isSameAsPropertyOwner;

  AdditionalDetail();

  factory AdditionalDetail.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'doorNo')
  String? doorNo;
  @JsonKey(name: 'latitude')
  dynamic latitude;
  @JsonKey(name: 'longitude')
  dynamic longitude;
  @JsonKey(name: 'addressId')
  dynamic addressId;
  @JsonKey(name: 'addressNumber')
  dynamic addressNumber;
  @JsonKey(name: 'type')
  dynamic type;
  @JsonKey(name: 'addressLine1')
  dynamic addressLine1;
  @JsonKey(name: 'addressLine2')
  dynamic addressLine2;
  @JsonKey(name: 'landmark')
  dynamic landmark;
  @JsonKey(name: 'city')
  String? city;
  @JsonKey(name: 'pincode')
  dynamic pincode;
  @JsonKey(name: 'detail')
  dynamic detail;
  @JsonKey(name: 'buildingName')
  dynamic buildingName;
  @JsonKey(name: 'street')
  String? street;
  @JsonKey(name: 'locality')
  Locality? locality;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
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
  @JsonKey(name: 'documentUid')
  dynamic documentUid;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;

  ApplicationDocument();

  factory ApplicationDocument.fromJson(Map<String, dynamic> json) =>
      _$ApplicationDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$ApplicationDocumentToJson(this);
}

@JsonSerializable()
class Owner {
  @JsonKey(name: 'id')
  int? id;
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
  List<Roles>? roles;
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
  @JsonKey(name: 'otpReference')
  dynamic otpReference;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'isPrimaryOwner')
  dynamic isPrimaryOwner;
  @JsonKey(name: 'ownerShipPercentage')
  dynamic ownerShipPercentage;
  @JsonKey(name: 'ownerType')
  String? ownerType;
  @JsonKey(name: 'institutionId')
  dynamic institutionId;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'documents')
  dynamic documents;
  @JsonKey(name: 'userActive')
  bool? userActive;
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
  @JsonKey(name: 'uom')
  String? uom;
  @JsonKey(name: 'uomValue')
  String? uomValue;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;

  TradeUnit();

  factory TradeUnit.fromJson(Map<String, dynamic> json) =>
      _$TradeUnitFromJson(json);

  Map<String, dynamic> toJson() => _$TradeUnitToJson(this);
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
