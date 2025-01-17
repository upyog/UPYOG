import 'package:json_annotation/json_annotation.dart';
import 'package:mobile_app/model/citizen/token/token.dart';
part 'grievance.g.dart';

@JsonSerializable()
class Grievance {
  @JsonKey(name: 'responseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'ServiceWrappers')
  List<ServiceWrapper>? serviceWrappers;
  @JsonKey(name: 'complaintsResolved')
  int? complaintsResolved;
  @JsonKey(name: 'averageResolutionTime')
  int? averageResolutionTime;
  @JsonKey(name: 'complaintTypes')
  int? complaintTypes;

  Grievance();

  factory Grievance.fromJson(Map<String, dynamic> json) =>
      _$GrievanceFromJson(json);

  Map<String, dynamic> toJson() => _$GrievanceToJson(this);
}

@JsonSerializable()
class ServiceWrapper {
  @JsonKey(name: 'service')
  Service? service;
  @JsonKey(name: 'workflow')
  Workflow? workflow;

  ServiceWrapper();

  factory ServiceWrapper.fromJson(Map<String, dynamic> json) =>
      _$ServiceWrapperFromJson(json);

  Map<String, dynamic> toJson() => _$ServiceWrapperToJson(this);
}

@JsonSerializable()
class Service {
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'citizen')
  Citizen? citizen;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'serviceCode')
  String? serviceCode;
  @JsonKey(name: 'serviceRequestId')
  String? serviceRequestId;
  @JsonKey(name: 'description')
  String? description;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'rating')
  int? rating;
  @JsonKey(name: 'additionalDetail')
  dynamic additionalDetail;
  @JsonKey(name: 'applicationStatus')
  String? applicationStatus;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'priority')
  String? priority;

  Service();

  factory Service.fromJson(Map<String, dynamic> json) =>
      _$ServiceFromJson(json);

  Map<String, dynamic> toJson() => _$ServiceToJson(this);
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
  String? pinCode;
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
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;

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
  double? latitude;
  @JsonKey(name: 'longitude')
  double? longitude;
  @JsonKey(name: 'children')
  String? children;
  @JsonKey(name: 'materializedPath')
  String? materializedPath;

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
class Citizen {
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
  List<Role>? roles;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'active')
  bool? active;

  Citizen();

  factory Citizen.fromJson(Map<String, dynamic> json) =>
      _$CitizenFromJson(json);

  Map<String, dynamic> toJson() => _$CitizenToJson(this);
}

@JsonSerializable()
class Roles {
  @JsonKey(name: 'id')
  int? id;
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
class Workflow {
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'assignes')
  dynamic assignes;
  @JsonKey(name: 'comments')
  dynamic comments;
  @JsonKey(name: 'verificationDocuments')
  List<VerificationDocument>? verificationDocuments;

  Workflow();

  factory Workflow.fromJson(Map<String, dynamic> json) =>
      _$WorkflowFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowToJson(this);
}

@JsonSerializable()
class VerificationDocument {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentUid')
  String? documentUid;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;

  VerificationDocument();

  factory VerificationDocument.fromJson(Map<String, dynamic> json) =>
      _$VerificationDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$VerificationDocumentToJson(this);
}
