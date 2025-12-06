import 'package:json_annotation/json_annotation.dart';

part 'emp_mdms_model.g.dart';

@JsonSerializable()
class EmpMdmsResModel {
  @JsonKey(name: 'MdmsRes')
  MdmsResEmp? mdmsResEmp;

  EmpMdmsResModel();

  factory EmpMdmsResModel.fromJson(Map<String, dynamic> json) =>
      _$EmpMdmsResModelFromJson(json);

  Map<String, dynamic> toJson() => _$EmpMdmsResModelToJson(this);
}

@JsonSerializable()
class MdmsResEmp {
  @JsonKey(name: 'BPA', includeIfNull: false)
  BpaObps? bpaObps;
  @JsonKey(name: 'NOC', includeIfNull: false)
  NocObps? nocObps;
  @JsonKey(name: 'common-masters', includeIfNull: false)
  CommonMastersObps? commonMastersObps;
  @JsonKey(name: 'ws-services-masters', includeIfNull: false)
  WsServicesMasters? wsServicesMasters;
  @JsonKey(name: 'ws-services-calculation', includeIfNull: false)
  WsServicesCalculation? wsServicesCalculation;

  MdmsResEmp();

  factory MdmsResEmp.fromJson(Map<String, dynamic> json) =>
      _$MdmsResEmpFromJson(json);

  Map<String, dynamic> toJson() => _$MdmsResEmpToJson(this);
}

@JsonSerializable()
class BpaObps {
  @JsonKey(name: 'CheckList')
  List<CheckList>? checkList;

  BpaObps();

  factory BpaObps.fromJson(Map<String, dynamic> json) =>
      _$BpaObpsFromJson(json);

  Map<String, dynamic> toJson() => _$BpaObpsToJson(this);
}

@JsonSerializable()
class CheckList {
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'ServiceType')
  String? serviceType;
  @JsonKey(name: 'RiskType')
  String? riskType;
  @JsonKey(name: 'WFState')
  String? wfState;
  @JsonKey(name: 'questions')
  List<Question>? questions;
  @JsonKey(name: 'docTypes')
  List<CheckListDocType>? docTypes;
  @JsonKey(name: 'conditions')
  List<String>? conditions;

  CheckList();

  factory CheckList.fromJson(Map<String, dynamic> json) =>
      _$CheckListFromJson(json);

  Map<String, dynamic> toJson() => _$CheckListToJson(this);
}

@JsonSerializable()
class CheckListDocType {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'required')
  bool? required;

  CheckListDocType();

  factory CheckListDocType.fromJson(Map<String, dynamic> json) =>
      _$CheckListDocTypeFromJson(json);

  Map<String, dynamic> toJson() => _$CheckListDocTypeToJson(this);
}

@JsonSerializable()
class Question {
  @JsonKey(name: 'question')
  String? question;
  @JsonKey(name: 'fieldType')
  String? fieldType;
  @JsonKey(name: 'active')
  bool? active;

  Question();

  factory Question.fromJson(Map<String, dynamic> json) =>
      _$QuestionFromJson(json);

  Map<String, dynamic> toJson() => _$QuestionToJson(this);
}

@JsonSerializable()
class CommonMastersObps {
  @JsonKey(name: 'DocumentType')
  List<DocumentType>? documentType;

  CommonMastersObps();

  factory CommonMastersObps.fromJson(Map<String, dynamic> json) =>
      _$CommonMastersObpsFromJson(json);

  Map<String, dynamic> toJson() => _$CommonMastersObpsToJson(this);
}

@JsonSerializable()
class DocumentType {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'allowedFormat')
  List<String>? allowedFormat;
  @JsonKey(name: 'maxFileSize')
  int? maxFileSize;

  DocumentType();

  factory DocumentType.fromJson(Map<String, dynamic> json) =>
      _$DocumentTypeFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentTypeToJson(this);
}

@JsonSerializable()
class NocObps {
  @JsonKey(name: 'DocumentTypeMapping')
  List<DocumentTypeMapping>? documentTypeMapping;

  NocObps();

  factory NocObps.fromJson(Map<String, dynamic> json) =>
      _$NocObpsFromJson(json);

  Map<String, dynamic> toJson() => _$NocObpsToJson(this);
}

@JsonSerializable()
class DocumentTypeMapping {
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'nocType')
  String? nocType;
  @JsonKey(name: 'docTypes')
  List<DocumentTypeMappingDocType>? docTypes;

  DocumentTypeMapping();

  factory DocumentTypeMapping.fromJson(Map<String, dynamic> json) =>
      _$DocumentTypeMappingFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentTypeMappingToJson(this);
}

@JsonSerializable()
class DocumentTypeMappingDocType {
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'required')
  bool? required;

  DocumentTypeMappingDocType();

  factory DocumentTypeMappingDocType.fromJson(Map<String, dynamic> json) =>
      _$DocumentTypeMappingDocTypeFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentTypeMappingDocTypeToJson(this);
}

@JsonSerializable()
class WsServicesCalculation {
  @JsonKey(name: 'RoadType')
  List<RoadType>? roadType;
  @JsonKey(name: 'PipeSize')
  List<PipeSize>? pipeSize;

  WsServicesCalculation();

  factory WsServicesCalculation.fromJson(Map<String, dynamic> json) =>
      _$WsServicesCalculationFromJson(json);

  Map<String, dynamic> toJson() => _$WsServicesCalculationToJson(this);
}

@JsonSerializable()
class PipeSize {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'size')
  double? size;
  @JsonKey(name: 'isActive')
  bool? isActive;

  PipeSize();

  factory PipeSize.fromJson(Map<String, dynamic> json) =>
      _$PipeSizeFromJson(json);

  Map<String, dynamic> toJson() => _$PipeSizeToJson(this);
}

@JsonSerializable()
class RoadType {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'unitCost')
  int? unitCost;
  @JsonKey(name: 'isActive')
  bool? isActive;

  RoadType();

  factory RoadType.fromJson(Map<String, dynamic> json) =>
      _$RoadTypeFromJson(json);

  Map<String, dynamic> toJson() => _$RoadTypeToJson(this);
}

@JsonSerializable()
class WsServicesMasters {
  @JsonKey(name: 'waterSource')
  List<ConnectionType>? waterSource;
  @JsonKey(name: 'connectionType')
  List<ConnectionType>? connectionType;

  WsServicesMasters();

  factory WsServicesMasters.fromJson(Map<String, dynamic> json) =>
      _$WsServicesMastersFromJson(json);

  Map<String, dynamic> toJson() => _$WsServicesMastersToJson(this);
}

@JsonSerializable()
class ConnectionType {
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;

  ConnectionType();

  factory ConnectionType.fromJson(Map<String, dynamic> json) =>
      _$ConnectionTypeFromJson(json);

  Map<String, dynamic> toJson() => _$ConnectionTypeToJson(this);
}
