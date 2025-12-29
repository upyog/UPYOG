import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mobile_app/config/base_config.dart';

class RequiredText extends StatelessWidget {
  const RequiredText({
    super.key,
    required this.text,
    this.fontSize,
    this.fontWeight,
    this.required = false,
  });

  final String text;
  final double? fontSize;
  final FontWeight? fontWeight;
  final bool required;

  @override
  Widget build(BuildContext context) {
    return Text.rich(
      TextSpan(
        children: [
          TextSpan(
            text: text,
            style: GoogleFonts.notoSans().copyWith(
                  fontSize: fontSize ?? 12.sp,
                  fontWeight: fontWeight,
                  color: BaseConfig.textColor,
                ),
          ),
          if (required)
            TextSpan(
              text: '*',
              style: GoogleFonts.notoSans().copyWith(
                    fontSize: fontSize ?? 12.sp,
                    fontWeight: fontWeight,
                    color: BaseConfig.redColor,
                  ),
            ),
        ],
      ),
    );
  }
}
