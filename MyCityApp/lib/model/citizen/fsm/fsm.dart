import 'package:json_annotation/json_annotation.dart';

part 'fsm.g.dart';

@JsonSerializable()
class FsmModel {
  @JsonKey(name: 'totalCount')
  int? totalCount;
  @JsonKey(name: 'responseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'fsm')
  List<Fsm>? fsm;
  @JsonKey(name: 'workflow')
  dynamic workflow;

  FsmModel();

  factory FsmModel.fromJson(Map<String, dynamic> json) =>
      _$FsmModelFromJson(json);

  Map<String, dynamic> toJson() => _$FsmModelToJson(this);
}

@JsonSerializable()
class Fsm {
  @JsonKey(name: 'citizen')
  Citizen? citizen;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'applicationNo')
  String? applicationNo;
  @JsonKey(name: 'description')
  dynamic description;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'additionalDetails')
  FsmAdditionalDetails? additionalDetails;
  @JsonKey(name: 'applicationStatus')
  String? applicationStatus;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'sanitationtype')
  String? sanitationtype;
  @JsonKey(name: 'propertyUsage')
  String? propertyUsage;
  @JsonKey(name: 'vehicleType')
  dynamic vehicleType;
  @JsonKey(name: 'noOfTrips')
  int? noOfTrips;
  @JsonKey(name: 'vehicleCapacity')
  String? vehicleCapacity;
  @JsonKey(name: 'status')
  dynamic status;
  @JsonKey(name: 'vehicleId')
  String? vehicleId;
  @JsonKey(name: 'vehicle')
  dynamic vehicle;
  @JsonKey(name: 'dsoId')
  String? dsoId;
  @JsonKey(name: 'dso')
  dynamic dso;
  @JsonKey(name: 'possibleServiceDate')
  int? possibleServiceDate;
  @JsonKey(name: 'pitDetail')
  PitDetail? pitDetail;
  @JsonKey(name: 'address')
  Address? address;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'wasteCollected')
  dynamic wasteCollected;
  @JsonKey(name: 'completedOn')
  int? completedOn;
  @JsonKey(name: 'advanceAmount')
  int? advanceAmount;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'oldApplicationNo')
  dynamic oldApplicationNo;
  @JsonKey(name: 'paymentPreference')
  dynamic paymentPreference;

  Fsm();

  factory Fsm.fromJson(Map<String, dynamic> json) => _$FsmFromJson(json);

  Map<String, dynamic> toJson() => _$FsmToJson(this);
}

@JsonSerializable()
class FsmAdditionalDetails {
  @JsonKey(name: 'roadWidth')
  String? roadWidth;
  @JsonKey(name: 'propertyID')
  String? propertyId;
  @JsonKey(name: 'tripAmount')
  dynamic tripAmount;
  @JsonKey(name: 'totalAmount')
  dynamic totalAmount;
  @JsonKey(name: 'distancefromroad')
  String? distancefromroad;
  @JsonKey(name: 'CheckList')
  List<CheckList>? checkList;

  FsmAdditionalDetails();

  factory FsmAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$FsmAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$FsmAdditionalDetailsToJson(this);
}

@JsonSerializable()
class CheckList {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'value')
  String? value;

  CheckList();

  factory CheckList.fromJson(Map<String, dynamic> json) =>
      _$CheckListFromJson(json);

  Map<String, dynamic> toJson() => _$CheckListToJson(this);
}

checkAdditionalDetails(additional) {
  if (additional is String) {
    return additional;
  }

  if (additional is Map<String, dynamic>) {
    AddressAdditionalDetails additionalDetails =
        AddressAdditionalDetails.fromJson(additional);
    return additionalDetails;
  }
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
  String? pincode;
  @JsonKey(name: 'additionalDetails', fromJson: checkAdditionalDetails)
  dynamic additionalDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'buildingName')
  dynamic buildingName;
  @JsonKey(name: 'street')
  String? street;
  @JsonKey(name: 'slumName')
  String? slumName;
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
class AddressAdditionalDetails {
  @JsonKey(name: 'boundaryType')
  String? boundaryType;
  @JsonKey(name: 'gramPanchayat')
  GramPanchayat? gramPanchayat;
  @JsonKey(
    name: 'village',
    fromJson: checkVillageFromJson,
    toJson: checkVillageToJson,
  )
  dynamic village;
  @JsonKey(name: 'newGramPanchayat')
  String? newGramPanchayat;

  AddressAdditionalDetails();

  factory AddressAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$AddressAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AddressAdditionalDetailsToJson(this);
}

checkVillageFromJson(village) {
  if (village is String) {
    return village;
  }

  if (village is Map<String, dynamic>) {
    Village villageDetails = Village.fromJson(village);
    return villageDetails;
  }
}

checkVillageToJson(village) {
  if (village is String) {
    return village;
  }

  if (village is Map<String, dynamic>) {
    Village villageDetails = Village.fromJson(village);
    return villageDetails.toJson();
  }
}

@JsonSerializable()
class GramPanchayat {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;

  GramPanchayat();

  factory GramPanchayat.fromJson(Map<String, dynamic> json) =>
      _$GramPanchayatFromJson(json);

  Map<String, dynamic> toJson() => _$GramPanchayatToJson(this);
}

@JsonSerializable()
class Village {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name', includeIfNull: false)
  String? name;

  Village();

  factory Village.fromJson(Map<String, dynamic> json) =>
      _$VillageFromJson(json);

  Map<String, dynamic> toJson() => _$VillageToJson(this);
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
class GeoLocation {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'latitude')
  int? latitude;
  @JsonKey(name: 'longitude')
  int? longitude;
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
  String? latitude;
  @JsonKey(name: 'longitude')
  String? longitude;
  @JsonKey(name: 'children')
  List<Locality>? children;
  @JsonKey(name: 'materializedPath')
  dynamic materializedPath;

  Locality();

  factory Locality.fromJson(Map<String, dynamic> json) =>
      _$LocalityFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityToJson(this);
}

@JsonSerializable()
class Citizen {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'password')
  dynamic password;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'salutation')
  dynamic salutation;
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
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'dob')
  int? dob;
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
  @JsonKey(name: 'gender')
  String? gender;

  Citizen();

  factory Citizen.fromJson(Map<String, dynamic> json) =>
      _$CitizenFromJson(json);

  Map<String, dynamic> toJson() => _$CitizenToJson(this);
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
class PitDetail {
  @JsonKey(name: 'type')
  dynamic type;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'height')
  double? height;
  @JsonKey(name: 'length')
  int? length;
  @JsonKey(name: 'width')
  int? width;
  @JsonKey(name: 'diameter')
  int? diameter;
  @JsonKey(name: 'distanceFromRoad')
  int? distanceFromRoad;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'additionalDetails')
  PitDetailAdditionalDetails? additionalDetails;

  PitDetail();

  factory PitDetail.fromJson(Map<String, dynamic> json) =>
      _$PitDetailFromJson(json);

  Map<String, dynamic> toJson() => _$PitDetailToJson(this);
}

@JsonSerializable()
class PitDetailAdditionalDetails {
  @JsonKey(name: 'fileStoreId')
  FileStoreId? fileStoreId;

  PitDetailAdditionalDetails();

  factory PitDetailAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$PitDetailAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$PitDetailAdditionalDetailsToJson(this);
}

@JsonSerializable()
class FileStoreId {
  @JsonKey(name: 'CITIZEN')
  List<String>? citizen;

  FileStoreId();

  factory FileStoreId.fromJson(Map<String, dynamic> json) =>
      _$FileStoreIdFromJson(json);

  Map<String, dynamic> toJson() => _$FileStoreIdToJson(this);
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
