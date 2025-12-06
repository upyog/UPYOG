
import 'package:json_annotation/json_annotation.dart';

part 'emp_challans_model.g.dart';

@JsonSerializable()
class Challans {
    @JsonKey(name: "countOfServices")
    int? countOfServices;
    @JsonKey(name: "totalAmountCollected")
    int? totalAmountCollected;
    @JsonKey(name: "challanValidity")
    int? challanValidity;
    @JsonKey(name: "responseInfo")
    ResponseInfo? responseInfo;
    @JsonKey(name: "challans")
    List<Challan>? challans;
    @JsonKey(name: "totalCount")
    int? totalCount;

    Challans();

    factory Challans.fromJson(Map<String, dynamic> json) => _$ChallansFromJson(json);

    Map<String, dynamic> toJson() => _$ChallansToJson(this);
}

@JsonSerializable()
class Challan {
    @JsonKey(name: "citizen")
    Citizen? citizen;
    @JsonKey(name: "id")
    String? id;
    @JsonKey(name: "tenantId")
    String? tenantId;
    @JsonKey(name: "businessService")
    String? businessService;
    @JsonKey(name: "challanNo")
    String? challanNo;
    @JsonKey(name: "referenceId")
    dynamic referenceId;
    @JsonKey(name: "description")
    dynamic description;
    @JsonKey(name: "accountId")
    dynamic accountId;
    @JsonKey(name: "additionalDetail")
    dynamic additionalDetail;
    @JsonKey(name: "source")
    dynamic source;
    @JsonKey(name: "taxPeriodFrom")
    int? taxPeriodFrom;
    @JsonKey(name: "taxPeriodTo")
    int? taxPeriodTo;
    @JsonKey(name: "calculation")
    Calculation? calculation;
    @JsonKey(name: "amount")
    List<Amount>? amount;
    @JsonKey(name: "address")
    Address? address;
    @JsonKey(name: "filestoreid")
    dynamic fileStoreId;
    @JsonKey(name: "auditDetails")
    AuditDetails? auditDetails;
    @JsonKey(name: "applicationStatus")
    String? applicationStatus;
    @JsonKey(name: "receiptNumber")
    dynamic receiptNumber;

    Challan();

    factory Challan.fromJson(Map<String, dynamic> json) => _$ChallanFromJson(json);

    Map<String, dynamic> toJson() => _$ChallanToJson(this);
}

@JsonSerializable()
class Address {
    @JsonKey(name: "id")
    String? id;
    @JsonKey(name: "tenantId")
    String? tenantId;
    @JsonKey(name: "doorNo")
    String? doorNo;
    @JsonKey(name: "latitude")
    dynamic latitude;
    @JsonKey(name: "longitude")
    dynamic longitude;
    @JsonKey(name: "addressId")
    dynamic addressId;
    @JsonKey(name: "addressNumber")
    dynamic addressNumber;
    @JsonKey(name: "type")
    dynamic type;
    @JsonKey(name: "addressLine1")
    dynamic addressLine1;
    @JsonKey(name: "addressLine2")
    dynamic addressLine2;
    @JsonKey(name: "landmark")
    dynamic landmark;
    @JsonKey(name: "city")
    dynamic city;
    @JsonKey(name: "pincode")
    String? pinCode;
    @JsonKey(name: "detail")
    dynamic detail;
    @JsonKey(name: "buildingName")
    String? buildingName;
    @JsonKey(name: "street")
    String? street;
    @JsonKey(name: "locality")
    Locality? locality;
    @JsonKey(name: "plotNo")
    dynamic plotNo;
    @JsonKey(name: "district")
    dynamic district;
    @JsonKey(name: "state")
    dynamic state;
    @JsonKey(name: "country")
    dynamic country;
    @JsonKey(name: "region")
    dynamic region;

    Address();

    factory Address.fromJson(Map<String, dynamic> json) => _$AddressFromJson(json);

    Map<String, dynamic> toJson() => _$AddressToJson(this);
}

@JsonSerializable()
class Locality {
    @JsonKey(name: "code")
    String? code;
    @JsonKey(name: "name")
    dynamic name;
    @JsonKey(name: "label")
    dynamic label;
    @JsonKey(name: "latitude")
    dynamic latitude;
    @JsonKey(name: "longitude")
    dynamic longitude;
    @JsonKey(name: "children")
    dynamic children;
    @JsonKey(name: "materializedPath")
    dynamic materializedPath;

    Locality();

    factory Locality.fromJson(Map<String, dynamic> json) => _$LocalityFromJson(json);

    Map<String, dynamic> toJson() => _$LocalityToJson(this);
}

@JsonSerializable()
class Amount {
    @JsonKey(name: "taxHeadCode")
    String? taxHeadCode;
    @JsonKey(name: "amount")
    int? amount;

    Amount();

    factory Amount.fromJson(Map<String, dynamic> json) => _$AmountFromJson(json);

    Map<String, dynamic> toJson() => _$AmountToJson(this);
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

    factory AuditDetails.fromJson(Map<String, dynamic> json) => _$AuditDetailsFromJson(json);

    Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class Calculation {
    @JsonKey(name: "challanNo")
    dynamic challanNo;
    @JsonKey(name: "challan")
    dynamic challan;
    @JsonKey(name: "tenantId")
    String? tenantId;
    @JsonKey(name: "taxHeadEstimates")
    List<TaxHeadEstimate>? taxHeadEstimates;

    Calculation();

    factory Calculation.fromJson(Map<String, dynamic> json) => _$CalculationFromJson(json);

    Map<String, dynamic> toJson() => _$CalculationToJson(this);
}

@JsonSerializable()
class TaxHeadEstimate {
    @JsonKey(name: "taxHeadCode")
    String? taxHeadCode;
    @JsonKey(name: "estimateAmount")
    int? estimateAmount;
    @JsonKey(name: "category")
    dynamic category;

    TaxHeadEstimate();

    factory TaxHeadEstimate.fromJson(Map<String, dynamic> json) => _$TaxHeadEstimateFromJson(json);

    Map<String, dynamic> toJson() => _$TaxHeadEstimateToJson(this);
}

@JsonSerializable()
class Citizen {
    @JsonKey(name: "id")
    int? id;
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
    @JsonKey(name: 'gender')
    dynamic gender;
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
    dynamic correspondenceAddress;
    @JsonKey(name: 'active')
    bool? active;
    @JsonKey(name: 'dob')
    dynamic dob;
    @JsonKey(name: 'pwdExpiryDate')
    dynamic pwdExpiryDate;
    @JsonKey(name: 'locale')
    dynamic locale;
    @JsonKey(name: 'type')
    String? type;
    @JsonKey(name: 'signature')
    dynamic signature;
    @JsonKey(name: 'accountLocked')
    dynamic accountLocked;
    @JsonKey(name: 'roles')
    List<Role>? roles;
    @JsonKey(name: 'fatherOrHusbandName')
    dynamic fatherOrHusbandName;
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
    @JsonKey(name: 'idToken')
    dynamic idToken;
    @JsonKey(name: 'email')
    dynamic email;
    @JsonKey(name: 'primaryrole')
    List<dynamic>? primaryRole;
    @JsonKey(name: 'additionalroles')
    dynamic additionalRoles;

    Citizen();

    factory Citizen.fromJson(Map<String, dynamic> json) => _$CitizenFromJson(json);

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
    dynamic tenantId;

    Role();

    factory Role.fromJson(Map<String, dynamic> json) => _$RoleFromJson(json);

    Map<String, dynamic> toJson() => _$RoleToJson(this);
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

