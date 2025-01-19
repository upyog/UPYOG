import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/rating_bar_widget.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';

class ComplainCard extends StatelessWidget {
  const ComplainCard({
    super.key,
    required this.title,
    required this.id,
    required this.date,
    required this.onTap,
    required this.status,
    required this.statusColor,
    required this.statusBackColor,
    this.rating,
    this.address,
    this.sla,
    this.ulbOfficial,
    this.viewDetails,
    this.isNewComment = false,
    this.o = Orientation.portrait,
    this.subtext,
    this.name,
    this.date2,
    this.isShowViewDetails = true,
  });

  final String title;
  final String id;
  final String date;
  final String status;
  final String? address, sla, ulbOfficial, viewDetails, subtext, name, date2;
  final void Function() onTap;
  final Color statusColor;
  final Color statusBackColor;
  final int? rating;
  final bool isNewComment, isShowViewDetails;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: Get.width,
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
                    SizedBox(
                      width: Get.width * 0.4,
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
                    if (isShowViewDetails)
                      TextButtonNotoSans(
                        text: viewDetails ??
                            getLocalizedString(i18.common.VIEWDETAILS),
                        padding: EdgeInsets.symmetric(
                          horizontal: 4.w,
                          vertical: 2.h,
                        ),
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
                SmallTextNotoSans(
                  text: id,
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 12.sp : 7.sp,
                ),
                if (subtext != null) ...[
                  SizedBox(height: 4.h),
                  SmallTextNotoSans(
                    text: subtext ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                ],
                if (name != null) ...[
                  SizedBox(height: 4.h),
                  SmallTextNotoSans(
                    text: name ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                ],
                if (date2 != null) ...[
                  SizedBox(height: 4.h),
                  SmallTextNotoSans(
                    text: date2!,
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                ],
                SizedBox(height: 4.h),
                SmallTextNotoSans(
                  text: date,
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 12.sp : 7.sp,
                ),

                if (address != null) ...[
                  SizedBox(height: 4.h),
                  SmallTextNotoSans(
                    text: address ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                ],

                if (ulbOfficial != null) ...[
                  SizedBox(height: 4.h),
                  SmallTextNotoSans(
                    text: ulbOfficial ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                ],
                if (sla != null) ...[
                  SizedBox(height: 4.h),
                  SmallTextNotoSans(
                    text: sla ?? '',
                    fontWeight: FontWeight.w400,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                ],

                // SizedBox(height: 4.h),
                if (rating != null && rating != 0)
                  Row(
                    children: [
                      SizedBox(height: 4.h),
                      SmallTextNotoSans(
                        text: 'Rate:',
                        fontWeight: FontWeight.w400,
                        size: o == Orientation.portrait ? 12.sp : 7.sp,
                      ),
                      SizedBox(width: 8.w),
                      RatingBarApp(
                        rating: rating!,
                        size: o == Orientation.portrait ? 24 : 14,
                        onRatingUpdate: (rating) {
                          dPrint('Rating: $rating');
                        },
                      ),
                    ],
                  ),
                if (isNewComment) ...[
                  Row(
                    children: [
                      SizedBox(height: 8.h),
                      Icon(
                        Icons.fiber_manual_record,
                        size: 14,
                        color: statusColor,
                      ),
                      SizedBox(width: 8.w),
                      SmallTextNotoSans(
                        text: 'You have a new comment from Lorem',
                        fontWeight: FontWeight.w500,
                        size: o == Orientation.portrait ? 12.sp : 7.sp,
                      ),
                    ],
                  ),
                  SizedBox(height: 12.h),
                ],
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
                textOverflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
