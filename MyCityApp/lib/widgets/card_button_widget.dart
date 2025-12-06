import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/widgets/medium_text.dart';

Widget cardButton({
  required String text,
  required Function() onPressed,
  bool isLoading = false,
  required IconData newIcon,
  required Color colorIcon,
  Orientation o = Orientation.portrait,
}) {
  return Card(
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(5.r),
    ),
    child: ListTile(
      leading: Padding(
        padding:
            o == Orientation.portrait ? EdgeInsets.all(8.w) : EdgeInsets.zero,
        child: CircleAvatar(
          backgroundColor: colorIcon.withValues(alpha: 0.1),
          radius: o == Orientation.portrait ? 20.r : 30.r,
          child: Padding(
            padding: o == Orientation.portrait
                ? EdgeInsets.all(8.w)
                : EdgeInsets.zero,
            child: Icon(
              newIcon,
              color: colorIcon.withValues(alpha: 0.8),
            ),
          ),
        ),
      ),
      title: isLoading
          ? const CircularProgressIndicator(
              color: BaseConfig.textColor,
            )
          : MediumTextNotoSans(
              text: text,
              color: BaseConfig.textColor,
              size: o == Orientation.portrait ? 14.sp : 8.sp,
              fontWeight: FontWeight.w600,
            ),
    ).rippleColor(onPressed, 200.r, borderRadius: 4.r),
  );
}
