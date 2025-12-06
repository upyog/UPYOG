import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/login_redirect_page.dart';

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
    // Perform your asynchronous operations here
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
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      ListTile(
                        title: BigTextNotoSans(
                          text: 'Trade License',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.TRADE_LICENSE_APPROVED);
                        },
                      ),
                      ListTile(
                        title: BigTextNotoSans(
                          text: 'Water',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.WATER_MY_CERTIFICATES);
                        },
                      ),
                      ListTile(
                        title: BigTextNotoSans(
                          text: 'Sewerage',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.SEWERAGE_MY_CERTIFICATES);
                        },
                      ),
                      ListTile(
                        title: BigTextNotoSans(
                          text: 'Property Tax',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.PROPERTY_MY_CERTIFICATES);
                        },
                      ),
                      ListTile(
                        title: BigTextNotoSans(
                          text: getLocalizedString(
                            i18.common.BUILDING_PLAN_APPROVAL,
                          ),
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        trailing: const Icon(Icons.east),
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
      ),
    );
  }
}
