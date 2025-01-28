// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'fire_noc_locality.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LocalityNoc _$LocalityNocFromJson(Map<String, dynamic> json) => LocalityNoc(
      localities: (json['Localities'] as List<dynamic>?)
          ?.map((e) => Locality.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$LocalityNocToJson(LocalityNoc instance) =>
    <String, dynamic>{
      'Localities': instance.localities,
    };

Locality _$LocalityFromJson(Map<String, dynamic> json) => Locality(
      referenceNumber: json['referencenumber'] as String?,
      locality: json['locality'] as String?,
    );

Map<String, dynamic> _$LocalityToJson(Locality instance) => <String, dynamic>{
      'referencenumber': instance.referenceNumber,
      'locality': instance.locality,
    };
