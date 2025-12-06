import 'package:json_annotation/json_annotation.dart';
import 'package:mobile_app/model/citizen/token/token.dart';

part 'timeline.g.dart';

@JsonSerializable()
class Timeline {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'ProcessInstances')
  List<ProcessInstanceTimeline>? processInstancesList;
  @JsonKey(name: 'totalCount')
  int? totalCount;

  Timeline();

  factory Timeline.fromJson(Map<String, dynamic> json) =>
      _$TimelineFromJson(json);

  Map<String, dynamic> toJson() => _$TimelineToJson(this);
}

@JsonSerializable()
class ProcessInstanceTimeline {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'businessId')
  String? businessId;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'moduleName')
  String? moduleName;
  @JsonKey(name: 'state')
  State? state;
  @JsonKey(name: 'comment')
  String? comment;
  @JsonKey(name: 'documents')
  List<DocumentTimeLine>? documents;
  @JsonKey(name: 'assigner')
  Assigne? assigner;
  @JsonKey(name: 'assignes')
  List<Assigne>? assignes;
  @JsonKey(name: 'nextActions')
  List<Action>? nextActions;
  @JsonKey(name: 'stateSla')
  int? stateSla;
  @JsonKey(name: 'businesssServiceSla')
  int? businesssServiceSla;
  @JsonKey(name: 'previousStatus')
  dynamic previousStatus;
  @JsonKey(name: 'entity')
  dynamic entity;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'rating')
  int? rating;
  @JsonKey(name: 'escalated')
  bool? escalated;

  ProcessInstanceTimeline();

  factory ProcessInstanceTimeline.fromJson(Map<String, dynamic> json) =>
      _$ProcessInstanceTimelineFromJson(json);

  Map<String, dynamic> toJson() => _$ProcessInstanceTimelineToJson(this);
}

@JsonSerializable()
class Assigne {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'userName')
  String? userName;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'type')
  String? type;
  @JsonKey(name: 'mobileNumber')
  String? mobileNumber;
  @JsonKey(name: 'emailId')
  String? emailId;
  @JsonKey(name: 'roles')
  List<Roles>? roles;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'uuid')
  String? uuid;

  Assigne();

  factory Assigne.fromJson(Map<String, dynamic> json) =>
      _$AssigneFromJson(json);

  Map<String, dynamic> toJson() => _$AssigneToJson(this);
}

@JsonSerializable()
class Roles {
  @JsonKey(name: 'id')
  int? id;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  Roles();

  factory Roles.fromJson(Map<String, dynamic> json) => _$RolesFromJson(json);

  Map<String, dynamic> toJson() => _$RolesToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'createdTime')
  int? createdTime;
  @JsonKey(name: 'lastModifiedTime')
  int? lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) =>
      _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class DocumentTimeLine {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentUid')
  String? documentUid;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  DocumentTimeLine();

  factory DocumentTimeLine.fromJson(Map<String, dynamic> json) =>
      _$DocumentTimeLineFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentTimeLineToJson(this);
}

@JsonSerializable()
class Action {
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'currentState')
  String? currentState;
  @JsonKey(name: 'action')
  String? action;
  @JsonKey(name: 'nextState')
  String? nextState;
  @JsonKey(name: 'roles')
  List<String>? roles;
  @JsonKey(name: 'active')
  dynamic active;

  Action();

  factory Action.fromJson(Map<String, dynamic> json) => _$ActionFromJson(json);

  Map<String, dynamic> toJson() => _$ActionToJson(this);
}

@JsonSerializable()
class State {
  @JsonKey(name: 'auditDetails')
  dynamic auditDetails;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'businessServiceId')
  String? businessServiceId;
  @JsonKey(name: 'sla')
  int? sla;
  @JsonKey(name: 'state')
  String? state;
  @JsonKey(name: 'applicationStatus')
  String? applicationStatus;
  @JsonKey(name: 'docUploadRequired')
  bool? docUploadRequired;
  @JsonKey(name: 'isStartState')
  bool? isStartState;
  @JsonKey(name: 'isTerminateState')
  bool? isTerminateState;
  @JsonKey(name: 'isStateUpdatable')
  dynamic isStateUpdatable;
  @JsonKey(name: 'actions')
  List<Action>? actions;

  State();

  factory State.fromJson(Map<String, dynamic> json) => _$StateFromJson(json);

  Map<String, dynamic> toJson() => _$StateToJson(this);
}
