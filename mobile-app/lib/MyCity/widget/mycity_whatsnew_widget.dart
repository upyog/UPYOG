import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/widgets/big_text.dart';

class MyCityWhatsNewWidget extends StatelessWidget {
  const MyCityWhatsNewWidget({
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
    final radius = Radius.circular(15.r);

    return Card(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(15.r),
      ),
      child: SizedBox(
        height: 200.h,
        child: Stack(
          children: [
            Container(
              height: 185.h,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.all(
                  radius,
                ),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.all(
                  radius,
                ),
                child: Image.asset(
                  img,
                  width: Get.width,
                  fit: BoxFit.fill,
                ),
              ),
            ),
            Positioned(
              top: 10.h,
              left: 0,
              right: 0,
              child: Align(
                alignment: Alignment.topCenter,
                child: InterText(
                  text: getLocalizedString(i18.common.WHATS_NEW),
                  fontWeight: FontWeight.w800,
                  size: orientation == Orientation.portrait ? 16.sp : 8.sp,
                  color: BaseConfig.textColor2,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
