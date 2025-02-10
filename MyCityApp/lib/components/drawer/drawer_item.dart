import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/medium_text.dart';

Widget drawerItem({
  required String label,
  Widget? icon,
  double? textSize,
  Color textColor = BaseConfig.textColor,
}) {
  textSize ??= 14.sp;
  return Padding(
    padding: const EdgeInsets.all(10),
    child: Row(
      children: [
        icon ?? const SizedBox(),
        const SizedBox(
          width: 20,
        ),
        Expanded(
          child: Tooltip(
            message: label,
            child: MediumTextNotoSans(
              text: label,
              color: textColor,
              textOverflow: TextOverflow.ellipsis,
              maxLine: 1,
              size: textSize,
            ),
          ),
        ),
      ],
    ),
  );
}
