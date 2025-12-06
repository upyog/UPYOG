import 'package:json_annotation/json_annotation.dart';

part 'fire_noc.g.dart';

@JsonSerializable()
class FireNocModel {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'FireNOCs')
  List<FireNoc>? fireNoCs;

  FireNocModel();

  factory FireNocModel.fromJson(Map<String, dynamic> json) =>
      _$FireNocModelFromJson(json);

  Map<String, dynamic> toJson() => _$FireNocModelToJson(this);
}

@JsonSerializable()
class FireNoc {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'fireNOCNumber', includeIfNull: false)
  String? fireNocNumber;
  @JsonKey(name: 'fireNOCDetails')
  FireNocDetails? fireNocDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'provisionFireNOCNumber', includeIfNull: false)
  String? provisionFireNocNumber;

  FireNoc();

  factory FireNoc.fromJson(Map<String, dynamic> json) =>
      _$FireNocFromJson(json);

  Map<String, dynamic> toJson() => _$FireNocToJson(this);
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
class FireNocDetails {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'applicationNumber')
  String? applicationNumber;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'fireNOCType')
  String? fireNocType;
  @JsonKey(name: 'firestationId')
  String? firestationId;
  @JsonKey(name: 'applicationDate')
  int? applicationDate;
  @JsonKey(name: 'financialYear')
  String? financialYear;
  @JsonKey(name: 'issuedDate', includeIfNull: false)
  int? issuedDate;
  @JsonKey(name: 'validFrom', includeIfNull: false)
  int? validFrom;
  @JsonKey(name: 'validTo', includeIfNull: false)
  int? validTo;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'channel')
  String? channel;
  @JsonKey(name: 'noOfBuildings')
  String? noOfBuildings;
  @JsonKey(name: 'buildings')
  List<Building>? buildings;
  @JsonKey(name: 'propertyDetails')
  PropertyDetails? propertyDetails;
  @JsonKey(name: 'applicantDetails')
  ApplicantDetails? applicantDetails;
  @JsonKey(name: 'additionalDetail')
  AdditionalDetail? additionalDetail;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  FireNocDetails();

  factory FireNocDetails.fromJson(Map<String, dynamic> json) =>
      _$FireNocDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$FireNocDetailsToJson(this);
}

@JsonSerializable()
class AdditionalDetail {
  @JsonKey(name: 'comment')
  String? comment;
  @JsonKey(name: 'assignee', includeIfNull: false)
  List<String>? assignee;
  @JsonKey(name: 'documents')
  List<PurpleDocument>? documents;
  @JsonKey(name: 'wfDocuments')
  List<WfDocument>? wfDocuments;
  @JsonKey(name: 'ownerAuditionalDetail')
  DitionalDetail? ownerAuditionalDetail;

  AdditionalDetail();

  factory AdditionalDetail.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailToJson(this);
}

@JsonSerializable()
class PurpleDocument {
  @JsonKey(name: 'link')
  String? link;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'title')
  String? title;
  @JsonKey(name: 'linkText')
  String? linkText;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  PurpleDocument();

  factory PurpleDocument.fromJson(Map<String, dynamic> json) =>
      _$PurpleDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$PurpleDocumentToJson(this);
}

@JsonSerializable()
class DitionalDetail {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'documents')
  List<OwnerAuditionalDetailDocument>? documents;
  @JsonKey(name: 'institutionName', includeIfNull: false)
  String? institutionName;
  @JsonKey(name: 'telephoneNumber', includeIfNull: false)
  String? telephoneNumber;
  @JsonKey(name: 'institutionDesignation', includeIfNull: false)
  String? institutionDesignation;

  DitionalDetail();

  factory DitionalDetail.fromJson(Map<String, dynamic> json) =>
      _$DitionalDetailFromJson(json);

  Map<String, dynamic> toJson() => _$DitionalDetailToJson(this);
}

@JsonSerializable()
class OwnerAuditionalDetailDocument {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentType')
  String? documentType;

  OwnerAuditionalDetailDocument();

  factory OwnerAuditionalDetailDocument.fromJson(Map<String, dynamic> json) =>
      _$OwnerAuditionalDetailDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerAuditionalDetailDocumentToJson(this);
}

@JsonSerializable()
class ApplicantDetails {
  @JsonKey(name: 'ownerShipType')
  String? ownerShipType;
  @JsonKey(name: 'owners')
  List<Owner>? owners;
  @JsonKey(name: 'additionalDetail')
  DitionalDetail? additionalDetail;

  ApplicantDetails();

  factory ApplicantDetails.fromJson(Map<String, dynamic> json) =>
      _$ApplicantDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$ApplicantDetailsToJson(this);
}

@JsonSerializable()
class Owner {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'useruuid')
  String? useruuid;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'ownerType')
  String? ownerType;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'accountLocked')
  bool? accountLocked;
  @JsonKey(name: 'accountLockedDate')
  int? accountLockedDate;
  @JsonKey(name: 'createdBy')
  int? createdBy;
  @JsonKey(name: 'lastModifiedBy')
  int? lastModifiedBy;
  @JsonKey(name: 'roles')
  List<Role>? roles;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'createdDate')
  int? createdDate;
  @JsonKey(name: 'lastModifiedDate')
  int? lastModifiedDate;
  @JsonKey(name: 'dob')
  int? dob;
  @JsonKey(name: 'pwdExpiryDate')
  int? pwdExpiryDate;
  @JsonKey(name: 'relationship')
  String? relationship;
  @JsonKey(name: 'fatherOrHusbandName')
  String? fatherOrHusbandName;
  @JsonKey(name: 'gender')
  String? gender;
  @JsonKey(name: 'correspondenceAddress')
  String? correspondenceAddress;
  @JsonKey(name: 'pan', includeIfNull: false)
  String? pan;
  @JsonKey(name: 'permanentAddress', includeIfNull: false)
  String? permanentAddress;

  Owner();

  factory Owner.fromJson(Map<String, dynamic> json) => _$OwnerFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerToJson(this);
}

@JsonSerializable()
class Role {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  Role();

  factory Role.fromJson(Map<String, dynamic> json) => _$RoleFromJson(json);

  Map<String, dynamic> toJson() => _$RoleToJson(this);
}

@JsonSerializable()
class Building {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'usageType')
  String? usageType;
  @JsonKey(name: 'uoms')
  List<Uom>? uoms;
  @JsonKey(name: 'uomsMap', includeIfNull: false)
  Map<String, dynamic>? uomsMap;
  @JsonKey(name: 'applicationDocuments')
  List<dynamic>? applicationDocuments;

  Building();

  factory Building.fromJson(Map<String, dynamic> json) =>
      _$BuildingFromJson(json);

  Map<String, dynamic> toJson() => _$BuildingToJson(this);
}

@JsonSerializable()
class Uom {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'value')
  int? value;
  @JsonKey(name: 'isActiveUom')
  bool? isActiveUom;
  @JsonKey(name: 'active')
  bool? active;

  Uom();

  factory Uom.fromJson(Map<String, dynamic> json) => _$UomFromJson(json);

  Map<String, dynamic> toJson() => _$UomToJson(this);
}

// @JsonSerializable()
// class UomsMap {
//   Map<String, dynamic>? mapData;

//   UomsMap({this.mapData});

//   factory UomsMap.fromJson(Map<String, dynamic> json) =>
//       _$UomsMapFromJson(json);

//   Map<String, dynamic> toJson() => _$UomsMapToJson(this);
// }

@JsonSerializable()
class PropertyDetails {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'propertyId', includeIfNull: false)
  String? propertyId;

  PropertyDetails();

  factory PropertyDetails.fromJson(Map<String, dynamic> json) =>
      _$PropertyDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyDetailsToJson(this);
}

@JsonSerializable()
class Address {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'city')
  String? city;
  @JsonKey(name: 'locality')
  Locality? locality;
  @JsonKey(name: 'doorNo', includeIfNull: false)
  String? doorNo;
  @JsonKey(name: 'buildingName', includeIfNull: false)
  String? buildingName;
  @JsonKey(name: 'street', includeIfNull: false)
  String? street;
  @JsonKey(name: 'pincode', includeIfNull: false)
  String? pincode;

  Address();

  factory Address.fromJson(Map<String, dynamic> json) =>
      _$AddressFromJson(json);

  Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class Locality {
  @JsonKey(name: 'code')
  String? code;

  Locality();

  factory Locality.fromJson(Map<String, dynamic> json) =>
      _$LocalityFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  String? ver;
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
class WfDocument {
  @JsonKey(name: "fileName")
  String? fileName;
  @JsonKey(name: "fileStoreId")
  String? fileStoreId;
  @JsonKey(name: "documentType")
  String? documentType;

  WfDocument();

  factory WfDocument.fromJson(Map<String, dynamic> json) =>
      _$WfDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$WfDocumentToJson(this);
}
