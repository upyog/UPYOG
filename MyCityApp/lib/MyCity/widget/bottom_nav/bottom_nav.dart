import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/routes/routes.dart';

class BottomNav extends StatelessWidget {
  const BottomNav({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.all(10.w),
      decoration: BoxDecoration(
        color: BaseConfig.mainBackgroundColor,
        borderRadius: BorderRadius.circular(20.r),
        border: Border.all(width: 2, color: Colors.indigo),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(20.r),
        child: Stack(
          clipBehavior: Clip.none,
          children: [
            BottomNavigationBar(
              backgroundColor: BaseConfig.mainBackgroundColor,
              elevation: 0,
              type: BottomNavigationBarType.fixed,
              items: [
                BottomNavigationBarItem(
                  icon: Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(10.r),
                      border: Border.all(width: 2, color: Colors.black),
                    ),
                    child: Image.asset(
                      BaseConfig.sosIcon,
                      height: 24.h,
                      width: 24.w,
                    ),
                  ),
                  label: 'Emergency',
                ),
                const BottomNavigationBarItem(
                  icon: SizedBox(),
                  label: '',
                ),
                BottomNavigationBarItem(
                  icon: Container(
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(10.r),
                      border: Border.all(width: 2, color: Colors.black),
                    ),
                    child: Image.asset(
                      BaseConfig.homeIcon,
                      height: 24.h,
                      width: 24.w,
                    ),
                  ),
                  label: 'Home',
                ),
              ],
              onTap: (index) {
                switch (index) {
                  case 0:
                    // Navigate to Emergency
                    break;
                  case 1:
                    // Navigate to Grid page
                    break;
                  case 2:
                    // Navigate to Home
                    Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
                    break;
                }
              },
            ),
            Positioned.fill(
              top: 12,
              child: Align(
                alignment: Alignment.topCenter,
                child: Material(
                  color: Colors.transparent,
                  child: InkWell(
                    customBorder: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(50.r),
                    ),
                    onTap: () {},
                    child: Container(
                      padding: const EdgeInsets.all(10),
                      decoration: BoxDecoration(
                        color: Colors.transparent,
                        borderRadius: BorderRadius.circular(50.r),
                        border: Border.all(width: 2, color: Colors.indigo),
                      ),
                      child: Image.asset(
                        BaseConfig.gridIcon,
                        height: 30.h,
                        width: 30.w,
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
