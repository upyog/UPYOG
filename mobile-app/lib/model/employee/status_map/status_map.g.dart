// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'status_map.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

StatusMap _$StatusMapFromJson(Map<String, dynamic> json) => StatusMap()
  ..count = (json['count'] as num?)?.toInt()
  ..applicationStatus = json['applicationstatus'] as String?
  ..businessService = json['businessservice'] as String?
  ..statusId = json['statusid'] as String?;

Map<String, dynamic> _$StatusMapToJson(StatusMap instance) => <String, dynamic>{
      'count': instance.count,
      'applicationstatus': instance.applicationStatus,
      'businessservice': instance.businessService,
      'statusid': instance.statusId,
    };
