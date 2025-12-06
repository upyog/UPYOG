// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'demands.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DemandsModel _$DemandsModelFromJson(Map<String, dynamic> json) => DemandsModel()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..demands = (json['Demands'] as List<dynamic>?)
      ?.map((e) => Demand.fromJson(e as Map<String, dynamic>))
      .toList()
  ..collectedReceipt = json['CollectedReceipt'];

Map<String, dynamic> _$DemandsModelToJson(DemandsModel instance) =>
    <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'Demands': instance.demands,
      'CollectedReceipt': instance.collectedReceipt,
    };

Demand _$DemandFromJson(Map<String, dynamic> json) => Demand()
  ..isPaymentCompleted = json['isPaymentCompleted'] as bool?
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..consumerCode = json['consumerCode'] as String?
  ..consumerType = json['consumerType'] as String?
  ..businessService = json['businessService'] as String?
  ..payer = json['payer'] == null
      ? null
      : Payer.fromJson(json['payer'] as Map<String, dynamic>)
  ..taxPeriodFrom = (json['taxPeriodFrom'] as num?)?.toInt()
  ..taxPeriodTo = (json['taxPeriodTo'] as num?)?.toInt()
  ..demandDetails = (json['demandDetails'] as List<dynamic>?)
      ?.map((e) => DemandDetail.fromJson(e as Map<String, dynamic>))
      .toList()
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..fixedBillExpiryDate = json['fixedBillExpiryDate']
  ..billExpiryTime = (json['billExpiryTime'] as num?)?.toInt()
  ..additionalDetails = json['additionalDetails']
  ..minimumAmountPayable = (json['minimumAmountPayable'] as num?)?.toInt()
  ..status = json['status'] as String?;

Map<String, dynamic> _$DemandToJson(Demand instance) => <String, dynamic>{
      'isPaymentCompleted': instance.isPaymentCompleted,
      'id': instance.id,
      'tenantId': instance.tenantId,
      'consumerCode': instance.consumerCode,
      'consumerType': instance.consumerType,
      'businessService': instance.businessService,
      'payer': instance.payer,
      'taxPeriodFrom': instance.taxPeriodFrom,
      'taxPeriodTo': instance.taxPeriodTo,
      'demandDetails': instance.demandDetails,
      'auditDetails': instance.auditDetails,
      'fixedBillExpiryDate': instance.fixedBillExpiryDate,
      'billExpiryTime': instance.billExpiryTime,
      'additionalDetails': instance.additionalDetails,
      'minimumAmountPayable': instance.minimumAmountPayable,
      'status': instance.status,
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

DemandDetail _$DemandDetailFromJson(Map<String, dynamic> json) => DemandDetail()
  ..id = json['id'] as String?
  ..demandId = json['demandId'] as String?
  ..taxHeadMasterCode = json['taxHeadMasterCode'] as String?
  ..taxAmount = (json['taxAmount'] as num?)?.toInt()
  ..collectionAmount = (json['collectionAmount'] as num?)?.toInt()
  ..additionalDetails = json['additionalDetails']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$DemandDetailToJson(DemandDetail instance) =>
    <String, dynamic>{
      'id': instance.id,
      'demandId': instance.demandId,
      'taxHeadMasterCode': instance.taxHeadMasterCode,
      'taxAmount': instance.taxAmount,
      'collectionAmount': instance.collectionAmount,
      'additionalDetails': instance.additionalDetails,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
    };

Payer _$PayerFromJson(Map<String, dynamic> json) => Payer()
  ..uuid = json['uuid'] as String?
  ..id = (json['id'] as num?)?.toInt()
  ..userName = json['userName'] as String?
  ..type = json['type'] as String?
  ..salutation = json['salutation']
  ..name = json['name'] as String?
  ..gender = json['gender']
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..altContactNumber = json['altContactNumber']
  ..pan = json['pan']
  ..aadhaarNumber = json['aadhaarNumber']
  ..permanentAddress = json['permanentAddress']
  ..permanentCity = json['permanentCity']
  ..permanentPinCode = json['permanentPinCode']
  ..correspondenceAddress = json['correspondenceAddress']
  ..correspondenceCity = json['correspondenceCity']
  ..correspondencePinCode = json['correspondencePinCode']
  ..active = json['active'] as bool?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$PayerToJson(Payer instance) => <String, dynamic>{
      'uuid': instance.uuid,
      'id': instance.id,
      'userName': instance.userName,
      'type': instance.type,
      'salutation': instance.salutation,
      'name': instance.name,
      'gender': instance.gender,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'altContactNumber': instance.altContactNumber,
      'pan': instance.pan,
      'aadhaarNumber': instance.aadhaarNumber,
      'permanentAddress': instance.permanentAddress,
      'permanentCity': instance.permanentCity,
      'permanentPinCode': instance.permanentPinCode,
      'correspondenceAddress': instance.correspondenceAddress,
      'correspondenceCity': instance.correspondenceCity,
      'correspondencePinCode': instance.correspondencePinCode,
      'active': instance.active,
      'tenantId': instance.tenantId,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver']
  ..ts = json['ts']
  ..resMsgId = json['resMsgId']
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
