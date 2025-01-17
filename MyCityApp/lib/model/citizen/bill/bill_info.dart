import 'package:json_annotation/json_annotation.dart';

part 'bill_info.g.dart';

@JsonSerializable()
class BillInfo {
  @JsonKey(name: 'ResposneInfo')
  ResposneInfo? resposneInfo;
  @JsonKey(name: 'Bill')
  List<Bill>? bill;
  @JsonKey(name: 'Bills')
  List<Bill>? bills;

  BillInfo();

  factory BillInfo.fromJson(Map<String, dynamic> json) =>
      _$BillInfoFromJson(json);

  Map<String, dynamic> toJson() => _$BillInfoToJson(this);
}

@JsonSerializable()
class Bill {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'userId')
  String? userId;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'payerName')
  String? payerName;
  @JsonKey(name: 'payerAddress')
  String? payerAddress;
  @JsonKey(name: 'payerEmail')
  dynamic payerEmail;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'totalAmount')
  double? totalAmount;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'billNumber')
  String? billNumber;
  @JsonKey(name: 'billDate')
  int? billDate;
  @JsonKey(name: 'consumerCode')
  String? consumerCode;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;
  @JsonKey(name: 'billDetails')
  List<BillDetail>? billDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'fileStoreId')
  dynamic fileStoreId;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  Bill();

  factory Bill.fromJson(Map<String, dynamic> json) => _$BillFromJson(json);

  Map<String, dynamic> toJson() => _$BillToJson(this);
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
class BillDetail {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'demandId')
  String? demandId;
  @JsonKey(name: 'billId')
  String? billId;
  @JsonKey(name: 'expiryDate')
  int? expiryDate;
  @JsonKey(name: 'amount')
  double? amount;
  @JsonKey(name: 'amountPaid')
  dynamic amountPaid;
  @JsonKey(name: 'fromPeriod')
  int? fromPeriod;
  @JsonKey(name: 'toPeriod')
  int? toPeriod;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;
  @JsonKey(name: 'billAccountDetails')
  List<BillAccountDetail>? billAccountDetails;

  BillDetail();

  factory BillDetail.fromJson(Map<String, dynamic> json) =>
      _$BillDetailFromJson(json);

  Map<String, dynamic> toJson() => _$BillDetailToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'calculationDescription', includeIfNull: false)
  List<String>? calculationDescription;
  @JsonKey(name: 'propertyId', includeIfNull: false)
  String? propertyId;

  AdditionalDetails();

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsToJson(this);
}

@JsonSerializable()
class BillAccountDetail {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'billDetailId')
  String? billDetailId;
  @JsonKey(name: 'demandDetailId')
  String? demandDetailId;
  @JsonKey(name: 'order')
  int? order;
  @JsonKey(name: 'amount')
  double? amount;
  @JsonKey(name: 'adjustedAmount')
  double? adjustedAmount;
  @JsonKey(name: 'taxHeadCode')
  String? taxHeadCode;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;

  BillAccountDetail();

  factory BillAccountDetail.fromJson(Map<String, dynamic> json) =>
      _$BillAccountDetailFromJson(json);

  Map<String, dynamic> toJson() => _$BillAccountDetailToJson(this);
}

@JsonSerializable()
class ResposneInfo {
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

  ResposneInfo();

  factory ResposneInfo.fromJson(Map<String, dynamic> json) =>
      _$ResposneInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResposneInfoToJson(this);
}
