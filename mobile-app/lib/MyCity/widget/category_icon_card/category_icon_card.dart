import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class CategoryIconCard extends StatelessWidget {
  const CategoryIconCard({
    super.key,
    this.onPressed,
    required this.title,
    required this.icon,
  });

  final VoidCallback? onPressed;
  final String title;
  final String icon;

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: onPressed,
      style: ButtonStyle(
        padding: WidgetStateProperty.all(
          EdgeInsets.symmetric(
            vertical: 16.h,
          ),
        ),
        backgroundColor: WidgetStateProperty.all(
          BaseConfig.mainBackgroundColor,
        ),
        shape: WidgetStateProperty.all(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(25.r),
          ),
        ),
        side: WidgetStateProperty.resolveWith<BorderSide>(
          (Set<WidgetState> states) {
            if (states.contains(WidgetState.pressed)) {
              return const BorderSide(
                color: BaseConfig.appThemeColor1,
                width: 2,
              );
            }
            return const BorderSide(
              color: BaseConfig.textColor,
              width: 2,
            );
          },
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SizedBox(
            height: 70.h,
            width: 60.w,
            child: Image.asset(
              icon,
              fit: BoxFit.contain,
            ),
          ),
          SizedBox(
            height: 5.h,
          ),
          Center(
            child: MediumTextNotoSans(
              text: title,
              fontWeight: FontWeight.bold,
              textAlign: TextAlign.center,
            ),
          ),
        ],
      ),
    );
  }
}
