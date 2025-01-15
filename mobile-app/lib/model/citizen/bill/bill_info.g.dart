// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bill_info.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BillInfo _$BillInfoFromJson(Map<String, dynamic> json) => BillInfo()
  ..resposneInfo = json['ResposneInfo'] == null
      ? null
      : ResposneInfo.fromJson(json['ResposneInfo'] as Map<String, dynamic>)
  ..bill = (json['Bill'] as List<dynamic>?)
      ?.map((e) => Bill.fromJson(e as Map<String, dynamic>))
      .toList()
  ..bills = (json['Bills'] as List<dynamic>?)
      ?.map((e) => Bill.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$BillInfoToJson(BillInfo instance) => <String, dynamic>{
      'ResposneInfo': instance.resposneInfo,
      'Bill': instance.bill,
      'Bills': instance.bills,
    };

Bill _$BillFromJson(Map<String, dynamic> json) => Bill()
  ..id = json['id'] as String?
  ..userId = json['userId'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..payerName = json['payerName'] as String?
  ..payerAddress = json['payerAddress'] as String?
  ..payerEmail = json['payerEmail']
  ..status = json['status'] as String?
  ..totalAmount = (json['totalAmount'] as num?)?.toDouble()
  ..businessService = json['businessService'] as String?
  ..billNumber = json['billNumber'] as String?
  ..billDate = (json['billDate'] as num?)?.toInt()
  ..consumerCode = json['consumerCode'] as String?
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : AdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..billDetails = (json['billDetails'] as List<dynamic>?)
      ?.map((e) => BillDetail.fromJson(e as Map<String, dynamic>))
      .toList()
  ..tenantId = json['tenantId'] as String?
  ..fileStoreId = json['fileStoreId']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$BillToJson(Bill instance) => <String, dynamic>{
      'id': instance.id,
      'userId': instance.userId,
      'mobileNumber': instance.mobileNumber,
      'payerName': instance.payerName,
      'payerAddress': instance.payerAddress,
      'payerEmail': instance.payerEmail,
      'status': instance.status,
      'totalAmount': instance.totalAmount,
      'businessService': instance.businessService,
      'billNumber': instance.billNumber,
      'billDate': instance.billDate,
      'consumerCode': instance.consumerCode,
      'additionalDetails': instance.additionalDetails,
      'billDetails': instance.billDetails,
      'tenantId': instance.tenantId,
      'fileStoreId': instance.fileStoreId,
      'auditDetails': instance.auditDetails,
    };

AuditDetails _$AuditDetailsFromJson(Map<String, dynamic> json) => AuditDetails()
  ..createdBy = json['createdBy'] as String?
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..createdTime = (json['createdTime'] as num?)?.toInt()
  ..lastModifiedTime = (json['lastModifiedTime'] as num?)?.toInt();

Map<String, dynamic> _$AuditDetailsToJson(AuditDetails instance) =>
    <String, dynamic>{
      'createdBy': instance.createdBy,
      'lastModifiedBy': instance.lastModifiedBy,
      'createdTime': instance.createdTime,
      'lastModifiedTime': instance.lastModifiedTime,
    };

BillDetail _$BillDetailFromJson(Map<String, dynamic> json) => BillDetail()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..demandId = json['demandId'] as String?
  ..billId = json['billId'] as String?
  ..expiryDate = (json['expiryDate'] as num?)?.toInt()
  ..amount = (json['amount'] as num?)?.toDouble()
  ..amountPaid = json['amountPaid']
  ..fromPeriod = (json['fromPeriod'] as num?)?.toInt()
  ..toPeriod = (json['toPeriod'] as num?)?.toInt()
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : AdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..billAccountDetails = (json['billAccountDetails'] as List<dynamic>?)
      ?.map((e) => BillAccountDetail.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$BillDetailToJson(BillDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'demandId': instance.demandId,
      'billId': instance.billId,
      'expiryDate': instance.expiryDate,
      'amount': instance.amount,
      'amountPaid': instance.amountPaid,
      'fromPeriod': instance.fromPeriod,
      'toPeriod': instance.toPeriod,
      'additionalDetails': instance.additionalDetails,
      'billAccountDetails': instance.billAccountDetails,
    };

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..calculationDescription =
          (json['calculationDescription'] as List<dynamic>?)
              ?.map((e) => e as String)
              .toList()
      ..propertyId = json['propertyId'] as String?;

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('calculationDescription', instance.calculationDescription);
  writeNotNull('propertyId', instance.propertyId);
  return val;
}

BillAccountDetail _$BillAccountDetailFromJson(Map<String, dynamic> json) =>
    BillAccountDetail()
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..billDetailId = json['billDetailId'] as String?
      ..demandDetailId = json['demandDetailId'] as String?
      ..order = (json['order'] as num?)?.toInt()
      ..amount = (json['amount'] as num?)?.toDouble()
      ..adjustedAmount = (json['adjustedAmount'] as num?)?.toDouble()
      ..taxHeadCode = json['taxHeadCode'] as String?
      ..additionalDetails = json['additionalDetails']
      ..auditDetails = json['auditDetails'];

Map<String, dynamic> _$BillAccountDetailToJson(BillAccountDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'billDetailId': instance.billDetailId,
      'demandDetailId': instance.demandDetailId,
      'order': instance.order,
      'amount': instance.amount,
      'adjustedAmount': instance.adjustedAmount,
      'taxHeadCode': instance.taxHeadCode,
      'additionalDetails': instance.additionalDetails,
      'auditDetails': instance.auditDetails,
    };

ResposneInfo _$ResposneInfoFromJson(Map<String, dynamic> json) => ResposneInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver'] as String?
  ..ts = json['ts'] as String?
  ..resMsgId = json['resMsgId'] as String?
  ..msgId = json['msgId'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$ResposneInfoToJson(ResposneInfo instance) =>
    <String, dynamic>{
      'apiId': instance.apiId,
      'ver': instance.ver,
      'ts': instance.ts,
      'resMsgId': instance.resMsgId,
      'msgId': instance.msgId,
      'status': instance.status,
    };
