import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/screens/citizen/bottom_navigation/bottom_navigation.dart';
import 'package:mobile_app/screens/employee/emp_bottom_navigation/emp_bottom_navigation.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:permission_handler/permission_handler.dart';

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
    checkStoragePermission();
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    super.initState();
    checkUserLoginData();
  }

  Future<void> checkUserLoginData() async {
    final languageCtrl = Get.find<LanguageController>();
    await languageCtrl.getLocalizationData();

    final commonCtrl = Get.find<CommonController>();
    await commonCtrl.fetchLabels();

    final userData = await storage.getString(SecureStorageConstants.LOGIN_KEY);
    final employeeUserData =
        await storage.getString(SecureStorageConstants.EMP_LOGIN_KEY);
    await goNextPage(userData, employeeUserData);
  }

  Future<void> goNextPage(user, employeeUser) async {
    if (user == null && employeeUser == null) {
      _isLoading.value = false;
      Get.offNamed(AppRoutes.SELECT_CITIZEN);
      return;
    }

    _isLoading.value = true;
    final userType =
        await storage.getString(Constants.USER_TYPE) ?? UserType.CITIZEN.name;
    Timer(const Duration(seconds: 2), () async {
      if (userType == UserType.CITIZEN.name) {
        bool isTrue =
            await storage.getBool(SecureStorageConstants.FIRST_TIME_USER) ??
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
              await storage.getBool(SecureStorageConstants.SKIP_BUTTON);
          if (isSkipped != null && isSkipped) {
            Get.offAll(
              () => const BottomNavigationPage(),
              transition: Transition.fade,
            );
          } else {
            Get.offNamed(AppRoutes.SELECT_CITIZEN);
            // Get.offNamed(AppRoutes.SELECT_CATEGORY);
          }
        }
      } else {
        if (employeeUser != null && employeeUser.isNotEmpty) {
          Get.offAll(
            () => const EmpBottomNavigationPage(),
            transition: Transition.fade,
          );
        } else {
          Get.offNamed(AppRoutes.SELECT_CITIZEN);
          // Get.offNamed(AppRoutes.SELECT_CATEGORY);
        }
      }
      _isLoading.value = false;
    });
  }

  Future<void> checkStoragePermission() async {
    await [
      Permission.storage,
      Permission.manageExternalStorage,
    ].request();
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
                  child: SafeArea(
                    bottom: true,
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
                          text:
                              'Powered by National Institute of Urban Affairs',
                          fontWeight: FontWeight.w500,
                          size: o == Orientation.portrait ? 12.sp : 5.sp,
                        ),
                      ],
                    ),
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
