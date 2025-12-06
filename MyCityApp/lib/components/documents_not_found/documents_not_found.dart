import 'package:flutter/widgets.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class DocumentsNotFound extends StatelessWidget {
  const DocumentsNotFound({super.key, this.module = Modules.COMMON});

  final Modules module;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: MediumTextNotoSans(
        text: getLocalizedString(
          i18.propertyTax.NO_DOCUMENTS_FOUND,
          module: module,
        ),
      ).paddingSymmetric(horizontal: 10.w),
    );
  }
}
