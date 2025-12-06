// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'workflow_business_service_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

WorkflowBusinessServices _$WorkflowBusinessServicesFromJson(
        Map<String, dynamic> json) =>
    WorkflowBusinessServices()
      ..businessServices = (json['BusinessServices'] as List<dynamic>?)
          ?.map((e) => BusinessService.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$WorkflowBusinessServicesToJson(
        WorkflowBusinessServices instance) =>
    <String, dynamic>{
      'BusinessServices': instance.businessServices,
    };

BusinessService _$BusinessServiceFromJson(Map<String, dynamic> json) =>
    BusinessService()
      ..tenantId = json['tenantId'] as String?
      ..uuid = json['uuid'] as String?
      ..businessService = json['businessService'] as String?
      ..business = json['business'] as String?
      ..businessServiceSla = (json['businessServiceSla'] as num?)?.toInt()
      ..states = (json['states'] as List<dynamic>?)
          ?.map((e) => State.fromJson(e as Map<String, dynamic>))
          .toList()
      ..auditDetails = json['auditDetails'] == null
          ? null
          : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$BusinessServiceToJson(BusinessService instance) =>
    <String, dynamic>{
      'tenantId': instance.tenantId,
      'uuid': instance.uuid,
      'businessService': instance.businessService,
      'business': instance.business,
      'businessServiceSla': instance.businessServiceSla,
      'states': instance.states,
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

State _$StateFromJson(Map<String, dynamic> json) => State()
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..businessServiceId = json['businessServiceId'] as String?
  ..sla = (json['sla'] as num?)?.toInt()
  ..state = json['state'] as String?
  ..applicationStatus = json['applicationStatus'] as String?
  ..docUploadRequired = json['docUploadRequired'] as bool?
  ..isStartState = json['isStartState'] as bool?
  ..isTerminateState = json['isTerminateState'] as bool?
  ..isStateUpdatable = json['isStateUpdatable'] as bool?
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

Action _$ActionFromJson(Map<String, dynamic> json) => Action()
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..uuid = json['uuid'] as String?
  ..tenantId = json['tenantId'] as String?
  ..currentState = json['currentState'] as String?
  ..action = json['action'] as String?
  ..nextState = json['nextState'] as String?
  ..roles = (json['roles'] as List<dynamic>?)?.map((e) => e as String).toList()
  ..active = json['active'] as bool?;

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
