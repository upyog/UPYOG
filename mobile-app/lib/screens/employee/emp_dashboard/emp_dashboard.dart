import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/image_placeholder.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/model/citizen/files/file_store.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/notification_service.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/dashboard_icon_role.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/news_card.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpDashboard extends StatefulWidget {
  const EmpDashboard({super.key});

  @override
  State<EmpDashboard> createState() => _EmpDashboardState();
}

class _EmpDashboardState extends State<EmpDashboard> {
  final _authController1 = Get.find<AuthController>();
  final _editProfileController = Get.find<EditProfileController>();
  // final _notificationController = Get.find<NotificationController>();

  late PageController _pageController;
  Timer? _autoPlayTimer;
  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    _pageController = PageController();
    _startAutoPlay();
    init();
  }

  init() async {
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      _authController1.getUserLocalData();
    });
    Future.delayed(const Duration(seconds: 2), () {
      NotificationService.getDeviceToken();
    });
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
            return Scaffold(
              appBar: AppBar(
                automaticallyImplyLeading: false,
                title: _buildHomeHeader(context, authController, o),
                elevation: 0.0,
              ),
              body: SizedBox(
                height: Get.height,
                width: Get.width,
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
                              height: o == Orientation.portrait
                                  ? Get.height * 0.4
                                  : Get.height * 1.5,
                              width: Get.width,
                              child: _citizenServices(o).marginOnly(top: 10.h),
                            ),

                            SizedBox(height: 100.h),
                          ],
                        ),
                ),
              ),
            );
          },
        );
      },
    );
  }

  Widget _citizenServices(Orientation o) {
    final filteredServiceNames = serviceNames.where((service) {
      return service.roles != null &&
          service.roles!.any(
            (role) => _authController1.token!.userRequest!.roles!
                .any((userRole) => userRole.code == role),
          );
    }).toList();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        InterText(
          text: getLocalizedString(i18.common.EMP_WORKLIST),
          fontWeight: FontWeight.w800,
          size: o == Orientation.portrait ? 16.sp : 8.sp,
          color: BaseConfig.textColor2,
        ).paddingSymmetric(
          horizontal: 16.w,
        ),
        SizedBox(height: 16.h),
        if (!_authController1.isValidUser) ...[
          const SizedBox.shrink(),
        ] else ...[
          if (filteredServiceNames.isNotEmpty)
            Padding(
              padding: EdgeInsets.symmetric(horizontal: 16.w),
              child: GridView.builder(
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: o == Orientation.portrait ? 4 : 6,
                  crossAxisSpacing: o == Orientation.portrait ? 30.w : 10.w,
                  mainAxisSpacing: 10,
                  childAspectRatio: 0.7,
                ),
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: filteredServiceNames.length,
                itemBuilder: (context, index) {
                  final service = filteredServiceNames[index];

                  return service.roles != null
                      ? Tooltip(
                          message: service.title
                                  .contains(i18.common.BUILDING_PLAN_APPROVAL)
                              ? 'OBPS'
                              : getLocalizedString(service.title),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.center,
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
                                  if (!_authController1.isValidUser) {
                                    Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
                                    // Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
                                    return;
                                  }

                                  if (service.title ==
                                      i18.common.PROPERTY_TAX) {
                                    Get.toNamed(AppRoutes.EMP_PT);
                                  }

                                  if (service.title ==
                                      i18.common.TRADE_LICENSE) {
                                    Get.toNamed(
                                      AppRoutes.EMP_TRADE_LICENSE,
                                    );
                                  }
                                  if (service.title ==
                                      i18.common.BIRTH_AND_DEATH) {
                                    // Get.toNamed(AppRoutes.BIRTH_DEATH_SCREEN);
                                  }
                                  if (service.title == i18.common.WATER) {
                                    Get.toNamed(AppRoutes.EMP_WS);
                                  }
                                  if (service.title == i18.common.SEWERAGE) {
                                    Get.toNamed(AppRoutes.EMP_SW);
                                  }
                                  if (service.title == 'Help & Grievance') {
                                    // Get.toNamed(AppRoutes.GRIEVANCES_SCREEN);
                                  }
                                  if (service.title ==
                                      i18.common.BUILDING_PLAN_APPROVAL) {
                                    Get.toNamed(AppRoutes.EMP_BPA_OBPS);
                                  }
                                  if (service.title == 'Desludging Serviced') {
                                    // Get.toNamed(AppRoutes.DESLUDGING_SERVICE);
                                    snackBar(
                                      'Incomplete',
                                      'Working progress',
                                      Colors.red,
                                    );
                                  }
                                  if (service.title ==
                                      i18.common.EMP_FIRE_NOC) {
                                    Get.toNamed(AppRoutes.EMP_FIRE_NOC);
                                  }
                                  if (service.title == i18.common.UC_COLLECT) {
                                    Get.toNamed(AppRoutes.EMP_UC_CHALLANS);
                                  }
                                  if (service.title ==
                                      i18.common.HELP_GRIEVANCE) {
                                    Get.toNamed(AppRoutes.EMP_GRIEVANCES);
                                  }
                                },
                                icon: Icon(
                                  service.flutterIcon,
                                  size: 24,
                                  color: Colors.white,
                                ),
                              ),
                              SizedBox(height: 4.h),
                              SmallTextNotoSans(
                                text: service.title.contains(
                                  i18.common.BUILDING_PLAN_APPROVAL,
                                )
                                    ? 'OBPS'
                                    : getLocalizedString(service.title),
                                size: o == Orientation.portrait ? 10.sp : 7.sp,
                                maxLine: 2,
                                fontWeight: FontWeight.w500,
                                textAlign: TextAlign.center,
                                textOverflow: TextOverflow.ellipsis,
                              ),
                            ],
                          ),
                        )
                      : const SizedBox.shrink();
                },
              ),
            )
          else
            const SmallTextNotoSans(
              text: 'No services available',
              size: 12,
              fontWeight: FontWeight.w500,
            ).paddingSymmetric(
              horizontal: 16,
            ),
        ],
      ],
    );
  }

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
            if (authController.isValidUser)
              IconButton(
                onPressed: () {},
                icon: CircleAvatar(
                  radius: 20.r,
                  backgroundColor: Colors.transparent,
                  child: FutureBuilder<FileStore?>(
                    future: _editProfileController.getCacheProfileFIleStore(),
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return showCircularIndicator();
                      }
                      if (snapshot.hasData && !snapshot.hasError) {
                        if (snapshot.data is String || snapshot.data == null) {
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
              ),
          ],
        ),
      ],
    );
  }
}
