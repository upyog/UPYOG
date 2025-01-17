import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/locality_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class AssignedFilter extends StatelessWidget {
  const AssignedFilter({super.key});

  @override
  Widget build(BuildContext context) {
    final authController = Get.find<AuthController>();
    final localityController = Get.find<LocalityController>();

    return Obx(
      () => SizedBox(
        height: 120.h,
        child: ListView(
          shrinkWrap: true,
          padding: EdgeInsets.zero,
          children: [
            RadioListTile<String>(
              title: MediumTextNotoSans(
                text: getLocalizedString(i18.inbox.ASSIGNED_TO_ME),
                fontWeight: FontWeight.w400,
                size: 14.sp,
              ),
              contentPadding: EdgeInsetsDirectional.zero,
              value: authController.token?.userRequest?.uuid ?? "",
              groupValue: localityController.assigneeUid.value.isEmpty
                  ? ""
                  : localityController.assigneeUid.value,
              activeColor: BaseConfig.appThemeColor1,
              onChanged: (value) async {
                if (value != null) {
                  localityController.assigneeUid.value =
                      authController.token?.userRequest?.uuid ?? "";
                  dPrint(
                    "Assigned to me: ${localityController.assigneeUid.value}",
                  );
                }
              },
            ),
            RadioListTile<String>(
              title: MediumTextNotoSans(
                text: getLocalizedString(i18.inbox.ASSIGNED_TO_ALL),
                fontWeight: FontWeight.w400,
                size: 14.sp,
              ),
              value: "",
              contentPadding: EdgeInsetsDirectional.zero,
              groupValue: localityController.assigneeUid.value.isEmpty
                  ? ""
                  : localityController.assigneeUid.value,
              activeColor: BaseConfig.appThemeColor1,
              onChanged: (value) async {
                if (value != null) {
                  localityController.assigneeUid.value = "";
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}
