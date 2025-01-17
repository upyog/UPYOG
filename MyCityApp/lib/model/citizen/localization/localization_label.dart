import 'package:json_annotation/json_annotation.dart';

part 'localization_label.g.dart';

@JsonSerializable()
class LocalizationLabel {
  @JsonKey(name: "code")
  String? code;

  @JsonKey(name: "message")
  String? message;

  @JsonKey(name: "module")
  String? module;

  @JsonKey(name: "locale")
  String? locale;

  LocalizationLabel();

  factory LocalizationLabel.fromJson(Map<String, dynamic> json) =>
      _$LocalizationLabelFromJson(json);

  Map<String, dynamic> toJson() => _$LocalizationLabelToJson(this);
}
