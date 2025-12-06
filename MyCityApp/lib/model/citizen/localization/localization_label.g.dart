// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'localization_label.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LocalizationLabel _$LocalizationLabelFromJson(Map<String, dynamic> json) =>
    LocalizationLabel()
      ..code = json['code'] as String?
      ..message = json['message'] as String?
      ..module = json['module'] as String?
      ..locale = json['locale'] as String?;

Map<String, dynamic> _$LocalizationLabelToJson(LocalizationLabel instance) =>
    <String, dynamic>{
      'code': instance.code,
      'message': instance.message,
      'module': instance.module,
      'locale': instance.locale,
    };
