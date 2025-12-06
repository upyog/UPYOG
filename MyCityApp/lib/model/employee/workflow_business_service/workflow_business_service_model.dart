import 'package:json_annotation/json_annotation.dart';

part 'workflow_business_service_model.g.dart';

@JsonSerializable()
class WorkflowBusinessServices {
  @JsonKey(name: 'BusinessServices')
  List<BusinessService>? businessServices;

  WorkflowBusinessServices();

  factory WorkflowBusinessServices.fromJson(Map<String, dynamic> json) =>
      _$WorkflowBusinessServicesFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowBusinessServicesToJson(this);
}

@JsonSerializable()
class BusinessService {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'uuid')
  String? uuid;
  @JsonKey(name: 'businessService')
  String? businessService;
  @JsonKey(name: 'business')
  String? business;
  @JsonKey(name: 'businessServiceSla')
  int? businessServiceSla;
  @JsonKey(name: 'states')
  List<State>? states;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;

  BusinessService();

  factory BusinessService.fromJson(Map<String, dynamic> json) =>
      _$BusinessServiceFromJson(json);

  Map<String, dynamic> toJson() => _$BusinessServiceToJson(this);
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
class State {
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
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
  bool? isStateUpdatable;
  @JsonKey(name: 'actions')
  List<Action>? actions;

  State();

  factory State.fromJson(Map<String, dynamic> json) => _$StateFromJson(json);

  Map<String, dynamic> toJson() => _$StateToJson(this);
}

@JsonSerializable()
class Action {
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
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
  bool? active;

  Action();

  factory Action.fromJson(Map<String, dynamic> json) => _$ActionFromJson(json);

  Map<String, dynamic> toJson() => _$ActionToJson(this);
}
