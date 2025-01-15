import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/small_text.dart';

class TextButtonNotoSans extends StatelessWidget {
  const TextButtonNotoSans({
    super.key,
    required this.text,
    this.fontSize,
    this.textColor = BaseConfig.appThemeColor1,
    this.padding,
    this.onPressed,
    this.fontWeight = FontWeight.w600,
    this.icon,
  });
  final String text;
  final double? fontSize;
  final Color textColor;
  final EdgeInsetsGeometry? padding;
  final void Function()? onPressed;
  final FontWeight fontWeight;
  final Widget? icon;

  @override
  Widget build(BuildContext context) {
    return TextButton(
      style: ButtonStyle(
        padding: WidgetStateProperty.all<EdgeInsetsGeometry>(
          padding ?? const EdgeInsets.all(0),
        ),
        minimumSize: WidgetStateProperty.all<Size>(Size.zero),
        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
      ),
      onPressed: onPressed,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SmallTextNotoSans(
            text: text,
            size: fontSize ?? 10.sp,
            fontWeight: fontWeight,
            color: textColor,
          ),
         icon ?? const SizedBox(),
        ],
      ),
    );
  }
}
