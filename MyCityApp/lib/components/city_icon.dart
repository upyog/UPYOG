import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';

class CityIcon extends StatelessWidget {
  const CityIcon({super.key, required this.districtCode, this.height});

  final String districtCode;
  final double? height;

  @override
  Widget build(BuildContext context) {
    final h = height ?? 40.h;
    if (districtCode == 'CITYA') {
      return Image.asset(BaseConfig.cityImg_A, height: h, width: h);
    } else if (districtCode == 'CITYB') {
      return Image.asset(BaseConfig.cityImg_B, height: h, width: h);
    } else if (districtCode == 'CITYC') {
      return Image.asset(BaseConfig.cityImg_C, height: h, width: h);
    } else if (districtCode == 'CITYD') {
      return Image.asset(BaseConfig.cityImg_D, height: h, width: h);
    } else if (districtCode == 'CITYE') {
      return Image.asset(BaseConfig.cityImg_E, height: h, width: h);
    } else {
      return Image.asset(BaseConfig.cityImg_PG, height: h, width: h);
    }
  }
}


