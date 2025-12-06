import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:photo_view/photo_view.dart';
import 'package:syncfusion_flutter_pdfviewer/pdfviewer.dart';

showTypeDialogue(
  context, {
  required String url,
  required String title,
  bool isPdf = false,
  double height = 0.6,
}) {
  return showAdaptiveDialog(
    context: context,
    builder: (context) => AlertDialog(
      backgroundColor: Theme.of(context).colorScheme.surface,
      surfaceTintColor: Theme.of(context).colorScheme.surface,
      scrollable: false,
      title: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: MediumTextNotoSans(
              text: title,
              fontWeight: FontWeight.w600,
              size: 16.sp,
            ),
          ),
          IconButton(
            icon: const Icon(
              Icons.close,
              color: BaseConfig.appThemeColor1,
            ),
            onPressed: () => Navigator.of(context).pop(),
          ),
        ],
      ),
      content: SizedBox(
        width: Get.width,
        height: Get.height * height,
        child: isPdf
            ? SfPdfViewer.network(
                url,
              )
            : PhotoView(
                loadingBuilder: (context, event) => showCircularIndicator(),
                backgroundDecoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surface,
                  borderRadius: BorderRadius.circular(10),
                ),
                imageProvider: NetworkImage(
                  url,
                ),
              ),
      ),
    ),
  );
}
