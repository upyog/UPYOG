import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mobile_app/config/base_config.dart';

class SmallText extends StatelessWidget {
  const SmallText({
    super.key,
    required this.text,
    this.color = BaseConfig.textColor,
    this.size,
    this.fontWeight,
    this.textAlign,
    this.maxLine,
    this.textOverflow,
  });

  final String text;
  final Color color;
  final double? size;
  final FontWeight? fontWeight;
  final TextAlign? textAlign;
  final int? maxLine;
  final TextOverflow? textOverflow;

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: TextStyle(
        fontSize: size ?? 12.sp,
        fontWeight: fontWeight,
        color: color,
      ),
      maxLines: maxLine,
      textAlign: textAlign,
      overflow: textOverflow,
    );
  }
}

/// Font size `[12]`
class SmallTextNotoSans extends StatelessWidget {
  const SmallTextNotoSans({
    super.key,
    required this.text,
    this.color = BaseConfig.textColor,
    this.size,
    this.fontWeight,
    this.textAlign,
    this.maxLine,
    this.textOverflow,
  });

  final String text;
  final Color color;
  final double? size;
  final FontWeight? fontWeight;
  final TextAlign? textAlign;
  final int? maxLine;
  final TextOverflow? textOverflow;

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: GoogleFonts.notoSans.call().copyWith(
            fontSize: size ?? 12.sp,
            fontWeight: fontWeight,
            color: color,
          ),
      maxLines: maxLine,
      textAlign: textAlign,
      overflow: textOverflow,
    );
  }
}
