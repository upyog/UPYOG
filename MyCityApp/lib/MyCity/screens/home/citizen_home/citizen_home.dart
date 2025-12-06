import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/screens/home/official_home/ulb_service_management/ulb_service_managment.dart';
import 'package:mobile_app/MyCity/widget/bottom_nav/bottom_nav.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/MyCity/widget/latest_news/latest_news.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/small_text.dart';

class CitizenHome extends StatefulWidget {
  const CitizenHome({super.key});

  @override
  State<CitizenHome> createState() => _CitizenHomeState();
}

class _CitizenHomeState extends State<CitizenHome> {
  bool isCalender = false;

  final List<TextModel> citizenCards = [
    TextModel(title: 'About the City', icon: BaseConfig.aboutIcon),
    TextModel(title: 'Grievance', icon: BaseConfig.grievanceIcon),
    TextModel(title: 'Water and Sewerage', icon: BaseConfig.waterSewerageIcon),
    TextModel(
      title: 'Property Tax',
      icon: BaseConfig.propertyIcon,
    ),
    TextModel(title: 'Building Plan Approval', icon: BaseConfig.billIcon),
    TextModel(
      title: 'Desludging Service',
      icon: BaseConfig.desludgingIcon,
    ),
    TextModel(title: 'Fire NOC', icon: BaseConfig.fireIcon),
    TextModel(title: 'Know your Govt.', icon: BaseConfig.govtIcon),
    TextModel(title: 'Enquiry and Participate', icon: BaseConfig.enquiryIcon),
  ];

  // late PageController _pageController;
  // Timer? _autoPlayTimer;
  // int _currentPage = 0;

  @override
  void initState() {
    SystemChrome.setEnabledSystemUIMode(
      SystemUiMode.manual,
      overlays: [
        SystemUiOverlay.top,
        SystemUiOverlay.bottom,
      ],
    );
    super.initState();
    // _pageController = PageController();
    // _startAutoPlay();
  }

  @override
  void dispose() {
    // _pageController.dispose();
    super.dispose();
  }

  // void _startAutoPlay() {
  //   _autoPlayTimer = Timer.periodic(const Duration(seconds: 3), (Timer timer) {
  //     if (_pageController.hasClients) {
  //       // Ensure controller is attached
  //       if (_currentPage < BaseConfig.APP_LOGIN_BANNERS.split(',').length - 1) {
  //         _currentPage++;
  //       } else {
  //         _currentPage = 0;
  //       }
  //       _pageController.animateToPage(
  //         _currentPage,
  //         duration: const Duration(milliseconds: 300),
  //         curve: Curves.easeInOut,
  //       );
  //     }
  //   });
  // }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: MyCityHeader(
        title: isCalender ? 'Calender' : 'Citizen Home',
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
              itemCount: citizenCards.length,
              itemBuilder: (context, index) {
                final service = citizenCards[index];
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
                          Get.toNamed(AppRoutes.LOGIN);
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

  // Widget _buildHomeHeader(
  //   BuildContext context,
  //   AuthController authController,
  //   Orientation o,
  // ) {
  //   return Column(
  //     children: [
  //       Row(
  //         mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //         children: [
  //           InkWell(
  //             customBorder: RoundedRectangleBorder(
  //               borderRadius: BorderRadius.circular(8.r),
  //             ),
  //             //onTap: () => Scaffold.of(context).openEndDrawer(),
  //             child: Image.asset(
  //               BaseConfig.HOME_HEADER_LOGO,
  //               fit: BoxFit.contain,
  //               height: o == Orientation.portrait ? 28.h : 30.h,
  //               width: o == Orientation.portrait ? 207.w : 85.w,
  //             ),
  //           ),
  //           const Icon(
  //             Icons.notifications_outlined,
  //             size: 30,
  //           ),
  //           ImagePlaceHolder(
  //             radius: 20.r,
  //             iconSize: 30,
  //             height: 155.h,
  //             width: 155.w,
  //             padding: EdgeInsets.zero,
  //             iconColor: BaseConfig.greyColor1,
  //           ),
  //         ],
  //       ),
  //     ],
  //   );
  // }

  // Widget _buildLatestNews(Orientation o) {
  //   return Stack(
  //     children: [
  //       SizedBox(height: 16.h),
  //       SizedBox(
  //         height: o == Orientation.portrait ? 188.h : 220.h,
  //         child: PageView.builder(
  //           controller: _pageController,
  //           itemCount: BaseConfig.APP_LOGIN_BANNERS.split(',').length,
  //           onPageChanged: (int index) {
  //             setState(() {
  //               _currentPage = index;
  //             });
  //             _autoPlayTimer?.cancel();
  //             _startAutoPlay();
  //           },
  //           itemBuilder: (BuildContext context, int index) {
  //             final img = BaseConfig.APP_LOGIN_BANNERS.split(',')[index];
  //             final imgText =
  //                 BaseConfig.APP_LOGIN_BANNERS_TEXT.split(',')[index];
  //             return FractionallySizedBox(
  //               widthFactor: 1 / _pageController.viewportFraction,
  //               child: MyCityWhatsNewWidget(
  //                 img: img,
  //                 orientation: o,
  //                 imgText: imgText,
  //               ),
  //             );
  //           },
  //         ),
  //       ),
  //       Positioned(
  //         bottom: 10.h,
  //         left: 0,
  //         right: 0,
  //         child: Center(
  //           child: SmoothPageIndicator(
  //             controller: _pageController,
  //             count: BaseConfig.APP_LOGIN_BANNERS.split(',').length,
  //             effect: ExpandingDotsEffect(
  //               dotHeight: 6.h,
  //               dotWidth: o == Orientation.portrait ? 6.w : 3.w,
  //               expansionFactor: 2,
  //               spacing: 4.w,
  //               activeDotColor: BaseConfig.appThemeColor1,
  //               dotColor: BaseConfig.dotGrayColor,
  //             ),
  //           ),
  //         ),
  //       ),
  //     ],
  //   );
  // }
}
