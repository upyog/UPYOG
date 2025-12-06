import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/widgets/small_text.dart';

class WrapText extends StatelessWidget {
  const WrapText({
    super.key,
    required this.title,
    required this.text,
    this.o = Orientation.portrait,
  });

  final String title;
  final String text;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SmallTextNotoSans(
          text: title,
          fontWeight: FontWeight.w400,
          size: o == Orientation.portrait ? 12.sp : 6.sp,
        ),
        Expanded(
          child: SmallTextNotoSans(
            text: text,
            fontWeight: FontWeight.w600,
            size: o == Orientation.portrait ? 12.sp : 6.sp,
            maxLine: 2,
          ),
        ),
      ],
    );
  }
}
