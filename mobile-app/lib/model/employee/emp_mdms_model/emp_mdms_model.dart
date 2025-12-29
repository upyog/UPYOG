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
  @JsonKey(name: 'RAINMAKER-PGR', includeIfNull: false)
  RainmakerPgr? rainmakerPgr;
  @JsonKey(name: 'PropertyTax', includeIfNull: false)
  PropertyTax? propertyTax;

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
  @JsonKey(name: 'DocumentType',includeIfNull: false)
  List<DocumentType>? documentType;
  @JsonKey(name: 'GenderType' ,includeIfNull: false)
  List<GenderType>? genderType;

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
class GenderType {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;

  GenderType();

  factory GenderType.fromJson(Map<String, dynamic> json) =>
      _$GenderTypeFromJson(json);

  Map<String, dynamic> toJson() => _$GenderTypeToJson(this);
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

@JsonSerializable()
class RainmakerPgr {
  @JsonKey(name: 'ServiceDefs')
  List<ServiceDef>? serviceDefs;

  RainmakerPgr();

  factory RainmakerPgr.fromJson(Map<String, dynamic> json) =>
      _$RainmakerPgrFromJson(json);

  Map<String, dynamic> toJson() => _$RainmakerPgrToJson(this);
}

@JsonSerializable()
class ServiceDef {
  @JsonKey(name: 'serviceCode')
  String? serviceCode;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'keywords')
  String? keywords;
  @JsonKey(name: 'department')
  String? department;
  @JsonKey(name: 'slaHours')
  int? slaHours;
  @JsonKey(name: 'menuPath')
  String? menuPath;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'order')
  int? order;

  ServiceDef();

  factory ServiceDef.fromJson(Map<String, dynamic> json) =>
      _$ServiceDefFromJson(json);

  Map<String, dynamic> toJson() => _$ServiceDefToJson(this);
}

@JsonSerializable()
class PropertyTax {
  @JsonKey(name: 'UsageCategory')
  List<OwnerType>? usageCategory;
  @JsonKey(name: 'Floor')
  List<Floor>? floor;
  @JsonKey(name: 'OwnerType')
  List<OwnerType>? ownerType;
  @JsonKey(name: 'OccupancyType')
  List<Floor>? occupancyType;
  @JsonKey(name: 'OwnerShipCategory')
  List<OwnerShipCategory>? ownerShipCategory;
  @JsonKey(name: 'MutationDocuments')
  List<MutationDocument>? mutationDocuments;
  @JsonKey(name: 'SubOwnerShipCategory')
  List<Floor>? subOwnerShipCategory;
  @JsonKey(name: 'Documents')
  List<Document>? documents;
  @JsonKey(name: 'PropertyType')
  List<PropertyType>? propertyType;

  PropertyTax();

  factory PropertyTax.fromJson(Map<String, dynamic> json) =>
      _$PropertyTaxFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyTaxToJson(this);
}

@JsonSerializable()
class Document {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'required')
  bool? required;
  @JsonKey(name: 'description')
  String? description;
  @JsonKey(name: 'hasDropdown')
  bool? hasDropdown;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'dropdownData')
  List<DocumentDropdownDatum>? dropdownData;
  @JsonKey(name: 'digit-citizen')
  bool? digitCitizen;
  @JsonKey(name: 'additionalDetails')
  AdditionalDetails? additionalDetails;

  Document();

  factory Document.fromJson(Map<String, dynamic> json) =>
      _$DocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentToJson(this);
}

@JsonSerializable()
class AdditionalDetails {
  @JsonKey(name: 'dropdownFilter')
  DropdownFilter? dropdownFilter;
  @JsonKey(name: 'enabledActions')
  EnabledActions? enabledActions;

  AdditionalDetails();

  factory AdditionalDetails.fromJson(Map<String, dynamic> json) =>
      _$AdditionalDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AdditionalDetailsToJson(this);
}

@JsonSerializable()
class DropdownFilter {
  @JsonKey(name: 'formDataPath')
  List<String>? formDataPath;
  @JsonKey(name: 'parentJsonpath')
  String? parentJsonpath;

  DropdownFilter();

  factory DropdownFilter.fromJson(Map<String, dynamic> json) =>
      _$DropdownFilterFromJson(json);

  Map<String, dynamic> toJson() => _$DropdownFilterToJson(this);
}

@JsonSerializable()
class EnabledActions {
  @JsonKey(name: 'assess')
  Assess? assess;
  @JsonKey(name: 'create')
  Assess? create;
  @JsonKey(name: 'update')
  Assess? update;
  @JsonKey(name: 'reassess')
  Assess? reassess;

  EnabledActions();

  factory EnabledActions.fromJson(Map<String, dynamic> json) =>
      _$EnabledActionsFromJson(json);

  Map<String, dynamic> toJson() => _$EnabledActionsToJson(this);
}

@JsonSerializable()
class Assess {
  @JsonKey(name: 'disableUpload')
  bool? disableUpload;
  @JsonKey(name: 'disableDropdown')
  bool? disableDropdown;

  Assess();

  factory Assess.fromJson(Map<String, dynamic> json) => _$AssessFromJson(json);

  Map<String, dynamic> toJson() => _$AssessToJson(this);
}

@JsonSerializable()
class DocumentDropdownDatum {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'parentValue', includeIfNull: false)
  List<dynamic>? parentValue;

  DocumentDropdownDatum();

  factory DocumentDropdownDatum.fromJson(Map<String, dynamic> json) =>
      _$DocumentDropdownDatumFromJson(json);

  Map<String, dynamic> toJson() => _$DocumentDropdownDatumToJson(this);
}

@JsonSerializable()
class Floor {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'description')
  String? description;
  @JsonKey(name: 'propertyType')
  String? propertyType;
  @JsonKey(name: 'ownerShipCategory')
  String? ownerShipCategory;

  Floor();

  factory Floor.fromJson(Map<String, dynamic> json) => _$FloorFromJson(json);

  Map<String, dynamic> toJson() => _$FloorToJson(this);
}

@JsonSerializable()
class PropertyType {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'propertyType')
  String? propertyType;

  PropertyType();

  factory PropertyType.fromJson(Map<String, dynamic> json) => _$PropertyTypeFromJson(json);

  Map<String, dynamic> toJson() => _$PropertyTypeToJson(this);
}

@JsonSerializable()
class MutationDocument {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;
  @JsonKey(name: 'required')
  bool? required;
  @JsonKey(name: 'description')
  String? description;
  @JsonKey(name: 'hasDropdown')
  bool? hasDropdown;
  @JsonKey(name: 'documentType')
  String? documentType;
  @JsonKey(name: 'dropdownData')
  List<MutationDocumentDropdownDatum>? dropdownData;

  MutationDocument();

  factory MutationDocument.fromJson(Map<String, dynamic> json) =>
      _$MutationDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$MutationDocumentToJson(this);
}

@JsonSerializable()
class MutationDocumentDropdownDatum {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'active')
  bool? active;

  MutationDocumentDropdownDatum();

  factory MutationDocumentDropdownDatum.fromJson(Map<String, dynamic> json) =>
      _$MutationDocumentDropdownDatumFromJson(json);

  Map<String, dynamic> toJson() => _$MutationDocumentDropdownDatumToJson(this);
}

@JsonSerializable()
class OwnerType {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'active')
  dynamic active;
  @JsonKey(name: 'fromFY')
  String? fromFy;
  @JsonKey(name: 'exemption')
  Exemption? exemption;

  OwnerType();

  factory OwnerType.fromJson(Map<String, dynamic> json) =>
      _$OwnerTypeFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerTypeToJson(this);
}

@JsonSerializable()
class Exemption {
  @JsonKey(name: 'rate')
  int? rate;
  @JsonKey(name: 'maxAmount')
  int? maxAmount;
  @JsonKey(name: 'flatAmount')
  int? flatAmount;

  Exemption();

  factory Exemption.fromJson(Map<String, dynamic> json) =>
      _$ExemptionFromJson(json);

  Map<String, dynamic> toJson() => _$ExemptionToJson(this);
}

@JsonSerializable()
class OwnerShipCategory {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'active')
  bool? active;


  OwnerShipCategory();

  factory OwnerShipCategory.fromJson(Map<String, dynamic> json) =>
      _$OwnerShipCategoryFromJson(json);

  Map<String, dynamic> toJson() => _$OwnerShipCategoryToJson(this);
}
