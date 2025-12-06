// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'estimate_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Estimate _$EstimateFromJson(Map<String, dynamic> json) => Estimate()
  ..calculation = (json['Calculation'] as List<dynamic>?)
      ?.map((e) => Calculation.fromJson(e as Map<String, dynamic>))
      .toList();

Map<String, dynamic> _$EstimateToJson(Estimate instance) => <String, dynamic>{
      'Calculation': instance.calculation,
    };

Calculation _$CalculationFromJson(Map<String, dynamic> json) => Calculation()
  ..tenantId = json['tenantId'] as String?
  ..totalAmount = (json['totalAmount'] as num?)?.toInt()
  ..charge = (json['charge'] as num?)?.toInt()
  ..taxAmount = (json['taxAmount'] as num?)?.toInt()
  ..fee = (json['fee'] as num?)?.toInt()
  ..exemption = (json['exemption'] as num?)?.toInt()
  ..rebate = (json['rebate'] as num?)?.toInt()
  ..penalty = (json['penalty'] as num?)?.toInt()
  ..taxHeadEstimates = (json['taxHeadEstimates'] as List<dynamic>?)
      ?.map((e) => TaxHeadEstimate.fromJson(e as Map<String, dynamic>))
      .toList()
  ..applicationNo = json['applicationNo'] as String?
  ..billingSlabIds = (json['billingSlabIds'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList()
  ..waterConnection = json['waterConnection']
  ..sewerageConnection = json['sewerageConnection']
  ..connectionNo = json['connectionNo'];

Map<String, dynamic> _$CalculationToJson(Calculation instance) {
  final val = <String, dynamic>{
    'tenantId': instance.tenantId,
    'totalAmount': instance.totalAmount,
    'charge': instance.charge,
    'taxAmount': instance.taxAmount,
    'fee': instance.fee,
    'exemption': instance.exemption,
    'rebate': instance.rebate,
    'penalty': instance.penalty,
    'taxHeadEstimates': instance.taxHeadEstimates,
    'applicationNo': instance.applicationNo,
    'billingSlabIds': instance.billingSlabIds,
    'waterConnection': instance.waterConnection,
  };

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('sewerageConnection', instance.sewerageConnection);
  val['connectionNo'] = instance.connectionNo;
  return val;
}

TaxHeadEstimate _$TaxHeadEstimateFromJson(Map<String, dynamic> json) =>
    TaxHeadEstimate()
      ..taxHeadCode = json['taxHeadCode'] as String?
      ..estimateAmount = (json['estimateAmount'] as num?)?.toInt()
      ..category = json['category'] as String?;

Map<String, dynamic> _$TaxHeadEstimateToJson(TaxHeadEstimate instance) =>
    <String, dynamic>{
      'taxHeadCode': instance.taxHeadCode,
      'estimateAmount': instance.estimateAmount,
      'category': instance.category,
    };
