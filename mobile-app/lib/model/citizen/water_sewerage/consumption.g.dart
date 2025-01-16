// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'consumption.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Consumption _$ConsumptionFromJson(Map<String, dynamic> json) => Consumption()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..meterReadings = (json['meterReadings'] as List<dynamic>?)
      ?.map((e) => MeterReading.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$ConsumptionToJson(Consumption instance) =>
    <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'meterReadings': instance.meterReadings,
    };

MeterReading _$MeterReadingFromJson(Map<String, dynamic> json) => MeterReading()
  ..id = json['id'] as String?
  ..billingPeriod = json['billingPeriod'] as String?
  ..meterStatus = json['meterStatus'] as String?
  ..lastReading = (json['lastReading'] as num?)?.toInt()
  ..lastReadingDate = (json['lastReadingDate'] as num?)?.toInt()
  ..currentReading = (json['currentReading'] as num?)?.toInt()
  ..currentReadingDate = (json['currentReadingDate'] as num?)?.toInt()
  ..connectionNo = json['connectionNo'] as String?
  ..consumption = json['consumption']
  ..generateDemand = json['generateDemand'] as bool?
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..tenantId = json['tenantId'] as String?
  ..status = json['status'];

Map<String, dynamic> _$MeterReadingToJson(MeterReading instance) =>
    <String, dynamic>{
      'id': instance.id,
      'billingPeriod': instance.billingPeriod,
      'meterStatus': instance.meterStatus,
      'lastReading': instance.lastReading,
      'lastReadingDate': instance.lastReadingDate,
      'currentReading': instance.currentReading,
      'currentReadingDate': instance.currentReadingDate,
      'connectionNo': instance.connectionNo,
      'consumption': instance.consumption,
      'generateDemand': instance.generateDemand,
      'auditDetails': instance.auditDetails,
      'tenantId': instance.tenantId,
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
