import 'package:json_annotation/json_annotation.dart';
part 'file_store.g.dart';


@JsonSerializable()
class FileStore {
  @JsonKey(name: 'fileStoreIds')
  List<FileStoreId>? fileStoreIds;

  FileStore();

  factory FileStore.fromJson(Map<String, dynamic> json) =>
      _$FileStoreFromJson(json);

  Map<String, dynamic> toJson() => _$FileStoreToJson(this);
}

@JsonSerializable()
class FileStoreId {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'url')
  String? url;

  FileStoreId();

  factory FileStoreId.fromJson(Map<String, dynamic> json) =>
      _$FileStoreIdFromJson(json);

  Map<String, dynamic> toJson() => _$FileStoreIdToJson(this);
}
