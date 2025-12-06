import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/small_text.dart';

Widget iconTextButton({
  required String label,
  String? assetPath,
  Icon? icon,
  Color labelColor = BaseConfig.textColor,
  required VoidCallback onPressed,
  double? labelSize,
  bool isLoading = false,
  Color backgroundColor = BaseConfig.mainBackgroundColor,
  Color? borderColor,
  double? width,
  double? radius,
  IconAlignment iconAlignment = IconAlignment.start,
}) {
  return SizedBox(
    height: 50.h,
    width: width,
    child: ElevatedButton.icon(
      iconAlignment: iconAlignment,
      style: ButtonStyle(
        backgroundColor: WidgetStatePropertyAll<Color>(backgroundColor),
        shape: WidgetStatePropertyAll<OutlinedBorder>(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(radius ?? 10.r),
          ),
        ),
      ),
      label: isLoading
          ? SizedBox(
              height: 20.h,
              width: 20.w,
              child: showCircularIndicator(
                color: BaseConfig.appThemeColor1,
              ),
            )
          : SmallTextNotoSans(text: label, color: labelColor, size: labelSize),
      icon: isLoading
          ? const SizedBox.shrink()
          : Padding(
              padding: EdgeInsets.only(right: 8.w),
              child: assetPath != null ? SvgPicture.asset(assetPath) : icon,
            ),
      onPressed: onPressed,
    ),
  );
}
