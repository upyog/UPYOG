import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:mobile_app/components/image_placeholder.dart';
import 'package:mobile_app/config/base_config.dart';

class HomeHeader extends StatelessWidget {
  const HomeHeader({super.key});

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            InkWell(
              customBorder: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8.r),
              ),
              child: Image.asset(
                BaseConfig.HOME_HEADER_LOGO,
                fit: BoxFit.contain,
                height: o == Orientation.portrait ? 28.h : 30.h,
                width: o == Orientation.portrait ? 207.w : 85.w,
              ),
            ),
            // const Icon(
            //   Icons.notifications_outlined,
            //   size: 30,
            // ),
            ImagePlaceHolder(
              radius: 20.r,
              iconSize: 30,
              height: 155.h,
              width: 155.w,
              padding: EdgeInsets.zero,
              iconColor: BaseConfig.greyColor1,
            ),
          ],
        ),
      ],
    );
  }
}
