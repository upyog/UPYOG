import 'package:json_annotation/json_annotation.dart';

part 'fire_noc_locality.g.dart';

@JsonSerializable()
class LocalityNoc {
  @JsonKey(name: "Localities")
  List<Locality>? localities;

  LocalityNoc({
    this.localities,
  });

  factory LocalityNoc.fromJson(Map<String, dynamic> json) =>
      _$LocalityNocFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityNocToJson(this);
}

@JsonSerializable()
class Locality {
  @JsonKey(name: "referencenumber")
  String? referenceNumber;
  @JsonKey(name: "locality")
  String? locality;

  Locality({
    this.referenceNumber,
    this.locality,
  });

  factory Locality.fromJson(Map<String, dynamic> json) => _$LocalityFromJson(json);

  Map<String, dynamic> toJson() => _$LocalityToJson(this);
}
