import 'package:json_annotation/json_annotation.dart';

part 'locality_model.g.dart';

@JsonSerializable()
class LocalityModel {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'TenantBoundary')
  List<TenantBoundary>? tenantBoundary;

  LocalityModel();

  factory LocalityModel.fromJson(Map<String, dynamic> json) =>
      _$LocalityModelFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityModelToJson(this);
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

  factory ResponseInfo.fromJson(Map<String, dynamic> json) =>
      _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}

@JsonSerializable()
class TenantBoundary {
  @JsonKey(name: 'hierarchyType')
  HierarchyType? hierarchyType;
  @JsonKey(name: 'boundary')
  List<Boundary>? boundary;
  @JsonKey(name: 'tenantId')
  String? tenantId;

  TenantBoundary();

  factory TenantBoundary.fromJson(Map<String, dynamic> json) =>
      _$TenantBoundaryFromJson(json);

  Map<String, dynamic> toJson() => _$TenantBoundaryToJson(this);
}

@JsonSerializable()
class Boundary {
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'label')
  String? label;
  @JsonKey(name: 'latitude')
  dynamic latitude;
  @JsonKey(name: 'longitude')
  dynamic longitude;
  @JsonKey(name: 'area')
  String? area;
  @JsonKey(name: 'pincode')
  List<int>? pinCode;
  @JsonKey(name: 'boundaryNum')
  int? boundaryNum;
  @JsonKey(name: 'children')
  List<dynamic>? children;

  Boundary();

  factory Boundary.fromJson(Map<String, dynamic> json) =>
      _$BoundaryFromJson(json);

  Map<String, dynamic> toJson() => _$BoundaryToJson(this);
}

@JsonSerializable()
class HierarchyType {
  @JsonKey(name: 'id')
  dynamic id;
  @JsonKey(name: 'name')
  String? name;
  @JsonKey(name: 'code')
  String? code;
  @JsonKey(name: 'localName')
  dynamic localName;
  @JsonKey(name: 'tenantId')
  dynamic tenantId;
  @JsonKey(name: 'createdBy')
  dynamic createdBy;
  @JsonKey(name: 'createdDate')
  dynamic createdDate;
  @JsonKey(name: 'lastModifiedBy')
  dynamic lastModifiedBy;
  @JsonKey(name: 'lastModifiedDate')
  dynamic lastModifiedDate;
  @JsonKey(name: 'version')
  int? version;
  @JsonKey(name: 'new')
  bool? hierarchyTypeNew;

  HierarchyType();

  factory HierarchyType.fromJson(Map<String, dynamic> json) =>
      _$HierarchyTypeFromJson(json);

  Map<String, dynamic> toJson() => _$HierarchyTypeToJson(this);
}
