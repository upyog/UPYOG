// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'transaction.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Transaction _$TransactionFromJson(Map<String, dynamic> json) => Transaction()
  ..tenantId = json['tenantId'] as String?
  ..txnAmount = json['txnAmount'] as String?
  ..billId = json['billId'] as String?
  ..module = json['module'] as String?
  ..consumerCode = json['consumerCode'] as String?
  ..taxAndPayments = (json['taxAndPayments'] as List<dynamic>?)
      ?.map((e) => TaxAndPayment.fromJson(e as Map<String, dynamic>))
      .toList()
  ..productInfo = json['productInfo'] as String?
  ..gateway = json['gateway'] as String?
  ..callbackUrl = json['callbackUrl'] as String?
  ..txnId = json['txnId'] as String?
  ..user = json['user'] == null
      ? null
      : User.fromJson(json['user'] as Map<String, dynamic>)
  ..redirectUrl = json['redirectUrl'] as String?
  ..txnStatus = json['txnStatus'] as String?
  ..txnStatusMsg = json['txnStatusMsg'] as String?
  ..gatewayTxnId = json['gatewayTxnId']
  ..gatewayPaymentMode = json['gatewayPaymentMode']
  ..gatewayStatusCode = json['gatewayStatusCode']
  ..gatewayStatusMsg = json['gatewayStatusMsg']
  ..receipt = json['receipt']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : AdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..bankTransactionNo = json['bankTransactionNo'];

Map<String, dynamic> _$TransactionToJson(Transaction instance) =>
    <String, dynamic>{
      'tenantId': instance.tenantId,
      'txnAmount': instance.txnAmount,
      'billId': instance.billId,
      'module': instance.module,
      'consumerCode': instance.consumerCode,
      'taxAndPayments': instance.taxAndPayments,
      'productInfo': instance.productInfo,
      'gateway': instance.gateway,
      'callbackUrl': instance.callbackUrl,
      'txnId': instance.txnId,
      'user': instance.user,
      'redirectUrl': instance.redirectUrl,
      'txnStatus': instance.txnStatus,
      'txnStatusMsg': instance.txnStatusMsg,
      'gatewayTxnId': instance.gatewayTxnId,
      'gatewayPaymentMode': instance.gatewayPaymentMode,
      'gatewayStatusCode': instance.gatewayStatusCode,
      'gatewayStatusMsg': instance.gatewayStatusMsg,
      'receipt': instance.receipt,
      'auditDetails': instance.auditDetails,
      'additionalDetails': instance.additionalDetails,
      'bankTransactionNo': instance.bankTransactionNo,
    };

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..isWhatsapp = json['isWhatsapp'] as bool?
      ..taxAndPayments = (json['taxAndPayments'] as List<dynamic>?)
          ?.map((e) => TaxAndPayment.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'isWhatsapp': instance.isWhatsapp,
      'taxAndPayments': instance.taxAndPayments,
    };

TaxAndPayment _$TaxAndPaymentFromJson(Map<String, dynamic> json) =>
    TaxAndPayment()
      ..taxAmount = json['taxAmount']
      ..amountPaid = (json['amountPaid'] as num?)?.toInt()
      ..billId = json['billId'] as String?;

Map<String, dynamic> _$TaxAndPaymentToJson(TaxAndPayment instance) =>
    <String, dynamic>{
      'taxAmount': instance.taxAmount,
      'amountPaid': instance.amountPaid,
      'billId': instance.billId,
    };

AuditDetails _$AuditDetailsFromJson(Map<String, dynamic> json) => AuditDetails()
  ..createdBy = json['createdBy'] as String?
  ..createdTime = (json['createdTime'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy']
  ..lastModifiedTime = json['lastModifiedTime'];

Map<String, dynamic> _$AuditDetailsToJson(AuditDetails instance) =>
    <String, dynamic>{
      'createdBy': instance.createdBy,
      'createdTime': instance.createdTime,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedTime': instance.lastModifiedTime,
    };

User _$UserFromJson(Map<String, dynamic> json) => User()
  ..id = (json['id'] as num?)?.toInt()
  ..uuid = json['uuid'] as String?
  ..name = json['name'] as String?
  ..userName = json['userName'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
      'id': instance.id,
      'uuid': instance.uuid,
      'name': instance.name,
      'userName': instance.userName,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'tenantId': instance.tenantId,
    };
