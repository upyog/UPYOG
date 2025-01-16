import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';

class CustomElevateBtn extends StatelessWidget {
  const CustomElevateBtn({
    super.key,
    this.onPressed,
    required this.icon,
    this.iconSize,
  });

  final VoidCallback? onPressed;
  final String icon;
  final double? iconSize;

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      style: ButtonStyle(
        padding: WidgetStateProperty.all(EdgeInsets.all(12.w)),
        shape: WidgetStateProperty.all(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12.r),
          ),
        ),
        side: WidgetStateBorderSide.resolveWith(
          (states) {
            if (states.contains(WidgetState.pressed)) {
              return const BorderSide(
                color: BaseConfig.appThemeColor1,
                width: 3,
              );
            }
            return const BorderSide(
              color: BaseConfig.textColor,
              width: 3,
            );
          },
        ),
      ),
      onPressed: onPressed,
      child: Image.asset(
        icon,
        height: iconSize ?? 40.h,
        width: iconSize ?? 40.w,
        fit: BoxFit.fill,
      ),
    );
  }
}
