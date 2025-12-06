import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

class NewsCard extends StatelessWidget {
  const NewsCard({
    super.key,
    required this.img,
    required this.orientation,
    required this.pageController,
  });

  final String img;
  final Orientation orientation;
  final PageController pageController;
  @override
  Widget build(BuildContext context) {
    // final radius = Radius.circular(8.r);
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8.r),
            ),
            child: Container(
              height: 130.h,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8.r),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(8.r),
                child: Image.asset(
                  img,
                  width: Get.width,
                  fit: BoxFit.fill,
                ),
              ),
            ),
          ),
          const SizedBox(
            height: 10,
          ),
          SmoothPageIndicator(
            controller: pageController,
            count: BaseConfig.APP_HOME_BANNERS.split(',').length,
            effect: ExpandingDotsEffect(
              dotHeight: 6.h,
              dotWidth: orientation == Orientation.portrait ? 6.w : 3.w,
              expansionFactor: 2,
              spacing: 4.w,
              activeDotColor: BaseConfig.appThemeColor1,
              dotColor: BaseConfig.dotGrayColor,
            ),
          ),
          const SizedBox(
            height: 20,
          ),
          InterText(
            text: getLocalizedString(i18.common.WHATS_NEW),
            fontWeight: FontWeight.w800,
            size: orientation == Orientation.portrait ? 16.sp : 8.sp,
            color: BaseConfig.textColor2,
          ),
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8.r),
            ),
            child: Padding(
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
                        size:
                            orientation == Orientation.portrait ? 15.sp : 7.sp,
                        fontWeight: FontWeight.w500,
                      ),
                      SmallText(
                        text: '15-Nov-2024 - 15-Dec-2024',
                        size:
                            orientation == Orientation.portrait ? 13.sp : 6.sp,
                        color: BaseConfig.subTextColor,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
