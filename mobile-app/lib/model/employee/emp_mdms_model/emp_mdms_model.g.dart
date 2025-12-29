// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'emp_mdms_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EmpMdmsResModel _$EmpMdmsResModelFromJson(Map<String, dynamic> json) =>
    EmpMdmsResModel()
      ..mdmsResEmp = json['MdmsRes'] == null
          ? null
          : MdmsResEmp.fromJson(json['MdmsRes'] as Map<String, dynamic>);

Map<String, dynamic> _$EmpMdmsResModelToJson(EmpMdmsResModel instance) =>
    <String, dynamic>{
      'MdmsRes': instance.mdmsResEmp,
    };

MdmsResEmp _$MdmsResEmpFromJson(Map<String, dynamic> json) => MdmsResEmp()
  ..bpaObps = json['BPA'] == null
      ? null
      : BpaObps.fromJson(json['BPA'] as Map<String, dynamic>)
  ..nocObps = json['NOC'] == null
      ? null
      : NocObps.fromJson(json['NOC'] as Map<String, dynamic>)
  ..commonMastersObps = json['common-masters'] == null
      ? null
      : CommonMastersObps.fromJson(
          json['common-masters'] as Map<String, dynamic>)
  ..wsServicesMasters = json['ws-services-masters'] == null
      ? null
      : WsServicesMasters.fromJson(
          json['ws-services-masters'] as Map<String, dynamic>)
  ..wsServicesCalculation = json['ws-services-calculation'] == null
      ? null
      : WsServicesCalculation.fromJson(
          json['ws-services-calculation'] as Map<String, dynamic>)
  ..rainmakerPgr = json['RAINMAKER-PGR'] == null
      ? null
      : RainmakerPgr.fromJson(json['RAINMAKER-PGR'] as Map<String, dynamic>)
  ..propertyTax = json['PropertyTax'] == null
      ? null
      : PropertyTax.fromJson(json['PropertyTax'] as Map<String, dynamic>);

Map<String, dynamic> _$MdmsResEmpToJson(MdmsResEmp instance) =>
    <String, dynamic>{
      if (instance.bpaObps case final value?) 'BPA': value,
      if (instance.nocObps case final value?) 'NOC': value,
      if (instance.commonMastersObps case final value?) 'common-masters': value,
      if (instance.wsServicesMasters case final value?)
        'ws-services-masters': value,
      if (instance.wsServicesCalculation case final value?)
        'ws-services-calculation': value,
      if (instance.rainmakerPgr case final value?) 'RAINMAKER-PGR': value,
      if (instance.propertyTax case final value?) 'PropertyTax': value,
    };

BpaObps _$BpaObpsFromJson(Map<String, dynamic> json) => BpaObps()
  ..checkList = (json['CheckList'] as List<dynamic>?)
      ?.map((e) => CheckList.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$BpaObpsToJson(BpaObps instance) => <String, dynamic>{
      'CheckList': instance.checkList,
    };

CheckList _$CheckListFromJson(Map<String, dynamic> json) => CheckList()
  ..applicationType = json['applicationType'] as String?
  ..serviceType = json['ServiceType'] as String?
  ..riskType = json['RiskType'] as String?
  ..wfState = json['WFState'] as String?
  ..questions = (json['questions'] as List<dynamic>?)
      ?.map((e) => Question.fromJson(e as Map<String, dynamic>))
      .toList()
  ..docTypes = (json['docTypes'] as List<dynamic>?)
      ?.map((e) => CheckListDocType.fromJson(e as Map<String, dynamic>))
      .toList()
  ..conditions =
      (json['conditions'] as List<dynamic>?)?.map((e) => e as String).toList();

Map<String, dynamic> _$CheckListToJson(CheckList instance) => <String, dynamic>{
      'applicationType': instance.applicationType,
      'ServiceType': instance.serviceType,
      'RiskType': instance.riskType,
      'WFState': instance.wfState,
      'questions': instance.questions,
      'docTypes': instance.docTypes,
      'conditions': instance.conditions,
    };

CheckListDocType _$CheckListDocTypeFromJson(Map<String, dynamic> json) =>
    CheckListDocType()
      ..code = json['code'] as String?
      ..required = json['required'] as bool?;

Map<String, dynamic> _$CheckListDocTypeToJson(CheckListDocType instance) =>
    <String, dynamic>{
      'code': instance.code,
      'required': instance.required,
    };

Question _$QuestionFromJson(Map<String, dynamic> json) => Question()
  ..question = json['question'] as String?
  ..fieldType = json['fieldType'] as String?
  ..active = json['active'] as bool?;

Map<String, dynamic> _$QuestionToJson(Question instance) => <String, dynamic>{
      'question': instance.question,
      'fieldType': instance.fieldType,
      'active': instance.active,
    };

CommonMastersObps _$CommonMastersObpsFromJson(Map<String, dynamic> json) =>
    CommonMastersObps()
      ..documentType = (json['DocumentType'] as List<dynamic>?)
          ?.map((e) => DocumentType.fromJson(e as Map<String, dynamic>))
          .toList()
      ..genderType = (json['GenderType'] as List<dynamic>?)
          ?.map((e) => GenderType.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$CommonMastersObpsToJson(CommonMastersObps instance) =>
    <String, dynamic>{
      if (instance.documentType case final value?) 'DocumentType': value,
      if (instance.genderType case final value?) 'GenderType': value,
    };

DocumentType _$DocumentTypeFromJson(Map<String, dynamic> json) => DocumentType()
  ..code = json['code'] as String?
  ..active = json['active'] as bool?
  ..allowedFormat = (json['allowedFormat'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList()
  ..maxFileSize = (json['maxFileSize'] as num?)?.toInt();

Map<String, dynamic> _$DocumentTypeToJson(DocumentType instance) =>
    <String, dynamic>{
      'code': instance.code,
      'active': instance.active,
      'allowedFormat': instance.allowedFormat,
      'maxFileSize': instance.maxFileSize,
    };

GenderType _$GenderTypeFromJson(Map<String, dynamic> json) => GenderType()
  ..code = json['code'] as String?
  ..active = json['active'] as bool?;

Map<String, dynamic> _$GenderTypeToJson(GenderType instance) =>
    <String, dynamic>{
      'code': instance.code,
      'active': instance.active,
    };

NocObps _$NocObpsFromJson(Map<String, dynamic> json) => NocObps()
  ..documentTypeMapping = (json['DocumentTypeMapping'] as List<dynamic>?)
      ?.map((e) => DocumentTypeMapping.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$NocObpsToJson(NocObps instance) => <String, dynamic>{
      'DocumentTypeMapping': instance.documentTypeMapping,
    };

DocumentTypeMapping _$DocumentTypeMappingFromJson(Map<String, dynamic> json) =>
    DocumentTypeMapping()
      ..applicationType = json['applicationType'] as String?
      ..nocType = json['nocType'] as String?
      ..docTypes = (json['docTypes'] as List<dynamic>?)
          ?.map((e) =>
              DocumentTypeMappingDocType.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$DocumentTypeMappingToJson(
        DocumentTypeMapping instance) =>
    <String, dynamic>{
      'applicationType': instance.applicationType,
      'nocType': instance.nocType,
      'docTypes': instance.docTypes,
    };

DocumentTypeMappingDocType _$DocumentTypeMappingDocTypeFromJson(
        Map<String, dynamic> json) =>
    DocumentTypeMappingDocType()
      ..documentType = json['documentType'] as String?
      ..required = json['required'] as bool?;

Map<String, dynamic> _$DocumentTypeMappingDocTypeToJson(
        DocumentTypeMappingDocType instance) =>
    <String, dynamic>{
      'documentType': instance.documentType,
      'required': instance.required,
    };

WsServicesCalculation _$WsServicesCalculationFromJson(
        Map<String, dynamic> json) =>
    WsServicesCalculation()
      ..roadType = (json['RoadType'] as List<dynamic>?)
          ?.map((e) => RoadType.fromJson(e as Map<String, dynamic>))
          .toList()
      ..pipeSize = (json['PipeSize'] as List<dynamic>?)
          ?.map((e) => PipeSize.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$WsServicesCalculationToJson(
        WsServicesCalculation instance) =>
    <String, dynamic>{
      'RoadType': instance.roadType,
      'PipeSize': instance.pipeSize,
    };

PipeSize _$PipeSizeFromJson(Map<String, dynamic> json) => PipeSize()
  ..id = json['id'] as String?
  ..size = (json['size'] as num?)?.toDouble()
  ..isActive = json['isActive'] as bool?;

Map<String, dynamic> _$PipeSizeToJson(PipeSize instance) => <String, dynamic>{
      'id': instance.id,
      'size': instance.size,
      'isActive': instance.isActive,
    };

RoadType _$RoadTypeFromJson(Map<String, dynamic> json) => RoadType()
  ..name = json['name'] as String?
  ..code = json['code'] as String?
  ..unitCost = (json['unitCost'] as num?)?.toInt()
  ..isActive = json['isActive'] as bool?;

Map<String, dynamic> _$RoadTypeToJson(RoadType instance) => <String, dynamic>{
      'name': instance.name,
      'code': instance.code,
      'unitCost': instance.unitCost,
      'isActive': instance.isActive,
    };

WsServicesMasters _$WsServicesMastersFromJson(Map<String, dynamic> json) =>
    WsServicesMasters()
      ..waterSource = (json['waterSource'] as List<dynamic>?)
          ?.map((e) => ConnectionType.fromJson(e as Map<String, dynamic>))
          .toList()
      ..connectionType = (json['connectionType'] as List<dynamic>?)
          ?.map((e) => ConnectionType.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$WsServicesMastersToJson(WsServicesMasters instance) =>
    <String, dynamic>{
      'waterSource': instance.waterSource,
      'connectionType': instance.connectionType,
    };

ConnectionType _$ConnectionTypeFromJson(Map<String, dynamic> json) =>
    ConnectionType()
      ..name = json['name'] as String?
      ..code = json['code'] as String?
      ..active = json['active'] as bool?;

Map<String, dynamic> _$ConnectionTypeToJson(ConnectionType instance) =>
    <String, dynamic>{
      'name': instance.name,
      'code': instance.code,
      'active': instance.active,
    };

RainmakerPgr _$RainmakerPgrFromJson(Map<String, dynamic> json) => RainmakerPgr()
  ..serviceDefs = (json['ServiceDefs'] as List<dynamic>?)
      ?.map((e) => ServiceDef.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$RainmakerPgrToJson(RainmakerPgr instance) =>
    <String, dynamic>{
      'ServiceDefs': instance.serviceDefs,
    };

ServiceDef _$ServiceDefFromJson(Map<String, dynamic> json) => ServiceDef()
  ..serviceCode = json['serviceCode'] as String?
  ..name = json['name'] as String?
  ..keywords = json['keywords'] as String?
  ..department = json['department'] as String?
  ..slaHours = (json['slaHours'] as num?)?.toInt()
  ..menuPath = json['menuPath'] as String?
  ..active = json['active'] as bool?
  ..order = (json['order'] as num?)?.toInt();

Map<String, dynamic> _$ServiceDefToJson(ServiceDef instance) =>
    <String, dynamic>{
      'serviceCode': instance.serviceCode,
      'name': instance.name,
      'keywords': instance.keywords,
      'department': instance.department,
      'slaHours': instance.slaHours,
      'menuPath': instance.menuPath,
      'active': instance.active,
      'order': instance.order,
    };

PropertyTax _$PropertyTaxFromJson(Map<String, dynamic> json) => PropertyTax()
  ..usageCategory = (json['UsageCategory'] as List<dynamic>?)
      ?.map((e) => OwnerType.fromJson(e as Map<String, dynamic>))
      .toList()
  ..floor = (json['Floor'] as List<dynamic>?)
      ?.map((e) => Floor.fromJson(e as Map<String, dynamic>))
      .toList()
  ..ownerType = (json['OwnerType'] as List<dynamic>?)
      ?.map((e) => OwnerType.fromJson(e as Map<String, dynamic>))
      .toList()
  ..occupancyType = (json['OccupancyType'] as List<dynamic>?)
      ?.map((e) => Floor.fromJson(e as Map<String, dynamic>))
      .toList()
  ..ownerShipCategory = (json['OwnerShipCategory'] as List<dynamic>?)
      ?.map((e) => OwnerShipCategory.fromJson(e as Map<String, dynamic>))
      .toList()
  ..mutationDocuments = (json['MutationDocuments'] as List<dynamic>?)
      ?.map((e) => MutationDocument.fromJson(e as Map<String, dynamic>))
      .toList()
  ..subOwnerShipCategory = (json['SubOwnerShipCategory'] as List<dynamic>?)
      ?.map((e) => Floor.fromJson(e as Map<String, dynamic>))
      .toList()
  ..documents = (json['Documents'] as List<dynamic>?)
      ?.map((e) => Document.fromJson(e as Map<String, dynamic>))
      .toList()
  ..propertyType = (json['PropertyType'] as List<dynamic>?)
      ?.map((e) => PropertyType.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$PropertyTaxToJson(PropertyTax instance) =>
    <String, dynamic>{
      'UsageCategory': instance.usageCategory,
      'Floor': instance.floor,
      'OwnerType': instance.ownerType,
      'OccupancyType': instance.occupancyType,
      'OwnerShipCategory': instance.ownerShipCategory,
      'MutationDocuments': instance.mutationDocuments,
      'SubOwnerShipCategory': instance.subOwnerShipCategory,
      'Documents': instance.documents,
      'PropertyType': instance.propertyType,
    };

Document _$DocumentFromJson(Map<String, dynamic> json) => Document()
  ..code = json['code'] as String?
  ..active = json['active'] as bool?
  ..required = json['required'] as bool?
  ..description = json['description'] as String?
  ..hasDropdown = json['hasDropdown'] as bool?
  ..documentType = json['documentType'] as String?
  ..dropdownData = (json['dropdownData'] as List<dynamic>?)
      ?.map((e) => DocumentDropdownDatum.fromJson(e as Map<String, dynamic>))
      .toList()
  ..digitCitizen = json['digit-citizen'] as bool?
  ..additionalDetails = json['additionalDetails'] == null
      ? null
      : AdditionalDetails.fromJson(
          json['additionalDetails'] as Map<String, dynamic>);

Map<String, dynamic> _$DocumentToJson(Document instance) => <String, dynamic>{
      'code': instance.code,
      'active': instance.active,
      'required': instance.required,
      'description': instance.description,
      'hasDropdown': instance.hasDropdown,
      'documentType': instance.documentType,
      'dropdownData': instance.dropdownData,
      'digit-citizen': instance.digitCitizen,
      'additionalDetails': instance.additionalDetails,
    };

AdditionalDetails _$AdditionalDetailsFromJson(Map<String, dynamic> json) =>
    AdditionalDetails()
      ..dropdownFilter = json['dropdownFilter'] == null
          ? null
          : DropdownFilter.fromJson(
              json['dropdownFilter'] as Map<String, dynamic>)
      ..enabledActions = json['enabledActions'] == null
          ? null
          : EnabledActions.fromJson(
              json['enabledActions'] as Map<String, dynamic>);

Map<String, dynamic> _$AdditionalDetailsToJson(AdditionalDetails instance) =>
    <String, dynamic>{
      'dropdownFilter': instance.dropdownFilter,
      'enabledActions': instance.enabledActions,
    };

DropdownFilter _$DropdownFilterFromJson(Map<String, dynamic> json) =>
    DropdownFilter()
      ..formDataPath = (json['formDataPath'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList()
      ..parentJsonpath = json['parentJsonpath'] as String?;

Map<String, dynamic> _$DropdownFilterToJson(DropdownFilter instance) =>
    <String, dynamic>{
      'formDataPath': instance.formDataPath,
      'parentJsonpath': instance.parentJsonpath,
    };

EnabledActions _$EnabledActionsFromJson(Map<String, dynamic> json) =>
    EnabledActions()
      ..assess = json['assess'] == null
          ? null
          : Assess.fromJson(json['assess'] as Map<String, dynamic>)
      ..create = json['create'] == null
          ? null
          : Assess.fromJson(json['create'] as Map<String, dynamic>)
      ..update = json['update'] == null
          ? null
          : Assess.fromJson(json['update'] as Map<String, dynamic>)
      ..reassess = json['reassess'] == null
          ? null
          : Assess.fromJson(json['reassess'] as Map<String, dynamic>);

Map<String, dynamic> _$EnabledActionsToJson(EnabledActions instance) =>
    <String, dynamic>{
      'assess': instance.assess,
      'create': instance.create,
      'update': instance.update,
      'reassess': instance.reassess,
    };

Assess _$AssessFromJson(Map<String, dynamic> json) => Assess()
  ..disableUpload = json['disableUpload'] as bool?
  ..disableDropdown = json['disableDropdown'] as bool?;

Map<String, dynamic> _$AssessToJson(Assess instance) => <String, dynamic>{
      'disableUpload': instance.disableUpload,
      'disableDropdown': instance.disableDropdown,
    };

DocumentDropdownDatum _$DocumentDropdownDatumFromJson(
        Map<String, dynamic> json) =>
    DocumentDropdownDatum()
      ..code = json['code'] as String?
      ..active = json['active'] as bool?
      ..parentValue = json['parentValue'] as List<dynamic>?;

Map<String, dynamic> _$DocumentDropdownDatumToJson(
        DocumentDropdownDatum instance) =>
    <String, dynamic>{
      'code': instance.code,
      'active': instance.active,
      if (instance.parentValue case final value?) 'parentValue': value,
    };

Floor _$FloorFromJson(Map<String, dynamic> json) => Floor()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..active = json['active'] as bool?
  ..description = json['description'] as String?
  ..propertyType = json['propertyType'] as String?
  ..ownerShipCategory = json['ownerShipCategory'] as String?;

Map<String, dynamic> _$FloorToJson(Floor instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'active': instance.active,
      'description': instance.description,
      'propertyType': instance.propertyType,
      'ownerShipCategory': instance.ownerShipCategory,
    };

PropertyType _$PropertyTypeFromJson(Map<String, dynamic> json) => PropertyType()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..active = json['active'] as bool?
  ..propertyType = json['propertyType'] as String?;

Map<String, dynamic> _$PropertyTypeToJson(PropertyType instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'active': instance.active,
      'propertyType': instance.propertyType,
    };

MutationDocument _$MutationDocumentFromJson(Map<String, dynamic> json) =>
    MutationDocument()
      ..code = json['code'] as String?
      ..active = json['active'] as bool?
      ..required = json['required'] as bool?
      ..description = json['description'] as String?
      ..hasDropdown = json['hasDropdown'] as bool?
      ..documentType = json['documentType'] as String?
      ..dropdownData = (json['dropdownData'] as List<dynamic>?)
          ?.map((e) =>
              MutationDocumentDropdownDatum.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$MutationDocumentToJson(MutationDocument instance) =>
    <String, dynamic>{
      'code': instance.code,
      'active': instance.active,
      'required': instance.required,
      'description': instance.description,
      'hasDropdown': instance.hasDropdown,
      'documentType': instance.documentType,
      'dropdownData': instance.dropdownData,
    };

MutationDocumentDropdownDatum _$MutationDocumentDropdownDatumFromJson(
        Map<String, dynamic> json) =>
    MutationDocumentDropdownDatum()
      ..code = json['code'] as String?
      ..active = json['active'] as bool?;

Map<String, dynamic> _$MutationDocumentDropdownDatumToJson(
        MutationDocumentDropdownDatum instance) =>
    <String, dynamic>{
      'code': instance.code,
      'active': instance.active,
    };

OwnerType _$OwnerTypeFromJson(Map<String, dynamic> json) => OwnerType()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..active = json['active']
  ..fromFy = json['fromFY'] as String?
  ..exemption = json['exemption'] == null
      ? null
      : Exemption.fromJson(json['exemption'] as Map<String, dynamic>);

Map<String, dynamic> _$OwnerTypeToJson(OwnerType instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'active': instance.active,
      'fromFY': instance.fromFy,
      'exemption': instance.exemption,
    };

Exemption _$ExemptionFromJson(Map<String, dynamic> json) => Exemption()
  ..rate = (json['rate'] as num?)?.toInt()
  ..maxAmount = (json['maxAmount'] as num?)?.toInt()
  ..flatAmount = (json['flatAmount'] as num?)?.toInt();

Map<String, dynamic> _$ExemptionToJson(Exemption instance) => <String, dynamic>{
      'rate': instance.rate,
      'maxAmount': instance.maxAmount,
      'flatAmount': instance.flatAmount,
    };

OwnerShipCategory _$OwnerShipCategoryFromJson(Map<String, dynamic> json) =>
    OwnerShipCategory()
      ..code = json['code'] as String?
      ..name = json['name'] as String?
      ..active = json['active'] as bool?;

Map<String, dynamic> _$OwnerShipCategoryToJson(OwnerShipCategory instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'active': instance.active,
    };
