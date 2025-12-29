import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/big_text.dart';

Widget gradientBtn({
  double? width,
  double? height,
  double horizonPadding = 20.0,
  Function()? onPressed,
  required String text,
  bool isLoading = false,
  double? fontSize,
  FontWeight fontWeight = FontWeight.w700,
  Color buttonColor = BaseConfig.appThemeColor1,
  Color textColor = Colors.white,
  Color borderColor = BaseConfig.appThemeColor1,
  double? radius,
}) {
  return Container(
    width: width,
    height: height,
    decoration: BoxDecoration(
      color: buttonColor,
      borderRadius: BorderRadius.circular(radius ?? 10.r),
    ),
    child: ElevatedButton(
      style: ButtonStyle(
        shape: WidgetStateProperty.all<RoundedRectangleBorder>(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(radius ?? 10.r),
            side: BorderSide(color: borderColor),
          ),
        ),
        // minimumSize:
        //     MaterialStateProperty.all(Size(Get.width, 50)),
        backgroundColor: WidgetStateProperty.resolveWith(
          (states) {
            return states.contains(WidgetState.pressed)
                ? BaseConfig.appThemeColor2.withValues(alpha: 0.1)
                : buttonColor;
          },
        ),
        shadowColor: WidgetStateProperty.all(Colors.transparent),
        overlayColor: WidgetStateProperty.resolveWith(
          (states) {
            return states.contains(WidgetState.pressed)
                ? BaseConfig.appThemeColor1.withValues(alpha: 0.5)
                : null;
          },
        ),
      ),
      onPressed: onPressed,
      child: isLoading
          ? const Center(
              child: CircularProgressIndicator(
                color: Colors.white,
              ),
            )
          : BigTextNotoSans(
              text: text,
              color: textColor,
              size: fontSize ?? 18.sp,
              fontWeight: fontWeight,
              textOverflow: TextOverflow.ellipsis,
            ),
    ),
  ).paddingSymmetric(horizontal: horizonPadding);
}
