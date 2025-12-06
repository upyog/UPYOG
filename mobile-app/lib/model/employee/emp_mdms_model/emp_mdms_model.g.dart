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
          json['ws-services-calculation'] as Map<String, dynamic>);

Map<String, dynamic> _$MdmsResEmpToJson(MdmsResEmp instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('BPA', instance.bpaObps);
  writeNotNull('NOC', instance.nocObps);
  writeNotNull('common-masters', instance.commonMastersObps);
  writeNotNull('ws-services-masters', instance.wsServicesMasters);
  writeNotNull('ws-services-calculation', instance.wsServicesCalculation);
  return val;
}

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
          .toList();

Map<String, dynamic> _$CommonMastersObpsToJson(CommonMastersObps instance) =>
    <String, dynamic>{
      'DocumentType': instance.documentType,
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
