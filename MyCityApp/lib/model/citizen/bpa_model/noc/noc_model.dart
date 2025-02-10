
import 'package:json_annotation/json_annotation.dart';

part 'noc_model.g.dart';

@JsonSerializable()
class Noc {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'Noc')
  List<NocElement>? noc;
  @JsonKey(name: 'count')
  int? count;

  Noc();

  factory Noc.fromJson(Map<String, dynamic> json) => _$NocFromJson(json);

  Map<String, dynamic> toJson() => _$NocToJson(this);
}

@JsonSerializable()
class NocElement {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'applicationNo')
  String? applicationNo;
  @JsonKey(name: 'nocNo')
  dynamic nocNo;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'nocType')
  String? nocType;
  @JsonKey(name: 'accountId')
  String? accountId;
  @JsonKey(name: 'source')
  String? source;
  @JsonKey(name: 'sourceRefId')
  String? sourceRefId;
  @JsonKey(name: 'landId')
  dynamic landId;
  @JsonKey(name: 'status')
  dynamic status;
  @JsonKey(name: 'applicationStatus')
  String? applicationStatus;
  @JsonKey(name: 'documents')
  List<Document>? documents;
  @JsonKey(name: 'workflow')
  dynamic workflow;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;

  NocElement();

  factory NocElement.fromJson(Map<String, dynamic> json) => _$NocElementFromJson(json);

  Map<String, dynamic> toJson() => _$NocElementToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'mode')
  String? mode;
  @JsonKey(name: 'SubmittedOn')
  String? submittedOn;
  @JsonKey(name: 'currentOwner')
  dynamic currentOwner;
  @JsonKey(name: 'workflowCode')
  String? workflowCode;
  @JsonKey(name: 'applicantName')
  String? applicantName;

  AdditionalDetails();

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) => _$AdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsToJson(this);
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

  factory AuditDetails.fromJson(Map<String, dynamic> json) => _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class Document {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;
  @JsonKey(name: 'documentUid')
  dynamic documentUid;
  @JsonKey(name: 'additionalDetails')
  dynamic additionalDetails;

  Document();

  factory Document.fromJson(Map<String, dynamic> json) => _$DocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentToJson(this);
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

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>_$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}
