import 'package:json_annotation/json_annotation.dart';

part 'notification_model.g.dart';

@JsonSerializable()
class NotificationList{
  @JsonKey(name: 'events')
  List<Event>? events;
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'totalCount')
  int? totalCount;

  NotificationList();

  factory NotificationList.fromJson(Map<String, dynamic> json) =>  _$NotificationListFromJson(json);

  Map<String, dynamic> toJson() => _$NotificationListToJson(this);
}

@JsonSerializable()
class Event {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'referenceId')
  dynamic referenceId;
  @JsonKey(name: 'eventType')
  String? eventType;
  @JsonKey(name: 'eventCategory')
  String? eventCategory;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'description')
  String? description;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'postedBy')
  String? postedBy;
  @JsonKey(name: 'recepient')
  Recepient? recepient;
  @JsonKey(name: 'actions')
  Actions? actions;
  @JsonKey(name: 'eventDetails')
  EventDetails? eventDetails;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'recepientEventMap')
  dynamic recepientEventMap;
  @JsonKey(name: 'generateCounterEvent')
  dynamic generateCounterEvent;
  @JsonKey(name: 'internallyUpdted')
  bool? internallyUpdted;

  Event();

  factory Event.fromJson(Map<String, dynamic> json) =>  _$EventFromJson(json);

  Map<String, dynamic> toJson() => _$EventToJson(this);
}

@JsonSerializable()
class Actions {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'eventId')
  String? eventId;
  @JsonKey(name: 'actionUrls')
  List<ActionUrl>? actionUrls;

  Actions();

  factory Actions.fromJson(Map<String, dynamic> json) =>  _$ActionsFromJson(json);

  Map<String, dynamic> toJson() => _$ActionsToJson(this) ;
}

@JsonSerializable()
class ActionUrl {
  @JsonKey(name: 'actionUrl')
  String? actionUrl;
  @JsonKey(name: 'code')
  String? code;

  ActionUrl();

  factory ActionUrl.fromJson(Map<String, dynamic> json) => _$ActionUrlFromJson(json);

  Map<String, dynamic> toJson() => _$ActionUrlToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'createdTime')
  int? createdTime;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'lastModifiedTime')
  int? lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>  _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class EventDetails {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'eventId')
  String? eventId;
  @JsonKey(name: 'organizer')
  String? organizer;
  @JsonKey(name: 'fromDate')
  int? fromDate;
  @JsonKey(name: 'toDate')
  int? toDate;
  @JsonKey(name: 'latitude')
  dynamic latitude;
  @JsonKey(name: 'longitude')
  dynamic longitude;
  @JsonKey(name: 'address')
  String? address;
  @JsonKey(name: 'documents')
  dynamic documents;
  @JsonKey(name: 'fees')
  dynamic fees;

  EventDetails();

  factory EventDetails.fromJson(Map<String, dynamic> json) =>  _$EventDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$EventDetailsToJson(this);
}

@JsonSerializable()
class Recepient {
  @JsonKey(name: 'toRoles')
  dynamic toRoles;
  @JsonKey(name: 'toUsers')
  List<String>? toUsers;

  Recepient();

  factory Recepient.fromJson(Map<String, dynamic> json) =>  _$RecepientFromJson(json);

  Map<String, dynamic> toJson() => _$RecepientToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  dynamic ver;
  @JsonKey(name: 'ts')
  dynamic ts;
  @JsonKey(name: 'resMsgId')
  String? resMsgId;
  @JsonKey(name: 'msgId')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>  _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}
