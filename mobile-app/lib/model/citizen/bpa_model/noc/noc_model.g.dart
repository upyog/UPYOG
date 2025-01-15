// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'noc_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Noc _$NocFromJson(Map<String, dynamic> json) => Noc()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..noc = (json['Noc'] as List<dynamic>?)
      ?.map((e) => NocElement.fromJson(e as Map<String, dynamic>))
      .toList()
  ..count = (json['count'] as num?)?.toInt();

Map<String, dynamic> _$NocToJson(Noc instance) => <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'Noc': instance.noc,
      'count': instance.count,
    };

NocElement _$NocElementFromJson(Map<String, dynamic> json) => NocElement()
  ..id = json['id'] as String?
  ..tenantId = json['tenantId'] as String?
  ..applicationNo = json['applicationNo'] as String?
  ..nocNo = json['nocNo']
  ..applicationType = json['applicationType'] as String?
  ..nocType = json['nocType'] as String?
  ..accountId = json['accountId'] as String?
  ..source = json['source'] as String?
  ..sourceRefId = json['sourceRefId'] as String?
  ..landId = json['landId']
  ..status = json['status']
  ..applicationStatus = json['applicationStatus'] as String?
  ..documents = (json['documents'] as List<dynamic>?)
      ?.map((e) => Document.fromJson(e as Map<String, dynamic>))
      .toList()
  ..workflow = json['workflow']
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : AdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$NocElementToJson(NocElement instance) =>
    <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'applicationNo': instance.applicationNo,
      'nocNo': instance.nocNo,
      'applicationType': instance.applicationType,
      'nocType': instance.nocType,
      'accountId': instance.accountId,
      'source': instance.source,
      'sourceRefId': instance.sourceRefId,
      'landId': instance.landId,
      'status': instance.status,
      'applicationStatus': instance.applicationStatus,
      'documents': instance.documents,
      'workflow': instance.workflow,
      'auditDetails': instance.auditDetails,
      'additionalDetails': instance.additionalDetails,
    };

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..mode = json['mode'] as String?
      ..submittedOn = json['SubmittedOn'] as String?
      ..currentOwner = json['currentOwner']
      ..workflowCode = json['workflowCode'] as String?
      ..applicantName = json['applicantName'] as String?;

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'mode': instance.mode,
      'SubmittedOn': instance.submittedOn,
      'currentOwner': instance.currentOwner,
      'workflowCode': instance.workflowCode,
      'applicantName': instance.applicantName,
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

Document _$DocumentFromJson(Map<String, dynamic> json) => Document()
  ..id = json['id'] as String?
  ..documentType = json['documentType'] as String?
  ..fileStoreId = json['fileStoreId'] as String?
  ..documentUid = json['documentUid']
  ..additionalDetails = json['additionalDetails'];

Map<String, dynamic> _$DocumentToJson(Document instance) => <String, dynamic>{
      'id': instance.id,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
      'additionalDetails': instance.additionalDetails,
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
