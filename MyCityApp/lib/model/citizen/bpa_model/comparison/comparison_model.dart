import 'package:json_annotation/json_annotation.dart';

part 'comparison_model.g.dart';


@JsonSerializable()
class Comparison {
    ComparisonDetail? comparisonDetail;

    Comparison();

    factory Comparison.fromJson(Map<String, dynamic> json) => _$ComparisonFromJson(json);

    Map<String, dynamic> toJson() => _$ComparisonToJson(this);
}

@JsonSerializable()
class ComparisonDetail {
    @JsonKey(name: "edcrNumber")
    String? edcrNumber;
    @JsonKey(name: "ocdcrNumber")
    String? ocdcrNumber;
    @JsonKey(name: "comparisonReport")
    String? comparisonReport;
    @JsonKey(name: "tenantId")
    String? tenantId;
    @JsonKey(name: "status")
    String? status;
    @JsonKey(name: "errors")
    dynamic errors;

    ComparisonDetail();

    factory ComparisonDetail.fromJson(Map<String, dynamic> json) => _$ComparisonDetailFromJson(json);

    Map<String, dynamic> toJson() => _$ComparisonDetailToJson(this);
}
