import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class FilterBottomSheet extends StatelessWidget {
  final String title;
  final Widget content;
  final VoidCallback onApply;
  final VoidCallback onCancel;
  final bool isDismissible;
  final bool enableDrag;
  final Orientation orientation;

  const FilterBottomSheet({
    super.key,
    required this.title,
    required this.content,
    required this.onApply,
    required this.onCancel,
    this.isDismissible = true,
    this.enableDrag = true,
    required this.orientation,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: orientation == Orientation.portrait
          ? Get.height * 0.6
          : Get.height * 0.9,
      decoration: BoxDecoration(
        color: BaseConfig.mainBackgroundColor,
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(20.r),
          topRight: Radius.circular(20.r),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: 16),
          Row(
            children: [
              IconButton(
                onPressed: onCancel,
                icon: const Icon(
                  Icons.chevron_left,
                  size: 28,
                ),
              ),
              SizedBox(width: orientation == Orientation.portrait ? 8.w : 1.w),
              MediumTextNotoSans(
                text: title.capitalize!,
                size: orientation == Orientation.portrait ? 16.sp : 8.sp,
                fontWeight: FontWeight.w500,
              ),
            ],
          ),
          SizedBox(height: 8.h),
          const Divider(),
          Expanded(
            child: SingleChildScrollView(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(height: 16.h),
                  content,
                ],
              ),
            ),
          ),
          SizedBox(height: 16.h),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(
                child: FilledButtonApp(
                  text: getLocalizedString(i18.common.CANCEL),
                  textColor: BaseConfig.appThemeColor1,
                  onPressed: onCancel,
                  backgroundColor: BaseConfig.mainBackgroundColor,
                  fontSize: orientation == Orientation.portrait ? 14.sp : 7.sp,
                  fontWeight: FontWeight.w600,
                  side: const BorderSide(
                    color: BaseConfig.appThemeColor1,
                    width: 1.5,
                  ),
                ),
              ),
              SizedBox(width: 16.w),
              Expanded(
                child: FilledButtonApp(
                  fontSize: orientation == Orientation.portrait ? 14.sp : 7.sp,
                  text: getLocalizedString(i18.common.APPLY),
                  onPressed: onApply,
                ),
              ),
            ],
          ),
          SizedBox(height: 16.h),
        ],
      ).paddingSymmetric(horizontal: 16.w),
    );
  }
}
