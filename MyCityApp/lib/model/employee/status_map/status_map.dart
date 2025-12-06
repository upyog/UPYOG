import 'package:json_annotation/json_annotation.dart';

part 'status_map.g.dart';

@JsonSerializable()
class StatusMap {
  @JsonKey(name: 'count')
  int? count;
  @JsonKey(name: 'applicationstatus')
  String? applicationStatus;
  @JsonKey(name: 'businessservice')
  String? businessService;
  @JsonKey(name: 'statusid')
  String? statusId;

  StatusMap();

  factory StatusMap.fromJson(Map<String, dynamic> json) =>
      _$StatusMapFromJson(json);

  Map<String, dynamic> toJson() => _$StatusMapToJson(this);
}
