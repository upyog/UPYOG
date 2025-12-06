import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/bottom_navigation/bottom_navigation.dart';
import 'package:mobile_app/screens/employee/emp_bottom_navigation/emp_bottom_navigation.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/widgets/medium_text.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  final _authController = Get.find<AuthController>();

  final RxBool _isLoading = false.obs;

  @override
  void initState() {
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    super.initState();
    checkUserLoginData();
  }

  checkUserLoginData() async {
    var userData = await HiveService.getData(HiveConstants.LOGIN_KEY);
    var employeeUserData =
        await HiveService.getData(HiveConstants.EMP_LOGIN_KEY);
    goNextPage(userData, employeeUserData);
  }

  goNextPage(user, employeeUser) async {
    _isLoading.value = true;
    final userType =
        await HiveService.getData(Constants.USER_TYPE) ?? UserType.CITIZEN.name;
    Timer(const Duration(seconds: 2), () async {
      if (userType == UserType.CITIZEN.name) {
        bool isTrue =
            await HiveService.getData(HiveConstants.FIRST_TIME_USER) as bool? ??
                false;
        if (user != null &&
            user.isNotEmpty &&
            isTrue &&
            _authController.userType != null) {
          Get.offAll(
            () => const BottomNavigationPage(),
            transition: Transition.fade,
          );
        } else {
          final isSkipped =
              await HiveService.getData(HiveConstants.SKIP_BUTTON);
          if (isSkipped != null && isSkipped) {
            Get.offAll(
              () => const BottomNavigationPage(),
              transition: Transition.fade,
            );
          } else {
            // Get.offNamed(AppRoutes.SELECT_CITIZEN);
            Get.offNamed(AppRoutes.SELECT_CATEGORY);
          }
        }
      } else {
        if (employeeUser != null && employeeUser.isNotEmpty) {
          Get.offAll(
            () => const EmpBottomNavigationPage(),
            transition: Transition.fade,
          );
        } else {
          // Get.offNamed(AppRoutes.SELECT_CITIZEN);
          Get.offNamed(AppRoutes.SELECT_CATEGORY);
        }
      }
      _isLoading.value = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.dark,
        statusBarBrightness: Brightness.dark,
      ),
    );
    return Scaffold(
      backgroundColor: BaseConfig.splashBackColor,
      body: OrientationBuilder(
        builder: (context, o) {
          return SizedBox(
            height: MediaQuery.sizeOf(context).height,
            width: MediaQuery.sizeOf(context).width,
            child: Stack(
              alignment: Alignment.center,
              children: [
                Positioned(
                  bottom: 0,
                  left: 0,
                  right: 0,
                  child: Image.asset(
                    BaseConfig.splashBuildingPng,
                    fit: BoxFit.fill,
                  ),
                ),
                Padding(
                  padding: EdgeInsets.symmetric(vertical: 24.h),
                  child: Column(
                    children: [
                      Image.asset(
                        BaseConfig.MINISTRY_SPLASH_LOGO,
                        fit: BoxFit.fill,
                        width: o == Orientation.portrait ? 175.w : 80.w,
                        height: 116.h,
                      ),
                      const Spacer(),
                      Image.asset(
                        BaseConfig.UPYOG_LOGO,
                        fit: BoxFit.contain,
                        width: o == Orientation.portrait ? 173.w : 100.w,
                        height: o == Orientation.portrait ? 72.h : 62.h,
                      ),
                      const Spacer(),
                      Image.asset(
                        BaseConfig.SPLASH_BOTTOM_NIUA_LOGO,
                        width: o == Orientation.portrait ? 68.w : 30.w,
                        height: 30.h,
                        fit: BoxFit.cover,
                      ),
                      SizedBox(height: 8.h),
                      MediumText(
                        text: 'Powered by National Institute of Urban Affairs',
                        fontWeight: FontWeight.w500,
                        size: o == Orientation.portrait ? 12.sp : 5.sp,
                      ),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
