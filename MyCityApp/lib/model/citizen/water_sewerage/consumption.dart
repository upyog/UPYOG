
import 'package:json_annotation/json_annotation.dart';

part 'consumption.g.dart';

@JsonSerializable()
class Consumption {
  @JsonKey(name: 'ResponseInfo')
  ResponseInfo? responseInfo;
  @JsonKey(name: 'meterReadings')
  List<MeterReading>? meterReadings;

  Consumption();

  factory Consumption.fromJson(Map<String, dynamic> json) => _$ConsumptionFromJson(json);

  Map<String, dynamic> toJson() => _$ConsumptionToJson(this);

}



@JsonSerializable()
class MeterReading {
  @JsonKey(name: 'id')
  String? id;
  @JsonKey(name: 'billingPeriod')
  String? billingPeriod;
  @JsonKey(name: 'meterStatus')
  String? meterStatus;
  @JsonKey(name: 'lastReading')
  int? lastReading;
  @JsonKey(name: 'lastReadingDate')
  int? lastReadingDate;
  @JsonKey(name: 'currentReading')
  int? currentReading;
  @JsonKey(name: 'currentReadingDate')
  int? currentReadingDate;
  @JsonKey(name: 'connectionNo')
  String? connectionNo;
  @JsonKey(name: 'consumption')
  dynamic consumption;
  @JsonKey(name: 'generateDemand')
  bool? generateDemand;
  @JsonKey(name: 'auditDetails')
  AuditDetails? auditDetails;
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'status')
  dynamic status;

  MeterReading();

  factory MeterReading.fromJson(Map<String, dynamic> json) => _$MeterReadingFromJson(json);

  Map<String, dynamic> toJson() => _$MeterReadingToJson(this);
}

@JsonSerializable()
class AuditDetails {
  @JsonKey(name: 'createdBy')
  String? createdBy;
  @JsonKey(name: 'lastModifiedBy')
  String? lastModifiedBy;
  @JsonKey(name: 'createdTime')
  int? createdTime;
  @JsonKey(name: 'lastModifiedTime')
  int? lastModifiedTime;

  AuditDetails();

  factory AuditDetails.fromJson(Map<String, dynamic> json) => _$AuditDetailsFromJson(json);

  Map<String, dynamic> toJson() => _$AuditDetailsToJson(this);
}

@JsonSerializable()
class ResponseInfo {
  @JsonKey(name: 'apiId')
  String? apiId;
  @JsonKey(name: 'ver')
  dynamic ver;
  @JsonKey(name: 'ts')
  dynamic ts;
  @JsonKey(name: 'resMsgId')
  String? resMsgId;
  @JsonKey(name: 'msgId')
  String? msgId;
  @JsonKey(name: 'status')
  String? status;

  ResponseInfo();

  factory ResponseInfo.fromJson(Map<String, dynamic> json) => _$ResponseInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ResponseInfoToJson(this);
}
