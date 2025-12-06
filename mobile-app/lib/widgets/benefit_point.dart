import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/small_text.dart';

class BenefitPoint extends StatelessWidget {
  const BenefitPoint({
    super.key,
    required this.text,
    this.o = Orientation.portrait,
  });
  final String text;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Icon(
          Icons.check_circle,
          color: BaseConfig.deepGreenColor,
          size: o == Orientation.portrait ? 16 : 8.sp,
        ),
        SizedBox(width: 8.w),
        Expanded(
          child: SmallTextNotoSans(
            text: text,
            size: o == Orientation.portrait ? 11.sp : 6.sp,
            fontWeight: FontWeight.w500,
          ),
        ),
      ],
    );
  }
}
