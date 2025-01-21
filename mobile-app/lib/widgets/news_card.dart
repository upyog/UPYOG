import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class NewsCard extends StatelessWidget {
  const NewsCard({super.key, required this.img, required this.orientation});

  final String img;
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
              height: 130.h,
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
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      MediumText(
                        text: 'Blood Donation Camp',
                        size: orientation == Orientation.portrait ? 15.sp : 7.sp,
                        fontWeight: FontWeight.w500,
                      ),
                      SmallText(
                        text: '15-Nov-2024 - 15-Dec-2024',
                        size: orientation == Orientation.portrait ? 13.sp : 6.sp,
                        color: BaseConfig.subTextColor,
                      ),
                    ],
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
