import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filter/filter_bottomsheet.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';

void openFilterBottomSheet({
  required String title,
  String? actionText,
  required Function() onApply,
  Orientation o = Orientation.portrait,
  List<Widget>? children,
}) {
  Get.bottomSheet(
    StatefulBuilder(
      builder: (context, setState) {
        return FilterBottomSheet(
          title: title,
          actionText: actionText,
          content: SingleChildScrollView(
            physics: AppPlatforms.platformPhysics(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              spacing: 8,
              children: [
                ...children ?? [],
              ],
            ),
          ),
          onApply: onApply,
          onCancel: () {
            Get.back();
          },
          orientation: o,
        );
      },
    ),
    isScrollControlled: true,
    isDismissible: false,
    enableDrag: false,
  );
}
