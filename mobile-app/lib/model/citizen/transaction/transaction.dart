import 'package:json_annotation/json_annotation.dart';

part 'transaction.g.dart';

@JsonSerializable()
class Transaction {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'txnAmount')
  String? txnAmount;
  @JsonKey(name: 'billId')
  String? billId;
  @JsonKey(name: 'module')
  String? module;
  @JsonKey(name: 'consumerCode')
  String? consumerCode;
  @JsonKey(name: 'taxAndPayments')
  List<TaxAndPayment>? taxAndPayments;
  @JsonKey(name: 'productInfo')
  String? productInfo;
  @JsonKey(name: 'gateway')
  String? gateway;
  @JsonKey(name: 'callbackUrl')
  String? callbackUrl;
  @JsonKey(name: 'txnId')
  String? txnId;
  @JsonKey(name: 'user')
  User? user;
  @JsonKey(name: 'redirectUrl')
  String? redirectUrl;
  @JsonKey(name: 'txnStatus')
  String? txnStatus;
  @JsonKey(name: 'txnStatusMsg')
  String? txnStatusMsg;
  @JsonKey(name: 'gatewayTxnId')
  dynamic gatewayTxnId;
  @JsonKey(name: 'gatewayPaymentMode')
  dynamic gatewayPaymentMode;
  @JsonKey(name: 'gatewayStatusCode')
  dynamic gatewayStatusCode;
  @JsonKey(name: 'gatewayStatusMsg')
  dynamic gatewayStatusMsg;
  @JsonKey(name: 'receipt')
  dynamic receipt;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;
  @JsonKey(name: 'bankTransactionNo')
  dynamic bankTransactionNo;

  Transaction();

  factory Transaction.fromJson(Map<String, dynamic> json) =>
      _$TransactionFromJson(json);

  Map<String, dynamic> toJson() => _$TransactionToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'isWhatsapp')
  bool? isWhatsapp;
  @JsonKey(name: 'taxAndPayments')
  List<TaxAndPayment>? taxAndPayments;

  AdditionalDetails();

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsToJson(this);
}

@JsonSerializable()
class TaxAndPayment {
  @JsonKey(name: 'taxAmount')
  dynamic taxAmount;
  @JsonKey(name: 'amountPaid')
  int? amountPaid;
  @JsonKey(name: 'billId')
  String? billId;

  TaxAndPayment();

  factory TaxAndPayment.fromJson(Map<String, dynamic> json) =>
      _$TaxAndPaymentFromJson(json);

  Map<String, dynamic> toJson() => _$TaxAndPaymentToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'createdTime')
  int? createdTime;
  @JsonKey(name: 'lastModifiedBy')
  dynamic lastModifiedBy;
  @JsonKey(name: 'lastModifiedTime')
  dynamic lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class User {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  User();

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);

  Map<String, dynamic> toJson() => _$UserToJson(this);
}
