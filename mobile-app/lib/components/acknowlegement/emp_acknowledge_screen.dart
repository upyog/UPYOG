import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpAcknowledgeScreen extends StatelessWidget {
  final String applicationNo;
  final String mainTitle;
  final String? subTitle;
  final String appIdName;
  final String message;
  const EmpAcknowledgeScreen({
    super.key,
    required this.mainTitle,
    this.subTitle,
    required this.appIdName,
    required this.applicationNo,
    required this.message,
  });

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
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
                height: Get.height / 1.9,
                child: Card(
                  child: Padding(
                    padding: EdgeInsets.all(20.w),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Container(
                          width: Get.width,
                          padding: EdgeInsets.all(10.w),
                          decoration: BoxDecoration(
                            color: BaseConfig.statusGreenColor,
                            borderRadius: BorderRadius.circular(20.r),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              MediumSelectableTextNotoSans(
                                text: mainTitle,
                                fontWeight: FontWeight.bold,
                                textAlign: TextAlign.center,
                                color: BaseConfig.mainBackgroundColor,
                              ),
                              if (subTitle != null) ...[
                                SizedBox(
                                  height: 10.h,
                                ),
                                SmallSelectableTextNotoSans(
                                  text: subTitle!,
                                  textAlign: TextAlign.center,
                                  color: BaseConfig.mainBackgroundColor,
                                ),
                              ],
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
                              SmallSelectableTextNotoSans(
                                text: appIdName,
                                textAlign: TextAlign.center,
                                color: BaseConfig.mainBackgroundColor,
                              ),
                              SizedBox(
                                height: 10.h,
                              ),
                              MediumSelectableTextNotoSans(
                                text: applicationNo,
                                textAlign: TextAlign.center,
                                color: BaseConfig.mainBackgroundColor,
                                fontWeight: FontWeight.bold,
                              ),
                            ],
                          ),
                        ),
                        const Spacer(),
                        Container(
                          padding: EdgeInsets.all(10.w),
                          decoration: BoxDecoration(
                            color: BaseConfig.statusResolvedBackColor,
                            borderRadius: BorderRadius.circular(8.r),
                          ),
                          child: SmallSelectableTextNotoSans(
                            text: message,
                            textAlign: TextAlign.center,
                            // color: BaseConfig.mainBackgroundColor,
                          ),
                        ),
                        const Spacer(),
                        FilledButtonApp(
                          width: Get.width,
                          text: getLocalizedString(i18.common.GO_TO_HOME),
                          onPressed: () =>
                              Get.offAllNamed(AppRoutes.EMP_BOTTOM_NAV),
                        ),
                      ],
                    ),
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
