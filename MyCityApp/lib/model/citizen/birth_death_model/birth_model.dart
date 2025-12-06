import 'package:json_annotation/json_annotation.dart';

part 'birth_model.g.dart';


@JsonSerializable()
class Birth {
  @JsonKey(name: 'responseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'applications')
  List<Application>? applications;

  Birth();

  factory Birth.fromJson(Map<String, dynamic> json) => _$BirthFromJson(json);

  Map<String,dynamic> toJson() => _$BirthToJson(this);

}

@JsonSerializable()
class Application {
  @JsonKey(name: 'applicationCategory')
  String? applicationCategory;
  @JsonKey(name: 'applicationType')
  String? applicationType;
  @JsonKey(name: 'applicationNumber')
  String? applicationNumber;
  @JsonKey(name: 'applicationDate')
  String? applicationDate;
  @JsonKey(name: 'regNo')
  String? regNo;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'status')
  String? status;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'fileStoreId')
  String? fileStoreId;

  Application();

  factory Application.fromJson(Map<String, dynamic> json) => _$ApplicationFromJson(json);

  Map<String,dynamic> toJson() => _$ApplicationToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  String? ver;
  @JsonKey(name: 'ts')
  String? ts;
  @JsonKey(name: 'resMsgId')
  String? resMsgId;
  @JsonKey(name: 'msgId')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) => _$ResponseInfoFromJson(json);

  Map<String,dynamic> toJson() => _$ResponseInfoToJson(this);


}
