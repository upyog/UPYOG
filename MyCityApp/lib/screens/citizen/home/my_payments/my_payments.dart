import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/login_redirect_page.dart';
import 'package:mobile_app/widgets/small_text.dart';

class HomeMyPayments extends StatefulWidget {
  const HomeMyPayments({super.key});

  @override
  State<HomeMyPayments> createState() => _HomeMyPaymentsState();
}

class _HomeMyPaymentsState extends State<HomeMyPayments> {
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();

  @override
  void initState() {
    super.initState();
    _fetchLabelsAsync();
  }

  Future<void> _fetchLabelsAsync() async {
    await _commonController.fetchLabels(modules: Modules.PT);
    await _commonController.fetchLabels(modules: Modules.WS);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        onPressed: () {
          Get.offAndToNamed(AppRoutes.BOTTOM_NAV);
        },
        title: 'My Payments',
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
                          text: 'Property Tax',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        subtitle: const SmallTextNotoSans(
                          text: 'My Payments',
                          fontWeight: FontWeight.w400,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.PROPERTY_MY_PAYMENT_SCREEN);
                        },
                      ),
                      ListTile(
                        title: BigTextNotoSans(
                          text: 'Water',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        subtitle: const SmallTextNotoSans(
                          text: 'My Payments',
                          fontWeight: FontWeight.w400,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.WATER_MY_PAYMENT);
                        },
                      ),
                      ListTile(
                        title: BigTextNotoSans(
                          text: 'Sewerage',
                          fontWeight: FontWeight.w600,
                          size: 16.sp,
                        ),
                        subtitle: const SmallTextNotoSans(
                          text: 'My Payments',
                          fontWeight: FontWeight.w400,
                        ),
                        trailing: const Icon(Icons.east),
                        onTap: () {
                          Get.toNamed(AppRoutes.SEWERAGE_MY_PAYMENT);
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
