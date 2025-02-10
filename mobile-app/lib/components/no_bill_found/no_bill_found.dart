import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class NoBillFoundWidget extends StatelessWidget {
  const NoBillFoundWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: EdgeInsets.only(top: Get.height * 0.1),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 35, color: Colors.grey),
            MediumTextNotoSans(
              text: getLocalizedString(
                i18.propertyTax.NO_BILLS_FOUND,
                module: Modules.PT,
              ),
              fontWeight: FontWeight.w700,
              color: Colors.grey,
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}
