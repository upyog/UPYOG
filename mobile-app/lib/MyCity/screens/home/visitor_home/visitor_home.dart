import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/ulb_service_management/ulb_service_managment.dart';
import 'package:mobile_app/MyCity/widget/custom_elevate_btn.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/MyCity/widget/latest_news/latest_news.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/small_text.dart';
// import 'package:badges/badges.dart' as badges;

class VisitorHomeScreen extends StatefulWidget {
  const VisitorHomeScreen({super.key});

  @override
  State<VisitorHomeScreen> createState() => _VisitorHomeScreenState();
}

class _VisitorHomeScreenState extends State<VisitorHomeScreen> {
  bool isCalender = false;

  final List<TextModel> visitorsCards = [
    TextModel(title: 'About the City', icon: BaseConfig.aboutCityIcon),
    TextModel(title: 'Report Grievance', icon: BaseConfig.reportGrievanceIcon),
    TextModel(title: 'Bookings', icon: BaseConfig.bookingIcon),
    TextModel(
      title: 'Updates Hub',
      icon: BaseConfig.updatesHubIcon,
      isNew: true,
    ),
    TextModel(title: 'Places', icon: BaseConfig.placesIcon),
    TextModel(
      title: 'Parking and Navigation',
      icon: BaseConfig.parkingNavigationIcon,
    ),
    TextModel(title: 'FAQs and Enquiries', icon: BaseConfig.faqEnquiriesIcon),
    TextModel(title: 'Conveniences', icon: BaseConfig.convenienceIcon),
    TextModel(title: 'Public Analytics', icon: BaseConfig.publicAnalyticsIcon),
  ];

  @override
  void initState() {
    super.initState();
    init();
  }

  void init() {}

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: MyCityHeader(
        title: isCalender ? 'Calender' : 'Visitor Home',
        isBackButton: isCalender,
        showBack: true,
        calenderOnPressed: () {
          setState(() {
            isCalender = !isCalender;
          });
        },
      ),
      floatingActionButton: CustomElevateBtn(
        icon: BaseConfig.customerSupportIcon,
        onPressed: () {
          // TODO: Implement Customer Support
        },
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: isCalender
            ? const CalenderHomeScreen()
            : Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(height: 10.h),
                  // _buildSearchBar(o).paddingSymmetric(
                  //   horizontal: 16.w,
                  // ),
                  SizedBox(height: 10.h),
                  const LatestNews(),
                  SizedBox(height: 30.h),
                  Expanded(child: categoryServices(o)),
                ],
              ),
      ),
      // bottomNavigationBar: const BottomNav(),
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
                width / (crossAxisCount * 1.56 * width / crossAxisCount);
            return GridView.builder(
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: crossAxisCount,
                crossAxisSpacing: crossAxisSpacing,
                mainAxisSpacing: 10.h,
                childAspectRatio: childAspectRatio,
              ),
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: visitorsCards.length,
              itemBuilder: (context, index) {
                final service = visitorsCards[index];
                return Tooltip(
                  message: service.title,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      ElevatedButton(
                        style: ButtonStyle(
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
                                  width: 3,
                                );
                              }
                              return const BorderSide(
                                color: BaseConfig.textColor,
                                width: 3,
                              );
                            },
                          ),
                        ),
                        onPressed: () async {
                          dPrint(service.title);
                          // Get.toNamed(AppRoutes.LOGIN);
                        },
                        child:
                            // service.isNew
                            //     ? Stack(
                            //         clipBehavior: Clip.hardEdge,
                            //         children: [
                            //           Positioned(
                            //             // alignment: Alignment.bottomLeft,
                            //             left: 0,
                            //             bottom: 0,
                            //             child: SizedBox(
                            //               height: 40.h,
                            //               width: 40.w,
                            //               child: Image.asset(
                            //                 service.icon!,
                            //                 height: 40.h,
                            //                 width: 40.w,
                            //               ),
                            //             ),
                            //           ),
                            //           Positioned(
                            //             top: -4.w,
                            //             right: -4.w,
                            //             child: Container(
                            //               padding: EdgeInsets.all(2.w),
                            //               decoration: const BoxDecoration(
                            //                 color: BaseConfig.appThemeColor1,
                            //                 shape: BoxShape.circle,
                            //               ),
                            //               child: SmallTextNotoSans(
                            //                 text: 'New',
                            //                 color: BaseConfig.mainBackgroundColor,
                            //                 size: 8.sp,
                            //                 fontWeight: FontWeight.w600,
                            //               ),
                            //             ),
                            //           ),
                            //         ],
                            //       )
                            //     :
                            service.icon != null
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
