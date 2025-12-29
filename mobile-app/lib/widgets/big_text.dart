import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mobile_app/config/base_config.dart';

class BigText extends StatelessWidget {
  const BigText({
    super.key,
    required this.text,
    this.color = BaseConfig.textColor,
    this.size,
    this.fontWeight,
    this.textAlign,
    this.maxLine,
    this.textOverflow,
    this.fontString,
  });

  final String text;
  final String? fontString;
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
        fontSize: size ?? 22.sp,
        fontWeight: fontWeight,
        color: color,
        fontFamily: fontString,
      ),
      maxLines: maxLine,
      textAlign: textAlign,
      overflow: textOverflow,
    );
  }
}

class InterText extends StatelessWidget {
  const InterText({
    super.key,
    required this.text,
    this.color = BaseConfig.textColor,
    this.size,
    this.fontWeight,
    this.textAlign,
    this.maxLine,
    this.textOverflow,
    this.fontString,
  });

  final String text;
  final String? fontString;
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
      style: GoogleFonts.inter().copyWith(
            fontSize: size ?? 22.sp,
            fontWeight: fontWeight,
            color: color,
            fontFamily: fontString,
          ),
      maxLines: maxLine,
      textAlign: textAlign,
      overflow: textOverflow,
    );
  }
}

// Font size 22
class BigTextNotoSans extends StatelessWidget {
  const BigTextNotoSans({
    super.key,
    required this.text,
    this.color = BaseConfig.textColor,
    this.size,
    this.fontWeight,
    this.textAlign,
    this.maxLine,
    this.textOverflow,
    this.fontString,
  });

  final String text;
  final String? fontString;
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
      style: GoogleFonts.notoSans().copyWith(
            fontSize: size ?? 22.sp,
            fontWeight: fontWeight,
            color: color,
            fontFamily: fontString,
          ),
      maxLines: maxLine,
      textAlign: textAlign,
      overflow: textOverflow,
    );
  }
}

class BigSelectableTextNotoSans extends StatelessWidget {
  const BigSelectableTextNotoSans({
    super.key,
    required this.text,
    this.color = BaseConfig.textColor,
    this.size,
    this.fontWeight,
    this.textAlign,
    this.maxLine,
    this.fontString,
  });

  final String text;
  final String? fontString;
  final Color color;
  final double? size;
  final FontWeight? fontWeight;
  final TextAlign? textAlign;
  final int? maxLine;

  @override
  Widget build(BuildContext context) {
    return SelectableText(
      text,
      style: GoogleFonts.notoSans().copyWith(
        fontSize: size ?? 22.sp,
        fontWeight: fontWeight,
        color: color,
        fontFamily: fontString,
      ),
      maxLines: maxLine,
      textAlign: textAlign,
    );
  }
}
