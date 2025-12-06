// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

NotificationList _$NotificationListFromJson(Map<String, dynamic> json) =>
    NotificationList()
      ..events = (json['events'] as List<dynamic>?)
          ?.map((e) => Event.fromJson(e as Map<String, dynamic>))
          .toList()
      ..responseInfo = json['ResponseInfo'] == null
          ? null
          : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
      ..totalCount = (json['totalCount'] as num?)?.toInt();

Map<String, dynamic> _$NotificationListToJson(NotificationList instance) =>
    <String, dynamic>{
      'events': instance.events,
      'ResponseInfo': instance.responseInfo,
      'totalCount': instance.totalCount,
    };

Event _$EventFromJson(Map<String, dynamic> json) => Event()
  ..tenantId = json['tenantId'] as String?
  ..id = json['id'] as String?
  ..referenceId = json['referenceId']
  ..eventType = json['eventType'] as String?
  ..eventCategory = json['eventCategory'] as String?
  ..name = json['name'] as String?
  ..description = json['description'] as String?
  ..status = json['status'] as String?
  ..source = json['source'] as String?
  ..postedBy = json['postedBy'] as String?
  ..recepient = json['recepient'] == null
      ? null
      : Recepient.fromJson(json['recepient'] as Map<String, dynamic>)
  ..actions = json['actions'] == null
      ? null
      : Actions.fromJson(json['actions'] as Map<String, dynamic>)
  ..eventDetails = json['eventDetails'] == null
      ? null
      : EventDetails.fromJson(json['eventDetails'] as Map<String, dynamic>)
  ..auditDetails = json['auditDetails'] == null
      ? null
      : AuditDetails.fromJson(json['auditDetails'] as Map<String, dynamic>)
  ..recepientEventMap = json['recepientEventMap']
  ..generateCounterEvent = json['generateCounterEvent']
  ..internallyUpdted = json['internallyUpdted'] as bool?;

Map<String, dynamic> _$EventToJson(Event instance) => <String, dynamic>{
      'tenantId': instance.tenantId,
      'id': instance.id,
      'referenceId': instance.referenceId,
      'eventType': instance.eventType,
      'eventCategory': instance.eventCategory,
      'name': instance.name,
      'description': instance.description,
      'status': instance.status,
      'source': instance.source,
      'postedBy': instance.postedBy,
      'recepient': instance.recepient,
      'actions': instance.actions,
      'eventDetails': instance.eventDetails,
      'auditDetails': instance.auditDetails,
      'recepientEventMap': instance.recepientEventMap,
      'generateCounterEvent': instance.generateCounterEvent,
      'internallyUpdted': instance.internallyUpdted,
    };

Actions _$ActionsFromJson(Map<String, dynamic> json) => Actions()
  ..tenantId = json['tenantId'] as String?
  ..id = json['id'] as String?
  ..eventId = json['eventId'] as String?
  ..actionUrls = (json['actionUrls'] as List<dynamic>?)
      ?.map((e) => ActionUrl.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$ActionsToJson(Actions instance) => <String, dynamic>{
      'tenantId': instance.tenantId,
      'id': instance.id,
      'eventId': instance.eventId,
      'actionUrls': instance.actionUrls,
    };

ActionUrl _$ActionUrlFromJson(Map<String, dynamic> json) => ActionUrl()
  ..actionUrl = json['actionUrl'] as String?
  ..code = json['code'] as String?;

Map<String, dynamic> _$ActionUrlToJson(ActionUrl instance) => <String, dynamic>{
      'actionUrl': instance.actionUrl,
      'code': instance.code,
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

EventDetails _$EventDetailsFromJson(Map<String, dynamic> json) => EventDetails()
  ..id = json['id'] as String?
  ..eventId = json['eventId'] as String?
  ..organizer = json['organizer'] as String?
  ..fromDate = (json['fromDate'] as num?)?.toInt()
  ..toDate = (json['toDate'] as num?)?.toInt()
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..address = json['address'] as String?
  ..documents = json['documents']
  ..fees = json['fees'];

Map<String, dynamic> _$EventDetailsToJson(EventDetails instance) =>
    <String, dynamic>{
      'id': instance.id,
      'eventId': instance.eventId,
      'organizer': instance.organizer,
      'fromDate': instance.fromDate,
      'toDate': instance.toDate,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'address': instance.address,
      'documents': instance.documents,
      'fees': instance.fees,
    };

Recepient _$RecepientFromJson(Map<String, dynamic> json) => Recepient()
  ..toRoles = json['toRoles']
  ..toUsers =
      (json['toUsers'] as List<dynamic>?)?.map((e) => e as String).toList();

Map<String, dynamic> _$RecepientToJson(Recepient instance) => <String, dynamic>{
      'toRoles': instance.toRoles,
      'toUsers': instance.toUsers,
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
