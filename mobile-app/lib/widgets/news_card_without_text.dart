import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class NewsCardWithoutText extends StatelessWidget {
  const NewsCardWithoutText({
    super.key,
    required this.img,
    required this.orientation,
    required this.imgText,
  });

  final String img;
  final String imgText;
  final Orientation orientation;

  @override
  Widget build(BuildContext context) {
    final radius = Radius.circular(8.r);
    return Card(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(8.r),
      ),
      child: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              height: 150.h,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.only(
                  topRight: radius,
                  topLeft: radius,
                ),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.only(
                  topLeft: radius,
                  topRight: radius,
                ),
                child: Image.asset(
                  img,
                  width: Get.width,
                  fit: BoxFit.fill,
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.symmetric(horizontal: 12.w, vertical: 13.h),
              child: Row(
                children: [
                  SvgPicture.asset(
                    BaseConfig.propertySvg,
                    height: 24.h,
                    colorFilter: const ColorFilter.mode(
                      BaseConfig.textColor,
                      BlendMode.srcIn,
                    ),
                  ),
                  SizedBox(width: 7.w),
                  SizedBox(
                    width: 270,
                    child: MediumText(
                      text: imgText,
                      size: orientation == Orientation.portrait ? 15.sp : 7.sp,
                      fontWeight: FontWeight.w500,
                      textOverflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
