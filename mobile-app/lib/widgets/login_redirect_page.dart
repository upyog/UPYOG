import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';

Widget loginRedirectPage() {
  return Center(
    child: FilledButtonApp(
      text: getLocalizedString(i18.common.LOGIN),
      // onPressed: () => Get.offAllNamed(AppRoutes.SELECT_CATEGORY),
      onPressed: () => Get.offAllNamed(AppRoutes.SELECT_CITIZEN),
    ),
  );
}
