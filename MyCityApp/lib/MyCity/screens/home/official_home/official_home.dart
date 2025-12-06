import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/widget/bottom_nav/bottom_nav.dart';
import 'package:mobile_app/MyCity/widget/category_icon_card/category_icon_card.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/MyCity/widget/latest_news/latest_news.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/widgets/big_text.dart';

class OfficialHomeScreen extends StatefulWidget {
  const OfficialHomeScreen({super.key});

  @override
  State<OfficialHomeScreen> createState() => _OfficialHomeScreenState();
}

class _OfficialHomeScreenState extends State<OfficialHomeScreen> {
  bool isCalender = false;

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      extendBody: true,
      appBar: MyCityHeader(
        title: isCalender ? 'Calender' : 'Official Home',
        isBackButton: isCalender,
        showBack: true,
        calenderOnPressed: () {
          setState(() {
            isCalender = !isCalender;
          });
        },
      ),
      body: SizedBox(
        height: Get.height,
        child: isCalender
            ? const CalenderHomeScreen()
            : Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  SizedBox(height: 10.h),
                  // _buildSearchBar(o).paddingSymmetric(
                  //   horizontal: 16.w,
                  // ),
                  SizedBox(height: 10.h),
                  const LatestNews(),
                  SizedBox(height: 50.h),
                  InterText(
                    text: "Select Service Option",
                    fontWeight: FontWeight.w800,
                    size: o == Orientation.portrait ? 16.sp : 8.sp,
                    color: BaseConfig.textColor2,
                  ).paddingSymmetric(
                    horizontal: 16.w,
                  ),
                  SizedBox(height: 15.h),
                  Row(
                    children: [
                      SizedBox(
                        width: 150.w,
                        height: 150.h,
                        child: CategoryIconCard(
                          onPressed: () =>
                              Get.toNamed(AppRoutes.ULB_SERVICE_MANAGEMENT),
                          title: 'ULB Service Management',
                          icon: BaseConfig.ulbServiceManagementIcon,
                        ),
                      ),
                      SizedBox(
                        width: 16.w,
                      ),
                      SizedBox(
                        width: 150.w,
                        height: 150.h,
                        child: CategoryIconCard(
                          onPressed: () =>
                              Get.toNamed(AppRoutes.REPORTS_AND_DASHBOARD),
                          title: 'Reports and Dashboard',
                          icon: BaseConfig.reportsAndDashboardIcon,
                        ),
                      ),
                    ],
                  ).paddingSymmetric(
                    horizontal: 16.w,
                  ),
                ],
              ),
      ),
      bottomNavigationBar: const BottomNav(),
    );
  }

  // Widget _buildSearchBar(Orientation o) {
  //   final radius = BorderRadius.circular(25.r);
  //   return Container(
  //     height: o == Orientation.portrait ? 52 : 60.h,
  //     decoration: BoxDecoration(
  //       borderRadius: radius,
  //       gradient: const LinearGradient(
  //         colors: [
  //           Color(0xfff3af81),
  //           Color(0xffbf4720),
  //         ],
  //         begin: Alignment.topLeft,
  //         end: Alignment.bottomRight,
  //       ),
  //     ),
  //     child: TextField(
  //       onTap: () {
  //         Get.toNamed(AppRoutes.HOME_GLOBAL_SEARCH);
  //       },
  //       style: const TextStyle(color: BaseConfig.mainBackgroundColor),
  //       cursorColor: BaseConfig.mainBackgroundColor,
  //       decoration: InputDecoration(
  //         hintText: getLocalizedString(i18.common.SEARCH),
  //         contentPadding:
  //             EdgeInsets.all(o == Orientation.portrait ? 16.w : 8.w),
  //         focusColor: BaseConfig.mainBackgroundColor,
  //         hintStyle: const TextStyle(color: BaseConfig.mainBackgroundColor),
  //         prefixIcon: Padding(
  //           padding: EdgeInsets.only(left: 6.w),
  //           child:
  //               const Icon(Icons.search, color: BaseConfig.mainBackgroundColor),
  //         ),
  //         suffixIcon: IconButton(
  //           onPressed: () {
  //             //TODO: Voice search
  //           },
  //           icon: Padding(
  //             padding: EdgeInsets.only(left: 12.w, right: 12.w),
  //             child: Icon(
  //               Icons.mic_none_outlined,
  //               color: BaseConfig.mainBackgroundColor.withValues(alpha:0.7),
  //             ),
  //           ),
  //         ),
  //         border: OutlineInputBorder(
  //           borderRadius: radius,
  //           borderSide: BorderSide.none,
  //         ),
  //         enabledBorder: OutlineInputBorder(
  //           borderRadius: radius,
  //           borderSide: const BorderSide(color: BaseConfig.mainBackgroundColor),
  //         ),
  //         focusedBorder: OutlineInputBorder(
  //           borderRadius: radius,
  //           borderSide: const BorderSide(color: BaseConfig.mainBackgroundColor),
  //         ),
  //       ),
  //     ),
  //   );
  // }
}
