import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';

class ComplainCardBilling extends StatelessWidget {
  const ComplainCardBilling({
    super.key,
    required this.title,
    required this.id,
    required this.onTap,
    required this.status,
    required this.statusColor,
    required this.statusBackColor,
    this.rating,
    this.address,
    this.ownerName,
    this.billingCycle,
    this.dueDate,
    this.overDueDate,
    this.isNewComment = false,
    this.o = Orientation.portrait,
  });

  final String title;
  final String id;
  final String? ownerName;
  final String? billingCycle;
  final String? dueDate;
  final String? overDueDate;
  final String status;
  final String? address;
  final void Function() onTap;
  final Color statusColor;
  final Color statusBackColor;
  final int? rating;
  final bool isNewComment;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: Get.width,
      padding: EdgeInsets.only(bottom: 12.h),
      child: Card(
        elevation: 3,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12.r),
          side: BorderSide(
            color: BaseConfig.borderColor,
            width: 1.w,
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SizedBox(
                          width: Get.width * 0.5,
                          child: Tooltip(
                            message: title,
                            child: MediumTextNotoSans(
                              text: title,
                              fontWeight: FontWeight.w700,
                              size: o == Orientation.portrait ? 14.sp : 8.sp,
                              maxLine: 2,
                              textOverflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ),
                        SmallSelectableTextNotoSans(
                          text:
                              '( ${getLocalizedString(i18.propertyTax.PAYMENT_OVERDUE, module: Modules.PT)} $overDueDate ${getLocalizedString(i18.propertyTax.COMMON_DAYS, module: Modules.PT)})',
                          color: BaseConfig.redColor1,
                          fontWeight: FontWeight.w400,
                          size: o == Orientation.portrait ? 12.sp : 7.sp,
                        ),
                      ],
                    ),
                    TextButtonNotoSans(
                      text: 'View Details',
                      padding:
                          EdgeInsets.symmetric(horizontal: 4.w, vertical: 2.h),
                      fontSize: o == Orientation.portrait ? 12.sp : 7.sp,
                      onPressed: onTap,
                      icon: const Icon(
                        Icons.chevron_right,
                        color: BaseConfig.appThemeColor1,
                      ),
                    ),
                  ],
                ),
                const Divider(
                  color: BaseConfig.borderColor,
                ),
                SizedBox(height: 4.h),
                SmallSelectableTextNotoSans(
                  text: id,
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 12.sp : 7.sp,
                ),
                SizedBox(height: 4.h),
                if (ownerName != null)
                  SmallSelectableTextNotoSans(
                    text: ownerName ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                SizedBox(height: 4.h),
                if (address != null)
                  SmallSelectableTextNotoSans(
                    text: address ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                SizedBox(height: 4.h),
                if (billingCycle != null)
                  SmallSelectableTextNotoSans(
                    text: billingCycle ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                SizedBox(height: 4.h),
                if (dueDate != null)
                  SmallSelectableTextNotoSans(
                    text: dueDate ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
              ],
            ).paddingAll(o == Orientation.portrait ? 16.w : 8.w),
            Container(
              height: 45.h,
              width: Get.width,
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: statusBackColor,
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(12.r),
                  bottomRight: Radius.circular(12.r),
                ),
              ),
              child: MediumText(
                text: status,
                color: statusColor,
                fontWeight: FontWeight.w600,
                size: o == Orientation.portrait ? 14.sp : 8.sp,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
