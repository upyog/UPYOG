import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';

class NavBar extends StatefulWidget {
  final int pageIndex;
  final Function(int) onTap;

  const NavBar({
    super.key,
    required this.pageIndex,
    required this.onTap,
  });

  @override
  State<NavBar> createState() => _NavBarState();
}

class _NavBarState extends State<NavBar> {
  final cityController = Get.find<CityController>();
  final _authController = Get.find<AuthController>();

  @override
  void initState() {
    super.initState();

    if (!_authController.isValidUser) return;
    cityController.fetchSelectedCity();
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return SizedBox(
          height: o == Orientation.portrait
              ? Platform.isIOS
                  ? 88.h
                  : 75.h
              : Platform.isIOS
                  ? 88.h
                  : 98.h,
          child: Obx(() {
            return BottomNavigationBar(
              currentIndex: widget.pageIndex,
              onTap: widget.onTap,
              type: BottomNavigationBarType.fixed,
              showSelectedLabels: true,
              showUnselectedLabels: true,
              backgroundColor: BaseConfig.shadeAmber,
              selectedItemColor: BaseConfig.appThemeColor1,
              unselectedItemColor: BaseConfig.appThemeColor1,
              selectedLabelStyle: GoogleFonts.notoSans().copyWith(
                    fontWeight: FontWeight.bold,
                  ),
              items: [
                BottomNavigationBarItem(
                  icon: Icon(
                    Icons.home_outlined,
                    size: 24.h,
                  ),
                  label: getLocalizedString(i18.common.BOTTOM_HOME),
                ),
                BottomNavigationBarItem(
                  icon: Icon(
                    Icons.my_location_outlined,
                    size: 24.h,
                  ),
                  label: cityController.selectedCity.value.isEmpty
                      ? 'Location'
                      : cityController.selectedCity.value == 'pg'
                          ? getLocalizedString(
                              '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}'
                                  .toUpperCase(),
                            )
                          : getLocalizedString(
                              '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${cityController.selectedCity.value.split('.').last}'
                                  .toUpperCase(),
                            ),
                ),
                BottomNavigationBarItem(
                  icon: Icon(
                    Icons.card_giftcard_outlined,
                    size: 24.h,
                  ),
                  label: 'Certificates',
                ),
                BottomNavigationBarItem(
                  icon: Icon(
                    Icons.menu_outlined,
                    size: 24.h,
                  ),
                  label: 'Menu',
                ),
              ],
            );
          }),
        );
      },
    );
  }
}
