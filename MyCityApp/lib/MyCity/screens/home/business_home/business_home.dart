import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/ulb_service_management/ulb_service_managment.dart';
import 'package:mobile_app/MyCity/widget/bottom_nav/bottom_nav.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/MyCity/widget/latest_news/latest_news.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:url_launcher/url_launcher.dart';

class BusinessHome extends StatefulWidget {
  const BusinessHome({super.key});

  @override
  State<BusinessHome> createState() => _BusinessHomeState();
}

class _BusinessHomeState extends State<BusinessHome> {
  final List<TextModel> businessCards = [
    TextModel(title: 'About the City', icon: BaseConfig.aboutIcon),
    TextModel(
      title: 'Report 311 Civic Issues',
      icon: BaseConfig.reportGrievanceIcon,
    ),
    TextModel(title: 'Apply for Permits', icon: BaseConfig.permitsIcon),
    TextModel(
      title: 'Utilities & Related Services',
      icon: BaseConfig.utilitiesIcon,
    ),
    TextModel(
      title: ' Enroll as Service Provider',
      icon: BaseConfig.updatesHubIcon,
    ),
    TextModel(
      title: 'Event Spotlight',
      icon: BaseConfig.bookingIcon,
    ),
    TextModel(title: 'Know Your Government', icon: BaseConfig.govtIcon),
    TextModel(
      title: ' Business Analytics',
      icon: BaseConfig.publicAnalyticsIcon,
    ),
  ];

  bool isCalender = false;

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;

    return Scaffold(
      extendBody: true,
      appBar: MyCityHeader(
        title: isCalender ? 'Calender' : 'Business Home',
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
                  // Container(
                  //   decoration: BoxDecoration(
                  //     borderRadius: BorderRadius.circular(30),
                  //     gradient: const LinearGradient(
                  //       colors: [
                  //         Color(0xfff3af81),
                  //         Color(0xffbf4720),
                  //       ],
                  //       begin: Alignment.topLeft,
                  //       end: Alignment.bottomRight,
                  //     ),
                  //   ),
                  //   child: textFormFieldNormal(
                  //     context,
                  //     'Search',
                  //     isFilled: false,
                  //     hintTextColor: BaseConfig.mainBackgroundColor,
                  //     cursorColor: BaseConfig.mainBackgroundColor,
                  //     borderRadius: BorderRadius.all(Radius.circular(30.r)),
                  //     focusBorderColor: BaseConfig.textColor,
                  //     prefixIcon: const Icon(
                  //       Icons.search,
                  //       color: BaseConfig.mainBackgroundColor,
                  //     ),
                  //     suffixIcon: IconButton(
                  //       onPressed: () {},
                  //       icon: Padding(
                  //         padding: EdgeInsets.only(left: 12.w, right: 12.w),
                  //         child: Icon(
                  //           Icons.mic_none_outlined,
                  //           color:
                  //               BaseConfig.mainBackgroundColor.withValues(alpha:0.7),
                  //         ),
                  //       ),
                  //     ),
                  //   ),
                  // ).paddingSymmetric(
                  //   horizontal: 16.w,
                  // ),
                  SizedBox(height: 10.h),
                  const LatestNews(),
                  SizedBox(height: 30.h),
                  Expanded(child: categoryServices(o)),
                ],
              ),
      ),
      bottomNavigationBar: const BottomNav(),
    );
  }

  Widget categoryServices(Orientation o) {
    return SingleChildScrollView(
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 16.w),
        child: LayoutBuilder(
          builder: (context, constraints) {
            final isPortrait = o == Orientation.portrait;
            final width = constraints.maxWidth;
            final crossAxisCount = isPortrait ? 4 : 6;
            final crossAxisSpacing = isPortrait ? 25.w : 10.w;
            final childAspectRatio =
                width / (crossAxisCount * 1.7 * width / crossAxisCount);
            return GridView.builder(
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: crossAxisCount,
                crossAxisSpacing: crossAxisSpacing,
                mainAxisSpacing: 10.h,
                childAspectRatio: childAspectRatio,
              ),
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: businessCards.length,
              itemBuilder: (context, index) {
                final service = businessCards[index];
                return Tooltip(
                  message: service.title,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      ElevatedButton(
                        style: ButtonStyle(
                          backgroundColor: WidgetStateProperty.all(
                            BaseConfig.mainBackgroundColor,
                          ),
                          padding: WidgetStateProperty.all(
                            EdgeInsets.all(12.w),
                          ),
                          shape: WidgetStateProperty.all(
                            RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12.r),
                            ),
                          ),
                          side: WidgetStateProperty.resolveWith(
                            (states) {
                              if (states.contains(WidgetState.pressed)) {
                                return const BorderSide(
                                  color: BaseConfig.appThemeColor1,
                                  width: 2,
                                );
                              }
                              return const BorderSide(
                                color: BaseConfig.textColor,
                                width: 2,
                              );
                            },
                          ),
                        ),
                        onPressed: () async {
                          dPrint(service.title);
                          // Get.toNamed(AppRoutes.EMP_LOGIN);
                          const url =
                              "http://15.207.2.81/upyogdesign/business.html";

                          await launchURL(
                            url,
                            mode: LaunchMode.externalApplication,
                          );
                        },
                        child: service.icon != null
                            ? Image.asset(
                                service.icon!,
                                height: 40.h,
                                width: 40.w,
                                fit: BoxFit.fill,
                              )
                            : service.iconData != null
                                ? Icon(
                                    service.iconData,
                                    size: 40.sp,
                                    color: BaseConfig.mainBackgroundColor,
                                  )
                                : const SizedBox.shrink(),
                      ),
                      SizedBox(height: 4.h),
                      SmallTextNotoSans(
                        text: service.title,
                        size: isPortrait ? 10.sp : 7.sp,
                        maxLine: 2,
                        fontWeight: FontWeight.w600,
                        textAlign: TextAlign.center,
                        textOverflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                );
              },
            );
          },
        ),
      ),
    );
  }
}
