import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/text_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/small_text.dart';

class CheckBoxApp extends StatelessWidget {
  const CheckBoxApp({
    super.key,
    required this.title,
    required this.value,
    this.onTap,
    this.height,
    this.width,
    this.o = Orientation.portrait,
  });

  final String title;
  final bool value;
  final Function()? onTap;
  final double? height;
  final double? width;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    return TextButtonApp(
      onPressed: onTap,
      padding: EdgeInsets.symmetric(horizontal: 8.w),
      height: height ?? 40.h,
      width: width,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            height: 18.h,
            width: o == Orientation.portrait ? 18.w : 8.w,
            decoration: BoxDecoration(
              color: value ? BaseConfig.appThemeColor1 : Colors.transparent,
              border: Border.all(
                color:
                    value ? BaseConfig.appThemeColor1 : BaseConfig.greyColor1,
                width: 1,
              ),
              borderRadius: BorderRadius.circular(4.r),
            ),
            alignment: Alignment.center,
            child: value
                ? Icon(
                    Icons.check,
                    color: Colors.white,
                    size: 14.h,
                  )
                : null,
          ),
          SizedBox(width: 8.w),
          Flexible(
            child: Tooltip(
              message: title.capitalize,
              child: SmallTextNotoSans(
                text: title.capitalize!,
                fontWeight: FontWeight.w500,
                color: const Color(0xFF232323).withValues(alpha: 0.6),
                size: o == Orientation.portrait ? 12.sp : 6.sp,
                maxLine: 2,
                textOverflow: TextOverflow.ellipsis,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
