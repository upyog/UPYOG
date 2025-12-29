import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/login_redirect_page.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class HomeMyCertificates extends StatefulWidget {
  const HomeMyCertificates({super.key});

  @override
  State<HomeMyCertificates> createState() => _HomeMyCertificatesState();
}

class _HomeMyCertificatesState extends State<HomeMyCertificates> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();

  @override
  void initState() {
    super.initState();
    _fetchLabelsAsync();
  }

  Future<void> _fetchLabelsAsync() async {
    await _commonController.fetchLabels(modules: Modules.TL);
    await _commonController.fetchLabels(modules: Modules.WS);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () => Get.offAndToNamed(AppRoutes.BOTTOM_NAV),
        title: 'My Certificates',
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: !_authController.isValidUser
            ? loginRedirectPage()
            : SingleChildScrollView(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SizedBox(height: 16.h),
                    _listTile(
                      text: 'Trade License',
                      onTap: () {
                        Get.toNamed(AppRoutes.TRADE_LICENSE_APPROVED);
                      },
                    ),
                    _listTile(
                      text: 'Water',
                      onTap: () {
                        Get.toNamed(AppRoutes.WATER_MY_CERTIFICATES);
                      },
                    ),
                    _listTile(
                      text: 'Sewerage',
                      onTap: () {
                        Get.toNamed(AppRoutes.SEWERAGE_MY_CERTIFICATES);
                      },
                    ),
                    _listTile(
                      text: 'Property Tax',
                      onTap: () {
                        Get.toNamed(AppRoutes.PROPERTY_MY_CERTIFICATES);
                      },
                    ),
                    _listTile(
                      text: getLocalizedString(
                        i18.common.BUILDING_PLAN_APPROVAL,
                      ),
                      onTap: () {
                        Get.toNamed(
                          AppRoutes.BUILDING_PLAN_CERTIFICATE,
                        );
                      },
                    ),
                  ],
                ),
              ),
      ),
    );
  }

  Widget _listTile({
    required String text,
    required Function() onTap,
  }) =>
      Padding(
        padding: EdgeInsets.symmetric(horizontal: 16.w),
        child: ListTile(
          title: MediumSelectableTextNotoSans(
            text: text,
            fontWeight: FontWeight.w600,
            size: 16.sp,
          ),
          trailing: const Icon(Icons.east),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10.r),
          ),
          onTap: onTap,
        ),
      );
}
