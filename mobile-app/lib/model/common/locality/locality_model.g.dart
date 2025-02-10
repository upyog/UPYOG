// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'locality_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LocalityModel _$LocalityModelFromJson(Map<String, dynamic> json) =>
    LocalityModel()
      ..responseInfo = json['ResponseInfo'] == null
          ? null
          : ResponseInfo.fromJson(json['ResponseInfo'] as Map<String, dynamic>)
      ..tenantBoundary = (json['TenantBoundary'] as List<dynamic>?)
          ?.map((e) => TenantBoundary.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$LocalityModelToJson(LocalityModel instance) =>
    <String, dynamic>{
      'ResponseInfo': instance.responseInfo,
      'TenantBoundary': instance.tenantBoundary,
    };

ResponseInfo _$ResponseInfoFromJson(Map<String, dynamic> json) => ResponseInfo()
  ..apiId = json['apiId'] as String?
  ..ver = json['ver'] as String?
  ..ts = json['ts'] as String?
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

TenantBoundary _$TenantBoundaryFromJson(Map<String, dynamic> json) =>
    TenantBoundary()
      ..hierarchyType = json['hierarchyType'] == null
          ? null
          : HierarchyType.fromJson(
              json['hierarchyType'] as Map<String, dynamic>)
      ..boundary = (json['boundary'] as List<dynamic>?)
          ?.map((e) => Boundary.fromJson(e as Map<String, dynamic>))
          .toList()
      ..tenantId = json['tenantId'] as String?;

Map<String, dynamic> _$TenantBoundaryToJson(TenantBoundary instance) =>
    <String, dynamic>{
      'hierarchyType': instance.hierarchyType,
      'boundary': instance.boundary,
      'tenantId': instance.tenantId,
    };

Boundary _$BoundaryFromJson(Map<String, dynamic> json) => Boundary()
  ..code = json['code'] as String?
  ..name = json['name'] as String?
  ..label = json['label'] as String?
  ..latitude = json['latitude']
  ..longitude = json['longitude']
  ..area = json['area'] as String?
  ..pinCode = (json['pincode'] as List<dynamic>?)
      ?.map((e) => (e as num).toInt())
      .toList()
  ..boundaryNum = (json['boundaryNum'] as num?)?.toInt()
  ..children = json['children'] as List<dynamic>?;

Map<String, dynamic> _$BoundaryToJson(Boundary instance) => <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'label': instance.label,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'area': instance.area,
      'pincode': instance.pinCode,
      'boundaryNum': instance.boundaryNum,
      'children': instance.children,
    };

HierarchyType _$HierarchyTypeFromJson(Map<String, dynamic> json) =>
    HierarchyType()
      ..id = json['id']
      ..name = json['name'] as String?
      ..code = json['code'] as String?
      ..localName = json['localName']
      ..tenantId = json['tenantId']
      ..createdBy = json['createdBy']
      ..createdDate = json['createdDate']
      ..lastModifiedBy = json['lastModifiedBy']
      ..lastModifiedDate = json['lastModifiedDate']
      ..version = (json['version'] as num?)?.toInt()
      ..hierarchyTypeNew = json['new'] as bool?;

Map<String, dynamic> _$HierarchyTypeToJson(HierarchyType instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'code': instance.code,
      'localName': instance.localName,
      'tenantId': instance.tenantId,
      'createdBy': instance.createdBy,
      'createdDate': instance.createdDate,
      'lastModifiedBy': instance.lastModifiedBy,
      'lastModifiedDate': instance.lastModifiedDate,
      'version': instance.version,
      'new': instance.hierarchyTypeNew,
    };
