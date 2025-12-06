import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/widgets/medium_text.dart';

void openBottomSheet({
  String? title,
  required void Function() onTabImageGallery,
  required void Function() onTabImageCamera,
}) {
  final borderRadius = Radius.circular(30.r);
  Get.bottomSheet(
    SizedBox(
      height: 130.h,
      width: Get.width,
      child: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(height: 10.h),
            if (title != null) ...[
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  MediumTextNotoSans(
                    text: title,
                    fontWeight: FontWeight.bold,
                  ),
                  IconButton(
                    onPressed: () => Get.back(),
                    icon: const Icon(
                      Icons.close,
                      color: BaseConfig.appThemeColor1,
                    ),
                  ),
                ],
              ).marginSymmetric(horizontal: 20.w),
              SizedBox(height: 10.h),
            ] else ...[
              Align(
                alignment: Alignment.topRight,
                child: IconButton(
                  onPressed: () => Get.back(),
                  icon: const Icon(
                    Icons.close,
                    color: BaseConfig.appThemeColor1,
                  ),
                ),
              ).marginSymmetric(horizontal: 20.w),
            ],
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                SizedBox(
                  width: 20.w,
                ),
                Expanded(
                  child: Card(
                    elevation: 3,
                    color: BaseConfig.appThemeColor1,
                    child: SizedBox(
                      height: 50.h,
                      width: 50.w,
                      child: Padding(
                        padding: EdgeInsets.all(8.w),
                        child: Icon(
                          Icons.photo_library,
                          color: BaseConfig.mainBackgroundColor,
                          size: 30.sp,
                        ),
                      ),
                    ).ripple(onTabImageGallery),
                  ),
                ),
                SizedBox(
                  width: 20.w,
                ),
                Expanded(
                  child: Card(
                    elevation: 3,
                    color: BaseConfig.appThemeColor1,
                    child: SizedBox(
                      height: 50.h,
                      width: 50.w,
                      child: Padding(
                        padding: EdgeInsets.all(8.w),
                        child: Icon(
                          Icons.photo_camera,
                          color: BaseConfig.mainBackgroundColor,
                          size: 30.sp,
                        ),
                      ),
                    ).ripple(onTabImageCamera),
                  ),
                ),
                SizedBox(
                  width: 20.w,
                ),
              ],
            ),
          ],
        ),
      ),
    ),
    backgroundColor: BaseConfig.mainBackgroundColor,
    elevation: 0,
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.only(
        topLeft: borderRadius,
        topRight: borderRadius,
      ),
    ),
  );
}
