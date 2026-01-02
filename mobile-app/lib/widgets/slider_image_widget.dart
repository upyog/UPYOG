import 'package:flutter/material.dart';
import 'package:flutter_image_slideshow/flutter_image_slideshow.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';

class SliderImageWidget extends StatelessWidget {
  const SliderImageWidget({super.key, this.height});
  final double? height;

  @override
  Widget build(BuildContext context) {
    return ImageSlideshow(
      width: 360.w,
      height: height ?? 330.h,
      initialPage: 0,
      indicatorColor: Colors.white,
      indicatorBackgroundColor: Colors.white,
      onPageChanged: (value) {
        // dPrint('Page changed: $value');
      },
      autoPlayInterval: 3000,
      isLoop: true,
      children: [
        //for (int i = 0; i < BaseConfig.APP_HOME_BANNERS.split(',').length; i++)
        Image.asset(
          BaseConfig.newloginBuildingPng,
          width: Get.width,
          fit: BoxFit.fill,
        ),
      ],
    );
  }
}

class SliderImagePortraitWidget extends StatelessWidget {
  const SliderImagePortraitWidget({super.key, this.height, this.width});
  final double? height;
  final double? width;

  @override
  Widget build(BuildContext context) {
    return ImageSlideshow(
      width: width ?? 160.w,
      height: height ?? Get.height,
      initialPage: 0,
      indicatorColor: Colors.white,
      indicatorBackgroundColor: Colors.white,
      onPageChanged: (value) {},
      autoPlayInterval: 3000,
      isLoop: true,
      children: [
        Image.asset(
          BaseConfig.newloginBuildingPng,
          width: Get.width,
          fit: BoxFit.fill,
        ),
      ],
    );
  }
}
