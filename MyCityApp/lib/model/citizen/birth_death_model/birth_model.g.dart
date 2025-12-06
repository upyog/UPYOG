// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'birth_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Birth _$BirthFromJson(Map<String, dynamic> json) => Birth()
  ..responseInfo = json['responseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['responseInfo'] as Map<String, dynamic>)
  ..applications = (json['applications'] as List<dynamic>?)
      ?.map((e) => Application.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$BirthToJson(Birth instance) => <String, dynamic>{
      'responseInfo': instance.responseInfo,
      'applications': instance.applications,
    };

Application _$ApplicationFromJson(Map<String, dynamic> json) => Application()
  ..applicationCategory = json['applicationCategory'] as String?
  ..applicationType = json['applicationType'] as String?
  ..applicationNumber = json['applicationNumber'] as String?
  ..applicationDate = json['applicationDate'] as String?
  ..regNo = json['regNo'] as String?
  ..name = json['name'] as String?
  ..status = json['status'] as String?
  ..tenantId = json['tenantId'] as String?
  ..fileStoreId = json['fileStoreId'] as String?;

Map<String, dynamic> _$ApplicationToJson(Application instance) =>
    <String, dynamic>{
      'applicationCategory': instance.applicationCategory,
      'applicationType': instance.applicationType,
      'applicationNumber': instance.applicationNumber,
      'applicationDate': instance.applicationDate,
      'regNo': instance.regNo,
      'name': instance.name,
      'status': instance.status,
      'tenantId': instance.tenantId,
      'fileStoreId': instance.fileStoreId,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver'] as String?
  ..ts = json['ts'] as String?
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
