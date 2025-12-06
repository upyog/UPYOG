// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'file_store.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FileStore _$FileStoreFromJson(Map<String, dynamic> json) => FileStore()
  ..fileStoreIds = (json['fileStoreIds'] as List<dynamic>?)
      ?.map((e) => FileStoreId.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$FileStoreToJson(FileStore instance) => <String, dynamic>{
      'fileStoreIds': instance.fileStoreIds,
    };

FileStoreId _$FileStoreIdFromJson(Map<String, dynamic> json) => FileStoreId()
  ..id = json['id'] as String?
  ..url = json['url'] as String?;

Map<String, dynamic> _$FileStoreIdToJson(FileStoreId instance) =>
    <String, dynamic>{
      'id': instance.id,
      'url': instance.url,
    };
