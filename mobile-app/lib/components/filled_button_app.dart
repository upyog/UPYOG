import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';

class FilledButtonApp extends StatelessWidget {
  const FilledButtonApp({
    super.key,
    required this.text,
    this.onPressed,
    this.backgroundColor = BaseConfig.appThemeColor1,
    this.textColor = Colors.white,
    this.height,
    this.width,
    this.verticalPadding = 10,
    this.horizontalPadding = 40,
    this.fontSize,
    this.isLoading = false,
    this.radius = 10,
    this.circularColor = BaseConfig.appThemeColor1,
    this.textAlign,
    this.side = BorderSide.none,
    this.fontWeight,
  });

  final String text;
  final void Function()? onPressed;
  final Color backgroundColor;
  final Color textColor;
  final double? height;
  final double? width;
  final double verticalPadding;
  final double horizontalPadding;
  final double? fontSize;
  final bool isLoading;
  final double radius;
  final Color circularColor;
  final TextAlign? textAlign;
  final BorderSide side;
  final FontWeight? fontWeight;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: height ?? 50.h,
      width: width,
      child: FilledButton(
        style: ButtonStyle(
          shape: WidgetStateProperty.all<RoundedRectangleBorder>(
            RoundedRectangleBorder(
              side: side,
              borderRadius: BorderRadius.circular(radius),
            ),
          ),
          backgroundColor: WidgetStateProperty.all<Color>(backgroundColor),
          padding: WidgetStateProperty.all<EdgeInsetsGeometry>(
            EdgeInsets.symmetric(
              horizontal: horizontalPadding,
              vertical: verticalPadding,
            ),
          ),
          overlayColor: WidgetStateProperty.resolveWith(
            (states) {
              return states.contains(WidgetState.pressed)
                  ? Colors.grey.withAlpha(100)
                  : null;
            },
          ),
        ),
        onPressed: onPressed,
        child: isLoading
            ? showCircularIndicator(color: circularColor)
            : BigTextNotoSans(
                text: text.capitalize!,
                color: textColor,
                textAlign: textAlign,
                fontWeight: fontWeight,
                size: fontSize ?? 16.sp,
              ),
      ),
    );
  }
}
