import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class IconRow extends StatelessWidget {
  const IconRow({
    super.key,
    this.icon,
    required this.text,
    this.iconColor,
    this.iconSize,
    this.textColor = BaseConfig.textColor,
    this.textSize,
    this.fontWeight,
  });

  final IconData? icon;
  final String text;
  final Color? iconColor;
  final Color textColor;
  final double? iconSize;
  final double? textSize;
  final FontWeight? fontWeight;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        if (icon != null) ...[
          Icon(
            icon,
            color: iconColor ?? Colors.grey.shade500,
            size: iconSize,
          ),
          SizedBox(width: 10.w),
        ],
        Expanded(
          child: MediumSelectableTextNotoSans(
            text: text,
            color: textColor,
            size: textSize,
            fontWeight: fontWeight,
          ),
        ),
      ],
    );
  }
}
