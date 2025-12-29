// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PaymentModel _$PaymentModelFromJson(Map<String, dynamic> json) => PaymentModel()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..payments = (json['Payments'] as List<dynamic>?)
      ?.map((e) => Payment.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$PaymentModelToJson(PaymentModel instance) =>
    <String, dynamic>{
      if (instance.responseInfo case final value?) 'ResponseInfo': value,
      'Payments': instance.payments,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver']
  ..ts = json['ts']
  ..resMsgId = json['resMsgId'] as String?
  ..msgId = json['msgId'] as String?
  ..status = json['status'] as String?;

Map<String, dynamic> _$ResponseInfoToJson(ResponseInfo instance) =>
    <String, dynamic>{
      'apiId': instance.apiId,
      'ver': instance.ver,
      'ts': instance.ts,
      'resMsgId': instance.resMsgId,
      'msgId': instance.msgId,
      'status': instance.status,
    };

Payment _$PaymentFromJson(Map<String, dynamic> json) => Payment()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..totalDue = (json['totalDue'] as num?)?.toDouble()
  ..totalAmountPaid = (json['totalAmountPaid'] as num?)?.toDouble()
  ..transactionNumber = json['transactionNumber'] as String?
  ..transactionDate = (json['transactionDate'] as num?)?.toInt()
  ..paymentMode = json['paymentMode'] as String?
  ..instrumentDate = (json['instrumentDate'] as num?)?.toInt()
  ..instrumentNumber = json['instrumentNumber'] as String?
  ..instrumentStatus = json['instrumentStatus'] as String?
  ..ifscCode = json['ifscCode']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : PaymentAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..paymentDetails = (json['paymentDetails'] as List<dynamic>?)
      ?.map((e) => PaymentDetail.fromJson(e as Map<String, dynamic>))
      .toList()
  ..paidBy = json['paidBy'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..payerName = json['payerName'] as String?
  ..payerAddress = json['payerAddress']
  ..payerEmail = json['payerEmail']
  ..payerId = json['payerId'] as String?
  ..paymentStatus = json['paymentStatus'] as String?
  ..fileStoreId = fileStoreIdFromJson(json['fileStoreId']);

Map<String, dynamic> _$PaymentToJson(Payment instance) => <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'totalDue': instance.totalDue,
      'totalAmountPaid': instance.totalAmountPaid,
      'transactionNumber': instance.transactionNumber,
      'transactionDate': instance.transactionDate,
      'paymentMode': instance.paymentMode,
      'instrumentDate': instance.instrumentDate,
      'instrumentNumber': instance.instrumentNumber,
      'instrumentStatus': instance.instrumentStatus,
      'ifscCode': instance.ifscCode,
      'auditDetails': instance.auditDetails,
      'additionalDetails': instance.additionalDetails,
      'paymentDetails': instance.paymentDetails,
      'paidBy': instance.paidBy,
      'mobileNumber': instance.mobileNumber,
      'payerName': instance.payerName,
      'payerAddress': instance.payerAddress,
      'payerEmail': instance.payerEmail,
      'payerId': instance.payerId,
      'paymentStatus': instance.paymentStatus,
      'fileStoreId': instance.fileStoreId,
    };

PaymentAdditionalDetails _$PaymentAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    PaymentAdditionalDetails()
      ..isWhatsapp = json['isWhatsapp'] as bool?
      ..taxAndPayments = (json['taxAndPayments'] as List<dynamic>?)
          ?.map((e) => TaxAndPayment.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$PaymentAdditionalDetailsToJson(
        PaymentAdditionalDetails instance) =>
    <String, dynamic>{
      'isWhatsapp': instance.isWhatsapp,
      'taxAndPayments': instance.taxAndPayments,
    };

TaxAndPayment _$TaxAndPaymentFromJson(Map<String, dynamic> json) =>
    TaxAndPayment()
      ..billId = json['billId'] as String?
      ..taxAmount = json['taxAmount']
      ..amountPaid = (json['amountPaid'] as num?)?.toInt();

Map<String, dynamic> _$TaxAndPaymentToJson(TaxAndPayment instance) =>
    <String, dynamic>{
      'billId': instance.billId,
      'taxAmount': instance.taxAmount,
      'amountPaid': instance.amountPaid,
    };

AuditDetails _$AuditDetailsFromJson(Map<String, dynamic> json) => AuditDetails()
  ..createdBy = json['createdBy'] as String?
  ..createdTime = (json['createdTime'] as num?)?.toInt()
  ..lastModifiedBy = json['lastModifiedBy'] as String?
  ..lastModifiedTime = (json['lastModifiedTime'] as num?)?.toInt();

Map<String, dynamic> _$AuditDetailsToJson(AuditDetails instance) =>
    <String, dynamic>{
      'createdBy': instance.createdBy,
      'createdTime': instance.createdTime,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedTime': instance.lastModifiedTime,
    };

PaymentDetail _$PaymentDetailFromJson(Map<String, dynamic> json) =>
    PaymentDetail()
      ..paymentId = json['paymentId']
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..totalDue = (json['totalDue'] as num?)?.toInt()
      ..totalAmountPaid = (json['totalAmountPaid'] as num?)?.toInt()
      ..receiptNumber = json['receiptNumber'] as String?
      ..manualReceiptNumber = json['manualReceiptNumber']
      ..manualReceiptDate = (json['manualReceiptDate'] as num?)?.toInt()
      ..receiptDate = (json['receiptDate'] as num?)?.toInt()
      ..receiptType = json['receiptType'] as String?
      ..businessService = json['businessService'] as String?
      ..billId = json['billId'] as String?
      ..bill = json['bill'] == null
          ? null
          : Bill.fromJson(json['bill'] as Map<String, dynamic>)
      ..additionalDetails = json['additionalDetails'] == null
          ? null
          : AdditionalDetailsClass.fromJson(
              json['additionalDetails'] as Map<String, dynamic>)
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$PaymentDetailToJson(PaymentDetail instance) =>
    <String, dynamic>{
      'paymentId': instance.paymentId,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'totalDue': instance.totalDue,
      'totalAmountPaid': instance.totalAmountPaid,
      'receiptNumber': instance.receiptNumber,
      'manualReceiptNumber': instance.manualReceiptNumber,
      'manualReceiptDate': instance.manualReceiptDate,
      'receiptDate': instance.receiptDate,
      'receiptType': instance.receiptType,
      'businessService': instance.businessService,
      'billId': instance.billId,
      'bill': instance.bill,
      if (instance.additionalDetails case final value?)
        'additionalDetails': value,
      'auditDetails': instance.auditDetails,
    };

AdditionalDetailsClass _$AdditionalDetailsClassFromJson(
        Map<String, dynamic> json) =>
    AdditionalDetailsClass()
      ..assessmentYears = json['assessmentYears'] as String?
      ..arrearArray = (json['arrearArray'] as List<dynamic>?)
          ?.map((e) => Array.fromJson(e as Map<String, dynamic>))
          .toList()
      ..taxArray = (json['taxArray'] as List<dynamic>?)
          ?.map((e) => Array.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$AdditionalDetailsClassToJson(
        AdditionalDetailsClass instance) =>
    <String, dynamic>{
      if (instance.assessmentYears case final value?) 'assessmentYears': value,
      if (instance.arrearArray case final value?) 'arrearArray': value,
      if (instance.taxArray case final value?) 'taxArray': value,
    };

Array _$ArrayFromJson(Map<String, dynamic> json) => Array()
  ..year = json['year'] as String?
  ..tax = (json['tax'] as num?)?.toInt()
  ..firecess = json['firecess']
  ..cancercess = json['cancercess']
  ..penalty = json['penalty']
  ..rebate = json['rebate']
  ..interest = json['interest']
  ..usageExemption = json['usageExemption']
  ..specialCategoryExemption = json['specialCategoryExemption']
  ..adhocPenalty = json['adhocPenalty']
  ..adhocRebate = json['adhocRebate']
  ..roundOff = json['roundoff']
  ..total = (json['total'] as num?)?.toInt();

Map<String, dynamic> _$ArrayToJson(Array instance) => <String, dynamic>{
      'year': instance.year,
      'tax': instance.tax,
      'firecess': instance.firecess,
      'cancercess': instance.cancercess,
      'penalty': instance.penalty,
      'rebate': instance.rebate,
      'interest': instance.interest,
      'usageExemption': instance.usageExemption,
      'specialCategoryExemption': instance.specialCategoryExemption,
      'adhocPenalty': instance.adhocPenalty,
      'adhocRebate': instance.adhocRebate,
      'roundoff': instance.roundOff,
      'total': instance.total,
    };

Bill _$BillFromJson(Map<String, dynamic> json) => Bill()
  ..id = json['id'] as String?
  ..mobileNumber = json['mobileNumber']
  ..paidBy = json['paidBy']
  ..payerName = json['payerName']
  ..payerAddress = json['payerAddress']
  ..payerEmail = json['payerEmail']
  ..payerId = json['payerId']
  ..status = json['status'] as String?
  ..reasonForCancellation = json['reasonForCancellation']
  ..isCancelled = json['isCancelled']
  ..additionalDetails = json['additionalDetails']
  ..billDetails = (json['billDetails'] as List<dynamic>?)
      ?.map((e) => BillDetail.fromJson(e as Map<String, dynamic>))
      .toList()
  ..tenantId = json['tenantId'] as String?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..collectionModesNotAllowed =
      (json['collectionModesNotAllowed'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList()
  ..partPaymentAllowed = json['partPaymentAllowed'] as bool?
  ..isAdvanceAllowed = json['isAdvanceAllowed'] as bool?
  ..minimumAmountToBePaid = json['minimumAmountToBePaid']
  ..businessService = json['businessService'] as String?
  ..totalAmount = (json['totalAmount'] as num?)?.toInt()
  ..consumerCode = json['consumerCode'] as String?
  ..billNumber = json['billNumber'] as String?
  ..billDate = (json['billDate'] as num?)?.toInt()
  ..amountPaid = json['amountPaid'];

Map<String, dynamic> _$BillToJson(Bill instance) => <String, dynamic>{
      'id': instance.id,
      'mobileNumber': instance.mobileNumber,
      'paidBy': instance.paidBy,
      'payerName': instance.payerName,
      'payerAddress': instance.payerAddress,
      'payerEmail': instance.payerEmail,
      'payerId': instance.payerId,
      'status': instance.status,
      'reasonForCancellation': instance.reasonForCancellation,
      'isCancelled': instance.isCancelled,
      'additionalDetails': instance.additionalDetails,
      'billDetails': instance.billDetails,
      'tenantId': instance.tenantId,
      'auditDetails': instance.auditDetails,
      'collectionModesNotAllowed': instance.collectionModesNotAllowed,
      'partPaymentAllowed': instance.partPaymentAllowed,
      'isAdvanceAllowed': instance.isAdvanceAllowed,
      'minimumAmountToBePaid': instance.minimumAmountToBePaid,
      'businessService': instance.businessService,
      'totalAmount': instance.totalAmount,
      'consumerCode': instance.consumerCode,
      'billNumber': instance.billNumber,
      'billDate': instance.billDate,
      'amountPaid': instance.amountPaid,
    };

BillDetail _$BillDetailFromJson(Map<String, dynamic> json) => BillDetail()
  ..billDescription = json['billDescription']
  ..displayMessage = json['displayMessage']
  ..callBackForApportioning = json['callBackForApportioning']
  ..cancellationRemarks = json['cancellationRemarks']
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..demandId = json['demandId'] as String?
  ..billId = json['billId'] as String?
  ..amount = (json['amount'] as num?)?.toInt()
  ..amountPaid = (json['amountPaid'] as num?)?.toInt()
  ..fromPeriod = (json['fromPeriod'] as num?)?.toInt()
  ..toPeriod = (json['toPeriod'] as num?)?.toInt()
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : BillDetailAdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>)
  ..channel = json['channel']
  ..voucherHeader = json['voucherHeader']
  ..boundary = json['boundary']
  ..manualReceiptNumber = json['manualReceiptNumber']
  ..manualReceiptDate = json['manualReceiptDate']
  ..billAccountDetails = (json['billAccountDetails'] as List<dynamic>?)
      ?.map((e) => BillAccountDetail.fromJson(e as Map<String, dynamic>))
      .toList()
  ..collectionType = json['collectionType']
  ..auditDetails = json['auditDetails']
  ..expiryDate = (json['expiryDate'] as num?)?.toInt();

Map<String, dynamic> _$BillDetailToJson(BillDetail instance) =>
    <String, dynamic>{
      'billDescription': instance.billDescription,
      'displayMessage': instance.displayMessage,
      'callBackForApportioning': instance.callBackForApportioning,
      'cancellationRemarks': instance.cancellationRemarks,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'demandId': instance.demandId,
      'billId': instance.billId,
      'amount': instance.amount,
      'amountPaid': instance.amountPaid,
      'fromPeriod': instance.fromPeriod,
      'toPeriod': instance.toPeriod,
      'additionalDetails': instance.additionalDetails,
      'channel': instance.channel,
      'voucherHeader': instance.voucherHeader,
      'boundary': instance.boundary,
      'manualReceiptNumber': instance.manualReceiptNumber,
      'manualReceiptDate': instance.manualReceiptDate,
      'billAccountDetails': instance.billAccountDetails,
      'collectionType': instance.collectionType,
      'auditDetails': instance.auditDetails,
      'expiryDate': instance.expiryDate,
    };

BillDetailAdditionalDetails _$BillDetailAdditionalDetailsFromJson(
        Map<String, dynamic> json) =>
    BillDetailAdditionalDetails()
      ..calculationDescription =
          (json['calculationDescription'] as List<dynamic>?)
              ?.map((e) => e as String)
              .toList();

Map<String, dynamic> _$BillDetailAdditionalDetailsToJson(
        BillDetailAdditionalDetails instance) =>
    <String, dynamic>{
      'calculationDescription': instance.calculationDescription,
    };

BillAccountDetail _$BillAccountDetailFromJson(Map<String, dynamic> json) =>
    BillAccountDetail()
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..billDetailId = json['billDetailId'] as String?
      ..demandDetailId = json['demandDetailId'] as String?
      ..order = (json['order'] as num?)?.toInt()
      ..amount = (json['amount'] as num?)?.toInt()
      ..adjustedAmount = (json['adjustedAmount'] as num?)?.toInt()
      ..isActualDemand = json['isActualDemand']
      ..taxHeadCode = json['taxHeadCode'] as String?
      ..additionalDetails = json['additionalDetails']
      ..purpose = json['purpose']
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
      'isActualDemand': instance.isActualDemand,
      'taxHeadCode': instance.taxHeadCode,
      'additionalDetails': instance.additionalDetails,
      'purpose': instance.purpose,
      'auditDetails': instance.auditDetails,
    };
