import 'package:json_annotation/json_annotation.dart';

part 'demands.g.dart';

@JsonSerializable()
class DemandsModel {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'Demands')
  List<Demand>? demands;
  @JsonKey(name: 'CollectedReceipt')
  dynamic collectedReceipt;

  DemandsModel();

  factory DemandsModel.fromJson(Map<String, dynamic> json) =>
      _$DemandsModelFromJson(json);

  Map<String, dynamic> toJson() => _$DemandsModelToJson(this);
}

@JsonSerializable()
class Demand {
  @JsonKey(name: 'isPaymentCompleted')
  bool? isPaymentCompleted;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'consumerCode')
  String? consumerCode;
  @JsonKey(name: 'consumerType')
  String? consumerType;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'payer')
  Payer? payer;
  @JsonKey(name: 'taxPeriodFrom')
  int? taxPeriodFrom;
  @JsonKey(name: 'taxPeriodTo')
  int? taxPeriodTo;
  @JsonKey(name: 'demandDetails')
  List<DemandDetail>? demandDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'fixedBillExpiryDate')
  dynamic fixedBillExpiryDate;
  @JsonKey(name: 'billExpiryTime')
  int? billExpiryTime;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'minimumAmountPayable')
  int? minimumAmountPayable;
  @JsonKey(name: 'status')
  String? status;

  Demand();

  factory Demand.fromJson(Map<String, dynamic> json) => _$DemandFromJson(json);

  Map<String, dynamic> toJson() => _$DemandToJson(this);
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
class DemandDetail {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'demandId')
  String? demandId;
  @JsonKey(name: 'taxHeadMasterCode')
  String? taxHeadMasterCode;
  @JsonKey(name: 'taxAmount')
  int? taxAmount;
  @JsonKey(name: 'collectionAmount')
  int? collectionAmount;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  DemandDetail();

  factory DemandDetail.fromJson(Map<String, dynamic> json) =>
      _$DemandDetailFromJson(json);

  Map<String, dynamic> toJson() => _$DemandDetailToJson(this);
}

@JsonSerializable()
class Payer {
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'salutation')
  dynamic salutation;
  @JsonKey(name: 'name')
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
  @JsonKey(name: 'correspondenceAddress')
  dynamic correspondenceAddress;
  @JsonKey(name: 'correspondenceCity')
  dynamic correspondenceCity;
  @JsonKey(name: 'correspondencePinCode')
  dynamic correspondencePinCode;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  Payer();

  factory Payer.fromJson(Map<String, dynamic> json) => _$PayerFromJson(json);

  Map<String, dynamic> toJson() => _$PayerToJson(this);
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
  dynamic resMsgId;
  @JsonKey(name: 'msgId')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>
      _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}
