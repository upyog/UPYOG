// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'timeline.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Timeline _$TimelineFromJson(Map<String, dynamic> json) => Timeline()
  ..responseInfo = json['ResponseInfo'] == null
      ? null
      : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
  ..processInstancesList = (json['ProcessInstances'] as List<dynamic>?)
      ?.map((e) => ProcessInstanceTimeline.fromJson(e as Map<String, dynamic>))
      .toList()
  ..totalCount = (json['totalCount'] as num?)?.toInt();

Map<String, dynamic> _$TimelineToJson(Timeline instance) => <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'ProcessInstances': instance.processInstancesList,
      'totalCount': instance.totalCount,
    };

ProcessInstanceTimeline _$ProcessInstanceTimelineFromJson(
        Map<String, dynamic> json) =>
    ProcessInstanceTimeline()
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..businessService = json['businessService'] as String?
      ..businessId = json['businessId'] as String?
      ..action = json['action'] as String?
      ..moduleName = json['moduleName'] as String?
      ..state = json['state'] == null
          ? null
          : State.fromJson(json['state'] as Map<String, dynamic>)
      ..comment = json['comment'] as String?
      ..documents = (json['documents'] as List<dynamic>?)
          ?.map((e) => DocumentTimeLine.fromJson(e as Map<String, dynamic>))
          .toList()
      ..assigner = json['assigner'] == null
          ? null
          : Assigne.fromJson(json['assigner'] as Map<String, dynamic>)
      ..assignes = (json['assignes'] as List<dynamic>?)
          ?.map((e) => Assigne.fromJson(e as Map<String, dynamic>))
          .toList()
      ..nextActions = (json['nextActions'] as List<dynamic>?)
          ?.map((e) => Action.fromJson(e as Map<String, dynamic>))
          .toList()
      ..stateSla = (json['stateSla'] as num?)?.toInt()
      ..businesssServiceSla = (json['businesssServiceSla'] as num?)?.toInt()
      ..previousStatus = json['previousStatus']
      ..entity = json['entity']
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
      ..rating = (json['rating'] as num?)?.toInt()
      ..escalated = json['escalated'] as bool?;

Map<String, dynamic> _$ProcessInstanceTimelineToJson(
        ProcessInstanceTimeline instance) =>
    <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'businessService': instance.businessService,
      'businessId': instance.businessId,
      'action': instance.action,
      'moduleName': instance.moduleName,
      'state': instance.state,
      'comment': instance.comment,
      'documents': instance.documents,
      'assigner': instance.assigner,
      'assignes': instance.assignes,
      'nextActions': instance.nextActions,
      'stateSla': instance.stateSla,
      'businesssServiceSla': instance.businesssServiceSla,
      'previousStatus': instance.previousStatus,
      'entity': instance.entity,
      'auditDetails': instance.auditDetails,
      'rating': instance.rating,
      'escalated': instance.escalated,
    };

Assigne _$AssigneFromJson(Map<String, dynamic> json) => Assigne()
  ..id = (json['id'] as num?)?.toInt()
  ..userName = json['userName'] as String?
  ..name = json['name'] as String?
  ..type = json['type'] as String?
  ..mobileNumber = json['mobileNumber'] as String?
  ..emailId = json['emailId'] as String?
  ..roles = (json['roles'] as List<dynamic>?)
      ?.map((e) => Roles.fromJson(e as Map<String, dynamic>))
      .toList()
  ..tenantId = json['tenantId'] as String?
  ..uuid = json['uuid'] as String?;

Map<String, dynamic> _$AssigneToJson(Assigne instance) => <String, dynamic>{
      'id': instance.id,
      'userName': instance.userName,
      'name': instance.name,
      'type': instance.type,
      'mobileNumber': instance.mobileNumber,
      'emailId': instance.emailId,
      'roles': instance.roles,
      'tenantId': instance.tenantId,
      'uuid': instance.uuid,
    };

Roles _$RolesFromJson(Map<String, dynamic> json) => Roles()
  ..id = (json['id'] as num?)?.toInt()
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$RolesToJson(Roles instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'tenantId': instance.tenantId,
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

DocumentTimeLine _$DocumentTimeLineFromJson(Map<String, dynamic> json) =>
    DocumentTimeLine()
      ..id = json['id'] as String?
      ..tenantId = json['tenantId'] as String?
      ..documentType = json['documentType'] as String?
      ..fileStoreId = json['fileStoreId'] as String?
      ..documentUid = json['documentUid'] as String?
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$DocumentTimeLineToJson(DocumentTimeLine instance) =>
    <String, dynamic>{
      'id': instance.id,
      'tenantId': instance.tenantId,
      'documentType': instance.documentType,
      'fileStoreId': instance.fileStoreId,
      'documentUid': instance.documentUid,
      'auditDetails': instance.auditDetails,
    };

Action _$ActionFromJson(Map<String, dynamic> json) => Action()
  ..auditDetails = json['auditDetails']
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..currentState = json['currentState'] as String?
  ..action = json['action'] as String?
  ..nextState = json['nextState'] as String?
  ..roles = (json['roles'] as List<dynamic>?)?.map((e) => e as String).toList()
  ..active = json['active'];

Map<String, dynamic> _$ActionToJson(Action instance) => <String, dynamic>{
      'auditDetails': instance.auditDetails,
      'uuid': instance.uuid,
      'tenantId': instance.tenantId,
      'currentState': instance.currentState,
      'action': instance.action,
      'nextState': instance.nextState,
      'roles': instance.roles,
      'active': instance.active,
    };

State _$StateFromJson(Map<String, dynamic> json) => State()
  ..auditDetails = json['auditDetails']
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessServiceId = json['businessServiceId'] as String?
  ..sla = (json['sla'] as num?)?.toInt()
  ..state = json['state'] as String?
  ..applicationStatus = json['applicationStatus'] as String?
  ..docUploadRequired = json['docUploadRequired'] as bool?
  ..isStartState = json['isStartState'] as bool?
  ..isTerminateState = json['isTerminateState'] as bool?
  ..isStateUpdatable = json['isStateUpdatable']
  ..actions = (json['actions'] as List<dynamic>?)
      ?.map((e) => Action.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$StateToJson(State instance) => <String, dynamic>{
      'auditDetails': instance.auditDetails,
      'uuid': instance.uuid,
      'tenantId': instance.tenantId,
      'businessServiceId': instance.businessServiceId,
      'sla': instance.sla,
      'state': instance.state,
      'applicationStatus': instance.applicationStatus,
      'docUploadRequired': instance.docUploadRequired,
      'isStartState': instance.isStartState,
      'isTerminateState': instance.isTerminateState,
      'isStateUpdatable': instance.isStateUpdatable,
      'actions': instance.actions,
    };
