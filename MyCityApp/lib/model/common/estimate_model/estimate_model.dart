import 'package:json_annotation/json_annotation.dart';

part 'estimate_model.g.dart';

@JsonSerializable()
class Estimate {
  @JsonKey(name: 'Calculation')
  List<Calculation>? calculation;

  Estimate();

  factory Estimate.fromJson(Map<String, dynamic> json) =>
      _$EstimateFromJson(json);

  Map<String, dynamic> toJson() => _$EstimateToJson(this);
}

@JsonSerializable()
class Calculation {
  @JsonKey(name: 'tenantId')
  String? tenantId;
  @JsonKey(name: 'totalAmount')
  int? totalAmount;
  @JsonKey(name: 'charge')
  int? charge;
  @JsonKey(name: 'taxAmount')
  int? taxAmount;
  @JsonKey(name: 'fee')
  int? fee;
  @JsonKey(name: 'exemption')
  int? exemption;
  @JsonKey(name: 'rebate')
  int? rebate;
  @JsonKey(name: 'penalty')
  int? penalty;
  @JsonKey(name: 'taxHeadEstimates')
  List<TaxHeadEstimate>? taxHeadEstimates;
  @JsonKey(name: 'applicationNo')
  String? applicationNo;
  @JsonKey(name: 'billingSlabIds')
  List<String>? billingSlabIds;
  @JsonKey(name: 'waterConnection')
  dynamic waterConnection;
  @JsonKey(name: 'sewerageConnection', includeIfNull: false)
  dynamic sewerageConnection;
  @JsonKey(name: 'connectionNo')
  dynamic connectionNo;

  Calculation();

  factory Calculation.fromJson(Map<String, dynamic> json) =>
      _$CalculationFromJson(json);

  Map<String, dynamic> toJson() => _$CalculationToJson(this);
}

@JsonSerializable()
class TaxHeadEstimate {
  @JsonKey(name: 'taxHeadCode')
  String? taxHeadCode;
  @JsonKey(name: 'estimateAmount')
  int? estimateAmount;
  @JsonKey(name: 'category')
  String? category;

  TaxHeadEstimate();

  factory TaxHeadEstimate.fromJson(Map<String, dynamic> json) =>
      _$TaxHeadEstimateFromJson(json);

  Map<String, dynamic> toJson() => _$TaxHeadEstimateToJson(this);
}
