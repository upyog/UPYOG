// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'comparison_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Comparison _$ComparisonFromJson(Map<String, dynamic> json) => Comparison()
  ..comparisonDetail = json['comparisonDetail'] == null
      ? null
      : ComparisonDetail.fromJson(
          json['comparisonDetail'] as Map<String, dynamic>);

Map<String, dynamic> _$ComparisonToJson(Comparison instance) =>
    <String, dynamic>{
      'comparisonDetail': instance.comparisonDetail,
    };

ComparisonDetail _$ComparisonDetailFromJson(Map<String, dynamic> json) =>
    ComparisonDetail()
      ..edcrNumber = json['edcrNumber'] as String?
      ..ocdcrNumber = json['ocdcrNumber'] as String?
      ..comparisonReport = json['comparisonReport'] as String?
      ..tenantId = json['tenantId'] as String?
      ..status = json['status'] as String?
      ..errors = json['errors'];

Map<String, dynamic> _$ComparisonDetailToJson(ComparisonDetail instance) =>
    <String, dynamic>{
      'edcrNumber': instance.edcrNumber,
      'ocdcrNumber': instance.ocdcrNumber,
      'comparisonReport': instance.comparisonReport,
      'tenantId': instance.tenantId,
      'status': instance.status,
      'errors': instance.errors,
    };
