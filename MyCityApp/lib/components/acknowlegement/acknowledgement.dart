import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class AcknowledgementWidget extends StatelessWidget {
  final String applicationNo;
  final Modules module;
  const AcknowledgementWidget({
    super.key,
    required this.applicationNo,
    this.module = Modules.COMMON,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        height: Get.height,
        width: Get.width,
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [
              BaseConfig.appThemeColor1,
              BaseConfig.appThemeColor2,
            ],
          ),
        ),
        child: Padding(
          padding: EdgeInsets.all(10.w),
          child: Center(
            child: SizedBox(
              height: Get.height / 2,
              child: Card(
                child: Padding(
                  padding: EdgeInsets.all(20.w),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Container(
                        height: 220.h,
                        width: Get.width,
                        padding: EdgeInsets.all(10.w),
                        decoration: BoxDecoration(
                          color: const Color(0xFF00703C),
                          borderRadius: BorderRadius.circular(20.r),
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const MediumTextNotoSans(
                              text:
                                  'New Building Plant Application Reviewed Successfully',
                              fontWeight: FontWeight.bold,
                              textAlign: TextAlign.center,
                              color: BaseConfig.mainBackgroundColor,
                            ),
                            SizedBox(
                              height: 20.h,
                            ),
                            Icon(
                              Icons.check_circle,
                              color: BaseConfig.mainBackgroundColor,
                              size: 50.sp,
                            ),
                            SizedBox(
                              height: 20.h,
                            ),
                            const SmallTextNotoSans(
                              text: 'New Building Plant Permit Application',
                              textAlign: TextAlign.center,
                              color: BaseConfig.mainBackgroundColor,
                            ),
                            SizedBox(
                              height: 10.h,
                            ),
                            MediumTextNotoSans(
                              text: applicationNo,
                              textAlign: TextAlign.center,
                              color: BaseConfig.mainBackgroundColor,
                              fontWeight: FontWeight.bold,
                            ),
                          ],
                        ),
                      ),
                      const Spacer(),
                      FilledButtonApp(
                        width: Get.width,
                        text: getLocalizedString(i18.common.GO_TO_HOME),
                        onPressed: () => Get.offAllNamed(AppRoutes.BOTTOM_NAV),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
