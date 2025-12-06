import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class NoApplicationFoundWidget extends StatelessWidget {
  const NoApplicationFoundWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          SizedBox(
            height: Get.height * 0.3,
          ),
          const Icon(Icons.error_outline, size: 35, color: Colors.grey),
          MediumTextNotoSans(
            text: getLocalizedString(i18.common.NO_DATA),
            fontWeight: FontWeight.w700,
            color: Colors.grey,
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }
}
