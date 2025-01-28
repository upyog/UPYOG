import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/small_text.dart';

class GridCardWidget extends StatelessWidget {
  const GridCardWidget({
    super.key,
    required this.text,
    required this.icon,
    this.onPressed,
    this.o = Orientation.portrait,
  });

  final String text;
  final Widget icon;
  final void Function()? onPressed;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        IconButton.filled(
          style: IconButton.styleFrom(
            backgroundColor: BaseConfig.appThemeColor1,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(
                12.r,
              ),
            ),
            padding: EdgeInsets.all(12.w),
          ),
          onPressed: onPressed,
          icon: icon,
        ),
        SizedBox(height: 4.h),
        SmallTextNotoSans(
          text: text,
          size: o == Orientation.portrait ? 10.sp : 7.sp,
          maxLine: 2,
          fontWeight: FontWeight.w600,
          textAlign: TextAlign.center,
          textOverflow: TextOverflow.ellipsis,
        ),
      ],
    );
  }
}
