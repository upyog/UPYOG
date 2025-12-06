import 'package:json_annotation/json_annotation.dart';

part 'payment.g.dart';

@JsonSerializable()
class PaymentModel {
  @JsonKey(name: 'ResponseInfo', includeIfNull: false)
  ResponseInfo? responseInfo;

  @JsonKey(name: 'Payments')
  List<Payment>? payments;

  PaymentModel();

  factory PaymentModel.fromJson(Map<String, dynamic> json) =>
      _$PaymentModelFromJson(json);

  Map<String, dynamic> toJson() => _$PaymentModelToJson(this);
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

@JsonSerializable()
class Payment {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'totalDue')
  double? totalDue;
  @JsonKey(name: 'totalAmountPaid')
  double? totalAmountPaid;
  @JsonKey(name: 'transactionNumber')
  String? transactionNumber;
  @JsonKey(name: 'transactionDate')
  int? transactionDate;
  @JsonKey(name: 'paymentMode')
  String? paymentMode;
  @JsonKey(name: 'instrumentDate')
  int? instrumentDate;
  @JsonKey(name: 'instrumentNumber')
  String? instrumentNumber;
  @JsonKey(name: 'instrumentStatus')
  String? instrumentStatus;
  @JsonKey(name: 'ifscCode')
  dynamic ifscCode;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'additionalDetails')
  PaymentAdditionalDetails? additionalDetails;
  @JsonKey(name: 'paymentDetails')
  List<PaymentDetail>? paymentDetails;
  @JsonKey(name: 'paidBy')
  String? paidBy;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'payerName')
  String? payerName;
  @JsonKey(name: 'payerAddress')
  dynamic payerAddress;
  @JsonKey(name: 'payerEmail')
  dynamic payerEmail;
  @JsonKey(name: 'payerId')
  String? payerId;
  @JsonKey(name: 'paymentStatus')
  String? paymentStatus;
  @JsonKey(
    name: 'fileStoreId',
    fromJson: fileStoreIdFromJson,
  )
  String? fileStoreId;

  Payment();

  factory Payment.fromJson(Map<String, dynamic> json) =>
      _$PaymentFromJson(json);

  Map<String, dynamic> toJson() => _$PaymentToJson(this);
}

dynamic fileStoreIdFromJson(dynamic fileStoreId) {
  if (fileStoreId is String) {
    return fileStoreId;
  } else if (fileStoreId is Map) {
    return fileStoreId;
  } else {
    return null;
  }
}

@JsonSerializable()
class PaymentAdditionalDetails {
  @JsonKey(name: 'isWhatsapp')
  bool? isWhatsapp;
  @JsonKey(name: 'taxAndPayments')
  List<TaxAndPayment>? taxAndPayments;

  PaymentAdditionalDetails();

  factory PaymentAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$PaymentAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$PaymentAdditionalDetailsToJson(this);
}

@JsonSerializable()
class TaxAndPayment {
  @JsonKey(name: 'billId')
  String? billId;
  @JsonKey(name: 'taxAmount')
  dynamic taxAmount;
  @JsonKey(name: 'amountPaid')
  int? amountPaid;

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
  String? lastModifiedBy;
  @JsonKey(name: 'lastModifiedTime')
  int? lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class PaymentDetail {
  @JsonKey(name: 'paymentId')
  dynamic paymentId;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'totalDue')
  int? totalDue;
  @JsonKey(name: 'totalAmountPaid')
  int? totalAmountPaid;
  @JsonKey(name: 'receiptNumber')
  String? receiptNumber;
  @JsonKey(name: 'manualReceiptNumber')
  dynamic manualReceiptNumber;
  @JsonKey(name: 'manualReceiptDate')
  int? manualReceiptDate;
  @JsonKey(name: 'receiptDate')
  int? receiptDate;
  @JsonKey(name: 'receiptType')
  String? receiptType;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'billId')
  String? billId;
  @JsonKey(name: 'bill')
  Bill? bill;
  @JsonKey(name: 'additionalDetails', includeIfNull: false)
  AdditionalDetailsClass? additionalDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  PaymentDetail();

  factory PaymentDetail.fromJson(Map<String, dynamic> json) =>
      _$PaymentDetailFromJson(json);

  Map<String, dynamic> toJson() => _$PaymentDetailToJson(this);
}

@JsonSerializable()
class AdditionalDetailsClass {
  @JsonKey(name: 'assessmentYears', includeIfNull: false)
  String? assessmentYears;
  @JsonKey(name: 'arrearArray', includeIfNull: false)
  List<Array>? arrearArray;
  @JsonKey(name: 'taxArray', includeIfNull: false)
  List<Array>? taxArray;

  AdditionalDetailsClass();

  factory AdditionalDetailsClass.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsClassFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsClassToJson(this);
}

@JsonSerializable()
class Array {
  @JsonKey(name: 'year')
  String? year;
  @JsonKey(name: 'tax')
  int? tax;
  @JsonKey(name: 'firecess')
  dynamic firecess;
  @JsonKey(name: 'cancercess')
  dynamic cancercess;
  @JsonKey(name: 'penalty')
  dynamic penalty;
  @JsonKey(name: 'rebate')
  dynamic rebate;
  @JsonKey(name: 'interest')
  dynamic interest;
  @JsonKey(name: 'usageExemption')
  dynamic usageExemption;
  @JsonKey(name: 'specialCategoryExemption')
  dynamic specialCategoryExemption;
  @JsonKey(name: 'adhocPenalty')
  dynamic adhocPenalty;
  @JsonKey(name: 'adhocRebate')
  dynamic adhocRebate;
  @JsonKey(name: 'roundoff')
  dynamic roundOff;
  @JsonKey(name: 'total')
  int? total;

  Array();

  factory Array.fromJson(Map<String, dynamic> json) => _$ArrayFromJson(json);

  Map<String, dynamic> toJson() => _$ArrayToJson(this);
}

@JsonSerializable()
class Bill {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'mobileNumber')
  dynamic mobileNumber;
  @JsonKey(name: 'paidBy')
  dynamic paidBy;
  @JsonKey(name: 'payerName')
  dynamic payerName;
  @JsonKey(name: 'payerAddress')
  dynamic payerAddress;
  @JsonKey(name: 'payerEmail')
  dynamic payerEmail;
  @JsonKey(name: 'payerId')
  dynamic payerId;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'reasonForCancellation')
  dynamic reasonForCancellation;
  @JsonKey(name: 'isCancelled')
  dynamic isCancelled;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'billDetails')
  List<BillDetail>? billDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'collectionModesNotAllowed')
  List<String>? collectionModesNotAllowed;
  @JsonKey(name: 'partPaymentAllowed')
  bool? partPaymentAllowed;
  @JsonKey(name: 'isAdvanceAllowed')
  bool? isAdvanceAllowed;
  @JsonKey(name: 'minimumAmountToBePaid')
  dynamic minimumAmountToBePaid;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'totalAmount')
  int? totalAmount;
  @JsonKey(name: 'consumerCode')
  String? consumerCode;
  @JsonKey(name: 'billNumber')
  String? billNumber;
  @JsonKey(name: 'billDate')
  int? billDate;
  @JsonKey(name: 'amountPaid')
  dynamic amountPaid;

  Bill();

  factory Bill.fromJson(Map<String, dynamic> json) => _$BillFromJson(json);

  Map<String, dynamic> toJson() => _$BillToJson(this);
}

@JsonSerializable()
class BillDetail {
  @JsonKey(name: 'billDescription')
  dynamic billDescription;
  @JsonKey(name: 'displayMessage')
  dynamic displayMessage;
  @JsonKey(name: 'callBackForApportioning')
  dynamic callBackForApportioning;
  @JsonKey(name: 'cancellationRemarks')
  dynamic cancellationRemarks;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'demandId')
  String? demandId;
  @JsonKey(name: 'billId')
  String? billId;
  @JsonKey(name: 'amount')
  int? amount;
  @JsonKey(name: 'amountPaid')
  int? amountPaid;
  @JsonKey(name: 'fromPeriod')
  int? fromPeriod;
  @JsonKey(name: 'toPeriod')
  int? toPeriod;
  @JsonKey(name: 'additionalDetails')
  BillDetailAdditionalDetails? additionalDetails;
  @JsonKey(name: 'channel')
  dynamic channel;
  @JsonKey(name: 'voucherHeader')
  dynamic voucherHeader;
  @JsonKey(name: 'boundary')
  dynamic boundary;
  @JsonKey(name: 'manualReceiptNumber')
  dynamic manualReceiptNumber;
  @JsonKey(name: 'manualReceiptDate')
  dynamic manualReceiptDate;
  @JsonKey(name: 'billAccountDetails')
  List<BillAccountDetail>? billAccountDetails;
  @JsonKey(name: 'collectionType')
  dynamic collectionType;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'expiryDate')
  int? expiryDate;

  BillDetail();

  factory BillDetail.fromJson(Map<String, dynamic> json) =>
      _$BillDetailFromJson(json);

  Map<String, dynamic> toJson() => _$BillDetailToJson(this);
}

@JsonSerializable()
class BillDetailAdditionalDetails {
  @JsonKey(name: 'calculationDescription')
  List<String>? calculationDescription;

  BillDetailAdditionalDetails();

  factory BillDetailAdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$BillDetailAdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$BillDetailAdditionalDetailsToJson(this);
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
  int? amount;
  @JsonKey(name: 'adjustedAmount')
  int? adjustedAmount;
  @JsonKey(name: 'isActualDemand')
  dynamic isActualDemand;
  @JsonKey(name: 'taxHeadCode')
  String? taxHeadCode;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;
  @JsonKey(name: 'purpose')
  dynamic purpose;
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;

  BillAccountDetail();

  factory BillAccountDetail.fromJson(Map<String, dynamic> json) =>
      _$BillAccountDetailFromJson(json);

  Map<String, dynamic> toJson() => _$BillAccountDetailToJson(this);
}
