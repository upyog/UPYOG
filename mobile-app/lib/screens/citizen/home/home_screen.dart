import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/image_placeholder.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/notification_controller.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/notification_service.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/dashboard_icon_role.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/news_card.dart';
import 'package:mobile_app/widgets/small_text.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _authController1 = Get.find<AuthController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _notificationController = Get.find<NotificationController>();
  final _commonController = Get.find<CommonController>();

  late PageController _pageController;
  Timer? _autoPlayTimer;
  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      _authController1.getUserLocalData();
    });
    Future.delayed(const Duration(seconds: 2), () {
      NotificationService.getDeviceToken();
    });
    _pageController = PageController();
    _startAutoPlay();
    if (!_authController1.isValidUser) return;
    _initModule();
  }

  void _startAutoPlay() {
    _autoPlayTimer = Timer.periodic(const Duration(seconds: 3), (Timer timer) {
      if (_currentPage < BaseConfig.APP_HOME_BANNERS.split(',').length - 1) {
        _currentPage++;
      } else {
        _currentPage = 0;
      }
      _pageController.animateToPage(
        _currentPage,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    });
  }

  _initModule() async {
    await _commonController.fetchLabels(modules: Modules.PT);
    await _notificationController.getNotificationCount(
      token: _authController1.token!.accessToken!,
    );
  }

  @override
  void dispose() {
    _autoPlayTimer?.cancel();
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<AuthController>(
      builder: (authController) {
        return OrientationBuilder(
          builder: (context, o) {
            final services = [
              // _buildRecentServices(o),
              _citizenServices(o),
            ];
            return Scaffold(
              //endDrawer: _buildHomeHeader(context, authController, o),
              appBar: AppBar(
                automaticallyImplyLeading: false,
                title: _buildHomeHeader(context, authController, o),
                elevation: 0.0,
              ),
              body: SizedBox(
                height: Get.height,
                child: SingleChildScrollView(
                  physics: const AlwaysScrollableScrollPhysics(),
                  child: authController.isLoading.value
                      ? SizedBox(
                          height: Get.height,
                          width: Get.width,
                          child: showCircularIndicator(),
                        )
                      : Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            //Search
                            SizedBox(height: 10.h),
                            _buildSearchBar(o).paddingSymmetric(
                              horizontal: 16.w,
                            ),
                            SizedBox(height: 18.h),
                            _buildLatestNews(o).paddingSymmetric(
                              horizontal: 16.w,
                            ),
                            SizedBox(height: 18.h),
                            // _buildBody(),
                            SizedBox(
                              height: _currentPage == 1
                                  ? o == Orientation.portrait
                                      ? Get.height * 0.4
                                      : Get.height * 1.5
                                  : o == Orientation.portrait
                                      ? Get.height * 0.3
                                      : Get.height * 2,
                              width: Get.width,
                              child: PageView.builder(
                                itemCount: services.length,
                                itemBuilder: (context, index) {
                                  return services[index];
                                },
                                onPageChanged: (index) {
                                  dPrint(index);
                                  setState(() {
                                    _currentPage = index;
                                  });
                                },
                              ),
                            ),
                            //SizedBox(height: 50.h),
                          ],
                        ),
                ),
              ),
              // floatingActionButtonLocation:
              //     FloatingActionButtonLocation.endDocked,
              // floatingActionButton: Padding(
              //   padding: const EdgeInsets.only(bottom: 80.0),
              //   child: Obx(() {
              //     if (_authController1.isValidUser) {
              //       return IconButton(
              //         onPressed: () async {
              //           const url =
              //               'https://upyog.niua.org/digit-ui/citizen/login';

              //           await launchURL(
              //             url,
              //             mode: LaunchMode.externalApplication,
              //           );
              //         },
              //         icon: const Icon(
              //           Icons.add_circle_outline,
              //           size: 40,
              //           color: BaseConfig.appThemeColor1,
              //         ),
              //       );
              //     } else {
              //       return const SizedBox();
              //     }
              //   }),
              // ),
            );
          },
        );
      },
    );
  }

  Widget _citizenServices(Orientation o) {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          InterText(
            text: getLocalizedString(i18.common.ALL_CITIZEN_SERVICES),
            fontWeight: FontWeight.w800,
            size: o == Orientation.portrait ? 16.sp : 8.sp,
            color: BaseConfig.textColor2,
          ).paddingSymmetric(
            horizontal: 16.w,
          ),
          SizedBox(height: 16.h),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16.w),
            child: LayoutBuilder(
              builder: (context, constraints) {
                final o = MediaQuery.of(context).orientation;
                final isPortrait = o == Orientation.portrait;
                final width = constraints.maxWidth;
                final crossAxisCount = isPortrait ? 4 : 6;
                final crossAxisSpacing = isPortrait ? 30.w : 10.w;
                final childAspectRatio =
                    width / (crossAxisCount * 1.4 * width / crossAxisCount);
                return GridView.builder(
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: crossAxisCount,
                    crossAxisSpacing: crossAxisSpacing,
                    mainAxisSpacing: 10.h,
                    childAspectRatio: childAspectRatio,
                  ),
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: serviceNamesCitizen.length,
                  itemBuilder: (context, index) {
                    final service = serviceNamesCitizen[index];
                    return Tooltip(
                      message: getLocalizedString(service.title),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          IconButton.filled(
                            style: IconButton.styleFrom(
                              backgroundColor: BaseConfig.appThemeColor1,
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(12.r),
                              ),
                              padding: EdgeInsets.all(12.w),
                            ),
                            onPressed: () async {
                              dPrint(service.title);
                              if (!_authController1.isValidUser) {
                                Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
                                // Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
                                return;
                              }
                              if (service.title == i18.common.HELP_GRIEVANCE) {
                                Get.toNamed(AppRoutes.GRIEVANCES_SCREEN);
                              }
                              if (service.title == i18.common.TRADE_LICENSE) {
                                Get.toNamed(
                                  AppRoutes.TRADE_LICENSE_APPLICATIONS,
                                );
                              }
                              if (service.title == i18.common.WATER_SEWERAGE) {
                                Get.toNamed(AppRoutes.WATER_SEWERAGE);
                              }
                              if (service.title == i18.common.PROPERTY_TAX) {
                                Get.toNamed(AppRoutes.MY_PROPERTIES);
                              }
                              if (service.title ==
                                  i18.common.BUILDING_PLAN_APPROVAL) {
                                Get.toNamed(AppRoutes.BUILDING_APPLICATION);
                              }
                              if (service.title ==
                                  i18.common.DESULDGING_SERVICES) {
                                Get.toNamed(AppRoutes.FSM_SCREEN);
                              }
                              if (service.title == i18.common.FIRE_NOC) {
                                Get.toNamed(AppRoutes.FIRE_NOC_SCREEN);
                              }
                              if (service.title == i18.common.MCOLLECT) {
                                await _commonController.fetchLabels(
                                  modules: Modules.UC,
                                );
                                Get.toNamed(AppRoutes.MY_CHALLANS);
                              }
                            },
                            icon: Icon(
                              service.flutterIcon,
                              size: 24,
                              color: Colors.white,
                            ),
                            // icon: SvgPicture.asset(
                            //   service.icon,
                            //   height: 24,
                            //   width: 24,
                            //   fit: BoxFit.contain,
                            //   colorFilter: const ColorFilter.mode(
                            //     BaseConfig.mainBackgroundColor,
                            //     BlendMode.srcIn,
                            //   ),
                            // ),
                          ),
                          SizedBox(height: 4.h),
                          SmallTextNotoSans(
                            text: getLocalizedString(service.title),
                            size: isPortrait ? 10.sp : 7.sp,
                            maxLine: 2,
                            fontWeight: FontWeight.w500,
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
        ],
      ),
    );
  }

  // Widget _buildRecentServices(Orientation o) {
  //   return Column(
  //     crossAxisAlignment: CrossAxisAlignment.start,
  //     children: [
  //       Row(
  //         mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //         children: [
  //           InterText(
  //             text: 'Your recent services',
  //             fontWeight: FontWeight.w800,
  //             size: o == Orientation.portrait ? 16.sp : 8.sp,
  //             color: BaseConfig.textColor2,
  //           ).paddingSymmetric(
  //             horizontal: 16.w,
  //           ),
  //           IconButton(
  //             onPressed: () {
  //               //TODO: View format
  //             },
  //             icon: SvgPicture.asset(
  //               BaseConfig.listSvg,
  //               height: 11.h,
  //               width: 16.w,
  //             ),
  //           ),
  //         ],
  //       ),
  //       // SizedBox(height: 4.h),

  //       //[x] Grievance card
  //       ListTile(
  //         leading: SvgPicture.asset(
  //           BaseConfig.ticketSvg,
  //           height: o == Orientation.portrait ? 16.h : 24.h,
  //         ),
  //         title: MediumText(
  //           text: 'Ticket  1- Grievance',
  //           size: o == Orientation.portrait ? 17.sp : 9.sp,
  //           fontWeight: FontWeight.w400,
  //         ),
  //         subtitle: Row(
  //           children: [
  //             SmallText(
  //               text: 'Pending resolution',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.appThemeColor1,
  //             ),
  //             SizedBox(width: 4.w),
  //             SmallText(
  //               text: '• 2 days ago',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.subTextColor,
  //             ),
  //           ],
  //         ),
  //         trailing: IconButton(
  //           onPressed: () {
  //             //[x] TODO: Implement some action menu
  //           },
  //           icon: const Icon(Icons.more_horiz, color: BaseConfig.dotBlueColor),
  //         ),
  //         contentPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //         onTap: () {
  //           //[x] TODO: Navigate to details page
  //           if (!_authController1.isValidUser) {
  //             Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
  //             return;
  //           }
  //           Get.toNamed(AppRoutes.GRIEVANCES_SCREEN);
  //         },
  //       ),
  //       //[x] Property tax card
  //       // SizedBox(height: 24.h),
  //       ListTile(
  //         leading: SvgPicture.asset(
  //           BaseConfig.propertySvg,
  //           height: o == Orientation.portrait ? 18.h : 30.h,
  //           colorFilter:
  //               const ColorFilter.mode(Color(0xFF974602), BlendMode.srcIn),
  //         ),
  //         title: MediumText(
  //           text: 'Property tax',
  //           size: o == Orientation.portrait ? 17.sp : 9.sp,
  //           fontWeight: FontWeight.w400,
  //         ),
  //         subtitle: Row(
  //           children: [
  //             SmallText(
  //               text: 'Application review',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.subTextColor,
  //             ),
  //             SizedBox(width: 4.w),
  //             SmallText(
  //               text: '• 3 days ago',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.subTextColor,
  //             ),
  //           ],
  //         ),
  //         trailing: IconButton(
  //           onPressed: () {},
  //           icon: const Icon(Icons.more_horiz, color: BaseConfig.dotBlueColor),
  //         ),
  //         contentPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //         onTap: () {
  //           if (!_authController1.isValidUser) {
  //             Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
  //             return;
  //           }
  //           Get.toNamed(AppRoutes.MY_PROPERTIES);
  //         },
  //       ),

  //       //[x] Water connection card
  //       ListTile(
  //         leading: SvgPicture.asset(
  //           BaseConfig.ticketSvg,
  //           height: o == Orientation.portrait ? 14.h : 22.h,
  //         ),
  //         title: MediumText(
  //           text: 'Ticket  1- Grievance',
  //           size: o == Orientation.portrait ? 17.sp : 9.sp,
  //           fontWeight: FontWeight.w400,
  //         ),
  //         subtitle: Row(
  //           children: [
  //             SmallText(
  //               text: 'Pending resolution',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.appThemeColor1,
  //             ),
  //             SizedBox(width: 4.w),
  //             SmallText(
  //               text: '• 2 days ago',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.subTextColor,
  //             ),
  //           ],
  //         ),
  //         trailing: IconButton(
  //           onPressed: () {
  //             //[x] TODO: Implement some action menu
  //           },
  //           icon: const Icon(Icons.more_horiz, color: BaseConfig.dotBlueColor),
  //         ),
  //         contentPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //         onTap: () {
  //           //[x] TODO: Navigate to details page
  //         },
  //       ),
  //       //[x] Property tax card
  //       ListTile(
  //         leading: SvgPicture.asset(
  //           BaseConfig.waterSvg,
  //           height: o == Orientation.portrait ? 16.h : 24.h,
  //         ),
  //         title: MediumText(
  //           text: 'Water connection',
  //           size: o == Orientation.portrait ? 17.sp : 9.sp,
  //           fontWeight: FontWeight.w400,
  //         ),
  //         subtitle: Row(
  //           children: [
  //             SmallText(
  //               text: 'Application review',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.textColor,
  //             ),
  //             SizedBox(width: 4.w),
  //             SmallText(
  //               text: '• 3 days ago',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.subTextColor,
  //             ),
  //           ],
  //         ),
  //         trailing: IconButton(
  //           onPressed: () {
  //             //[x] TODO: Implement some action menu
  //           },
  //           icon: const Icon(Icons.more_horiz, color: BaseConfig.dotBlueColor),
  //         ),
  //         contentPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //         onTap: () {
  //           if (!_authController1.isValidUser) {
  //             Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
  //             return;
  //           }
  //           Get.toNamed(AppRoutes.WATER_SEWERAGE);
  //         },
  //       ),

  //       //[x] Ticket card
  //       ListTile(
  //         leading: SvgPicture.asset(
  //           BaseConfig.ticketSvg,
  //           height: o == Orientation.portrait ? 16.h : 24.h,
  //         ),
  //         title: MediumText(
  //           text: 'Ticket 2',
  //           size: o == Orientation.portrait ? 17.sp : 9.sp,
  //           fontWeight: FontWeight.w400,
  //         ),
  //         subtitle: Row(
  //           children: [
  //             SmallText(
  //               text: 'Resolved',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.textColor,
  //             ),
  //             SizedBox(width: 4.w),
  //             SmallText(
  //               text: '• 4 days ago',
  //               size: o == Orientation.portrait ? 13.sp : 7.sp,
  //               fontWeight: FontWeight.w400,
  //               color: BaseConfig.subTextColor,
  //             ),
  //           ],
  //         ),
  //         trailing: IconButton(
  //           onPressed: () {
  //             //[x] TODO: Implement some action menu
  //           },
  //           icon: const Icon(Icons.more_horiz, color: BaseConfig.dotBlueColor),
  //         ),
  //         contentPadding: EdgeInsets.symmetric(horizontal: 16.w),
  //         onTap: () {
  //           //[x] TODO: Navigate to details page
  //         },
  //       ),

  //       //[x] See more button
  //       Align(
  //         alignment: Alignment.centerLeft,
  //         child: TextButton(
  //           onPressed: () {
  //             //[x] TODO: Implement see more action
  //           },
  //           child: SmallText(
  //             text: 'See More',
  //             size: o == Orientation.portrait ? 17.sp : 9.sp,
  //             fontWeight: FontWeight.w400,
  //             color: BaseConfig.lightAmber,
  //           ),
  //         ).paddingOnly(left: o == Orientation.portrait ? 0 : 12.w),
  //       ),
  //       SizedBox(height: 24.h),
  //     ],
  //   );
  // }

  Widget _buildLatestNews(Orientation o) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          height: o == Orientation.portrait ? 280.h : 220.h,
          child: PageView.builder(
            controller: _pageController,
            itemCount: BaseConfig.APP_HOME_BANNERS.split(',').length,
            onPageChanged: (int index) {
              _currentPage = index;
              _autoPlayTimer?.cancel();
              _startAutoPlay();
            },
            itemBuilder: (BuildContext context, int index) {
              final img = BaseConfig.APP_HOME_BANNERS.split(',')[index];
              return FractionallySizedBox(
                widthFactor: 1 / _pageController.viewportFraction,
                child: NewsCard(
                  img: img,
                  orientation: o,
                  pageController: _pageController,
                ),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildSearchBar(Orientation o) {
    final radius = BorderRadius.circular(12.r);
    return Container(
      height: o == Orientation.portrait ? 52 : 60.h,
      decoration: BoxDecoration(
        borderRadius: radius,
        gradient: const LinearGradient(
          colors: [
            Color(0xffE67055),
            Color(0xffAC5855),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      ),
      child: TextField(
        onTap: () {
          Get.toNamed(AppRoutes.HOME_GLOBAL_SEARCH);
        },
        style: const TextStyle(color: BaseConfig.mainBackgroundColor),
        cursorColor: BaseConfig.mainBackgroundColor,
        decoration: InputDecoration(
          hintText: getLocalizedString(i18.common.SEARCH),
          contentPadding:
              EdgeInsets.all(o == Orientation.portrait ? 16.w : 8.w),
          focusColor: BaseConfig.mainBackgroundColor,
          hintStyle: const TextStyle(color: BaseConfig.mainBackgroundColor),
          prefixIcon: Padding(
            padding: EdgeInsets.only(left: 6.w),
            child:
                const Icon(Icons.search, color: BaseConfig.mainBackgroundColor),
          ),
          suffixIcon: IconButton(
            onPressed: () {
              //TODO: Voice search
            },
            icon: Padding(
              padding: EdgeInsets.only(left: 12.w, right: 12.w),
              child: Icon(
                Icons.mic_none_outlined,
                color: BaseConfig.mainBackgroundColor.withValues(alpha: 0.7),
              ),
            ),
          ),
          border: OutlineInputBorder(
            borderRadius: radius,
            borderSide: BorderSide.none,
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: radius,
            borderSide: const BorderSide(color: BaseConfig.mainBackgroundColor),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: radius,
            borderSide: const BorderSide(color: BaseConfig.mainBackgroundColor),
          ),
        ),
      ),
    );
  }

  Widget _buildHomeHeader(
    BuildContext context,
    AuthController authController,
    Orientation o,
  ) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        InkWell(
          customBorder: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8.r),
          ),
          //onTap: () => Scaffold.of(context).openEndDrawer(),
          child: Image.asset(
            BaseConfig.HOME_HEADER_LOGO,
            fit: BoxFit.contain,
            height: o == Orientation.portrait ? 28.h : 30.h,
            width: o == Orientation.portrait ? 207.w : 85.w,
          ),
        ),

        // Search
        Row(
          children: [
            Obx(
              () => IconButton(
                onPressed: () {
                  _notificationController.isBadgeShow.value = false;
                  _notificationController.totalCount.value = 0;
                  Get.toNamed(AppRoutes.NOTIFICATION_TEMP);
                },
                icon: Badge.count(
                  count: _notificationController.totalCount.value,
                  isLabelVisible: _notificationController.totalCount.value > 0,
                  child: const Icon(
                    Icons.notifications_outlined,
                    size: 30,
                  ),
                  // child: SvgPicture.asset(
                  //   BaseConfig.bellSvg,
                  //   height: 24.h,
                  //   colorFilter: const ColorFilter.mode(
                  //     BaseConfig.textColor,
                  //     BlendMode.srcIn,
                  //   ),
                  // ),
                ),
              ),
            ),
            authController.isValidUser
                ? IconButton(
                    onPressed: () {
                      // Get.toNamed(
                      //   AppRoutes.PROFILE,
                      //   arguments: {
                      //     'showBackBtn': true,
                      //   },
                      // );
                    },
                    icon: CircleAvatar(
                      radius: 20.r,
                      backgroundColor: Colors.transparent,
                      child: FutureBuilder<FileStore?>(
                        future:
                            _editProfileController.getCacheProfileFIleStore(),
                        builder: (context, snapshot) {
                          if (snapshot.connectionState ==
                              ConnectionState.waiting) {
                            return showCircularIndicator();
                          }
                          if (snapshot.hasData && !snapshot.hasError) {
                            if (snapshot.data is String ||
                                snapshot.data == null) {
                              return CircleAvatar(
                                backgroundColor: BaseConfig.greyColor2,
                                radius: 20.r,
                                child: const Icon(
                                  Icons.person,
                                  size: 30,
                                ),
                              );
                            }
                            final fileStore = snapshot.data;
                            return ImagePlaceHolder(
                              radius: 20.r,
                              iconSize: 30,
                              height: 155.h,
                              width: 155.w,
                              padding: EdgeInsets.zero,
                              iconColor: Colors.white,
                              backgroundColor: BaseConfig.appThemeColor1,
                              photoUrl: fileStore?.getUserPhoto(),
                            );
                          } else {
                            return CircleAvatar(
                              backgroundColor: BaseConfig.greyColor2,
                              radius: 20.r,
                              child: const Icon(
                                Icons.person,
                                color: BaseConfig.mainBackgroundColor,
                                size: 30,
                              ),
                              // child: const Icon(
                              //   BaseConfig.headerProfilePlaceholderIcon,
                              //   color: BaseConfig.greyColor1,
                              //   size: 30,
                              // ),
                            );
                          }
                        },
                      ),
                    ),
                  )
                : ImagePlaceHolder(
                    radius: 20.r,
                    iconSize: 30,
                    height: 155.h,
                    width: 155.w,
                    padding: EdgeInsets.zero,
                    iconColor: BaseConfig.greyColor1,
                  ),
          ],
        ),
      ],
    );
  }

  // Widget _buildBody() {
  //   return const Padding(
  //     padding: EdgeInsets.symmetric(horizontal: 10),
  //     child: Column(
  //       crossAxisAlignment: CrossAxisAlignment.start,
  //       children: [
  // const BigText(
  //   text: 'Quick Actions',
  //   fontWeight: FontWeight.bold,
  // ),
  // Row(
  //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //   children: [
  //     Expanded(
  //       child: _cardBuild(
  //         icon: BaseConfig.homeUiMyPaymentIcon,
  //         iconColor: BaseConfig.bodyIconColor,
  //         txt: "My Payments",
  //       ).ripple(() async {
  //         Get.toNamed(AppRoutes.HOME_MY_PAYMENTS);
  //         await _commonController.fetchLabels(modules: Modules.PT);
  //         await _commonController.fetchLabels(modules: Modules.WS);
  //       }),
  //     ),
  //     // const SizedBox(width: 6),
  //     Expanded(
  //       child: _cardBuild(
  //         icon: BaseConfig.homeUiMyCertificateIcon,
  //         iconColor: BaseConfig.bodyIconColor,
  //         txt: "My Certificates",
  //       ).ripple(() {
  //         Get.toNamed(AppRoutes.HOME_MY_CERTIFICATES);
  //       }),
  //     ),
  //     // const SizedBox(width: 6),
  //     Expanded(
  //       child: _cardBuild(
  //         icon: BaseConfig.homeUiMyApplicationIcon,
  //         iconColor: BaseConfig.bodyIconColor,
  //         txt: "My Applications",
  //       ).ripple(() async {
  //         Get.toNamed(AppRoutes.HOME_MY_APPLICATIONS);
  //         await _commonController.fetchLabels(modules: Modules.PT);
  //         await _commonController.fetchLabels(modules: Modules.WS);
  //         await _commonController.fetchLabels(modules: Modules.TL);
  //       }),
  //     ),
  //   ],
  // ),
  // const SizedBox(height: 40),
  // const BigText(
  //   text: 'Citizen Services',
  //   fontWeight: FontWeight.bold,
  // ),
  // _buildBodyCard1(),
  // _buildBodyCard3(),
  // _buildBodyCard4(),
  // _buildBodyCard2(),
  // const SizedBox(height: 30),
  // _shareApp(),
  // const SizedBox(height: 20),
  // _buildLatestUpdates(),
  // const SizedBox(height: 100),
  //       ],
  //     ),
  //   );
  // }

//   Widget _buildLatestUpdates() {
//     return Column(
//       crossAxisAlignment: CrossAxisAlignment.start,
//       children: [
//         const BigText(
//           text: 'Latest Updates',
//           fontWeight: FontWeight.w500,
//         ),
//         const SizedBox(height: 10),
//         SizedBox(
//           width: Get.width,
//           child: const Card(
//             color: BaseConfig.mainBackgroundColor,
//             child: Padding(
//               padding: EdgeInsets.all(8.0),
//               child: Column(
//                 crossAxisAlignment: CrossAxisAlignment.start,
//                 children: [
//                   MediumText(
//                     text:
//                         '1. Urban citizen services at doorstep for your ease of living and doing business.',
//                   ),
//                   MediumText(
//                     text:
//                         '2. New urban citizen services will be available soon.',
//                   ),
//                 ],
//               ),
//             ),
//           ),
//         ),
//       ],
//     );
//   }

//   Widget _buildBodyCard1() {
//     return Row(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       children: [
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardPtIcon,
//             title: getLocalizedString(i18.propertyTax.PROPERTY_TAX),
//             onTap: () {
//               Get.toNamed(AppRoutes.PROPERTY_TAX);
//             },
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardTlIcon,
//             title: getLocalizedString(i18.tradeLicense.TRADE_LICENSE),
//             onTap: () {
//               Get.toNamed(AppRoutes.TRADE_LICENSE);
//             },
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardWsIcon,
//             title: 'Water',
//             onTap: () {
//               Get.toNamed(AppRoutes.WATER);
//             },
//           ),
//         ),
//       ],
//     );
//   }

//   Widget _buildBodyCard2() {
//     return Row(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       children: [
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardBirthIcon,
//             title: getLocalizedString(i18.common.BIRTH),
//             height: 30,
//             onTap: () {
//               Get.toNamed(AppRoutes.BIRTHSCREEN);
//             },
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardBirthIcon,
//             title: getLocalizedString(i18.common.DEATH),
//             height: 30,
//             onTap: () {
//               Get.toNamed(AppRoutes.DEATHSCREEN);
//             },
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: Container(),
//         ),
//       ],
//     );
//   }

//   Widget _buildBodyCard3() {
//     return Row(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       children: [
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardWsIcon,
//             title: 'Sewerage',
//             onTap: () {
//               Get.toNamed(AppRoutes.SEWERAGE);
//             },
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardGrievanceIcon,
//             title: getLocalizedString(i18.grievance.PGR),
//             onTap: () {
//               Get.toNamed(AppRoutes.GRIEVANCE);
//             },
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardBpaIcon,
//             title: getLocalizedString(i18.common.BUILDING_PLAN_APPROVAL),
//             onTap: () {
//               Get.toNamed(AppRoutes.BDPLANAPPROV);
//             },
//           ),
//         ),
//       ],
//     );
//   }

//   Widget _buildBodyCard4() {
//     return Row(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       children: [
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardDsIcon,
//             title: 'Desludging Service',
//             onTap: () {},
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardFnIcon,
//             title: 'Fire NOC',
//             height: 30,
//             onTap: () {},
//           ),
//         ),
//         const SizedBox(width: 6),
//         Expanded(
//           child: _cardBuildSvg(
//             svgIcon: BaseConfig.homeUiCardMcIcon,
//             title: 'Miscellaneous Collections',
//             onTap: () {},
//           ),
//         ),
//       ],
//     );
//   }

//   Widget _cardBuild({
//     required IconData icon,
//     Color iconColor = BaseConfig.bodyIconColor,
//     required String txt,
//   }) {
//     return Column(
//       children: [
//         SizedBox(
//           height: 80,
//           width: 80,
//           child: Card(
//             shape: RoundedRectangleBorder(
//               borderRadius: BorderRadius.circular(100.0),
//             ),
//             color: BaseConfig.mainBackgroundColor,
//             child: Padding(
//               padding: const EdgeInsets.all(20.0),
//               child: Center(
//                 child: Icon(icon, size: 34, color: iconColor),
//               ),
//             ),
//           ),
//         ),
//         SmallText(
//           text: txt,
//           textAlign: TextAlign.center,
//           fontWeight: FontWeight.w500,
//           size: 15,
//         ),
//       ],
//     );
//   }

//   Widget _cardBuildSvg({
//     required String svgIcon,
//     required String title,
//     Color svgColor = BaseConfig.bodyIconColor,
//     double height = 24,
//     required VoidCallback onTap,
//   }) {
//     return SizedBox(
//       height: 120,
//       width: 100,
//       child: Card(
//         color: BaseConfig.mainBackgroundColor,
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             SvgPicture.asset(
//               svgIcon,
//               height: height,
//               colorFilter: ColorFilter.mode(
//                 svgColor,
//                 BlendMode.srcIn,
//               ),
//             ),
//             const SizedBox(height: 6),
//             SmallText(
//               text: title,
//               textAlign: TextAlign.center,
//               fontWeight: FontWeight.w500,
//               size: 15,
//             ),
//           ],
//         ).rippleColor(onTap, 100, borderRadius: 10),
//       ),
//     );
//   }

// //Share App Widget Abhis
//   Widget _shareApp() {
//     return Container(
//       height: 180,
//       width: Get.width,
//       decoration: BoxDecoration(
//         gradient: const LinearGradient(
//           colors: [
//             Colors.orange,
//             Colors.yellow,
//           ],
//         ),
//         borderRadius: BorderRadius.circular(25),
//       ),
//       child: Padding(
//         padding: const EdgeInsets.all(10.0),
//         child: Row(
//           mainAxisAlignment: MainAxisAlignment.spaceAround,
//           children: [
//             Column(
//               children: [
//                 const SizedBox(
//                   height: 10,
//                 ),
//                 const BigText(
//                   text: "Share UPYOG app",
//                   fontWeight: FontWeight.bold,
//                   size: 18,
//                 ),
//                 const Padding(
//                   padding: EdgeInsets.fromLTRB(0, 10, 0, 0),
//                   child: SmallText(
//                     text: "Tell your friends about the \nUPYOG app!",
//                     size: 12,
//                   ),
//                 ),
//                 const SizedBox(
//                   height: 20,
//                 ),
//                 gradientBtn(
//                   text: "SHARE NOW",
//                   onPressed: () {
//                     Share.share(
//                         'Hi, click on the link to download the UPYOG mobile app and start accessing UPYOG servicesthrough your smart phone.'
//                         '*Operator data charges may apply. #UPYOG https://download.app.com/1');
//                   },
//                   horizonPadding: 10,
//                   width: Get.width * 0.35,
//                   fontSize: 12,
//                 ),
//               ],
//             ),
//             const Icon(
//               Icons.mobile_screen_share,
//               size: 120,
//             ),
//           ],
//         ),
//       ),
//     );
//   }

//   //Landscape
//   Widget _buildBodyLand() {
//     return Padding(
//       padding: const EdgeInsets.symmetric(horizontal: 10),
//       child: Column(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         children: [
//           const BigText(
//             text: 'Quick Actions',
//             fontWeight: FontWeight.bold,
//           ),
//           Row(
//             mainAxisAlignment: MainAxisAlignment.spaceAround,
//             crossAxisAlignment: CrossAxisAlignment.center,
//             children: [
//               Expanded(
//                 child: _cardBuild(
//                   icon: BaseConfig.homeUiMyPaymentIcon,
//                   iconColor: BaseConfig.bodyIconColor,
//                   txt: "My Payments",
//                 ).ripple(() async {
//                   Get.toNamed(AppRoutes.HOME_MY_PAYMENTS);
//                   await _commonController.fetchLabels(modules: Modules.PT);
//                   await _commonController.fetchLabels(modules: Modules.WS);
//                 }),
//               ),
//               const SizedBox(width: 6),
//               Expanded(
//                 child: _cardBuild(
//                   icon: BaseConfig.homeUiMyCertificateIcon,
//                   iconColor: BaseConfig.bodyIconColor,
//                   txt: "My Certificates",
//                 ).ripple(() {
//                   Get.toNamed(AppRoutes.HOME_MY_CERTIFICATES);
//                 }),
//               ),
//               const SizedBox(width: 6),
//               Expanded(
//                 child: _cardBuild(
//                   icon: BaseConfig.homeUiMyApplicationIcon,
//                   iconColor: BaseConfig.bodyIconColor,
//                   txt: "My Applications",
//                 ).ripple(() async {
//                   Get.toNamed(AppRoutes.HOME_MY_APPLICATIONS);
//                   await _commonController.fetchLabels(modules: Modules.PT);
//                   await _commonController.fetchLabels(modules: Modules.WS);
//                   await _commonController.fetchLabels(modules: Modules.TL);
//                 }),
//               ),
//             ],
//           ),
//           const SizedBox(height: 40),
//           const BigText(
//             text: 'Citizen Services',
//             fontWeight: FontWeight.bold,
//           ),
//           _buildBodyCard1(),
//           _buildBodyCard2(),
//           _buildBodyCard3(),
//           const SizedBox(height: 30),
//           _shareAppLand(),
//           const SizedBox(height: 20),
//           _buildLatestUpdates(),
//           const SizedBox(height: 100),
//         ],
//       ),
//     );
//   }

//   Widget _shareAppLand() {
//     return Container(
//       height: Get.height * 0.5,
//       width: Get.width,
//       decoration: BoxDecoration(
//         gradient: const LinearGradient(
//           colors: [
//             Colors.orange,
//             Colors.yellow,
//           ],
//         ),
//         borderRadius: BorderRadius.circular(20),
//       ),
//       child: Padding(
//         padding: const EdgeInsets.all(10.0),
//         child: Row(
//           children: [
//             Expanded(
//               child: Column(
//                 children: [
//                   const SizedBox(
//                     height: 10,
//                   ),
//                   const BigText(
//                     text: "Share UPYOG app",
//                     fontWeight: FontWeight.bold,
//                     size: 18,
//                   ),
//                   const Padding(
//                     padding: EdgeInsets.fromLTRB(0, 10, 0, 0),
//                     child: SmallText(
//                       text: "Tell your friends about the \nUPYOG app!",
//                       size: 12,
//                     ),
//                   ),
//                   const SizedBox(
//                     height: 20,
//                   ),
//                   gradientBtn(
//                     text: "SHARE NOW",
//                     onPressed: () {
//                       Share.share(
//                           'Hi, click on the link to download the UPYOG mobile app and start accessing UPYOG servicesthrough your smart phone.'
//                           '*Operator data charges may apply. #UPYOG https://download.app.com/1');
//                     },
//                     horizonPadding: 10,
//                     width: Get.width * 0.35,
//                     fontSize: 12,
//                   ),
//                 ],
//               ),
//             ),
//             const Expanded(
//               child: Icon(
//                 Icons.mobile_screen_share,
//                 size: 120,
//               ),
//             ),
//           ],
//         ),
//       ),
//     );
//   }
}
