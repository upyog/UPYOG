import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/gradient_btn.dart';
import 'package:mobile_app/components/image_placeholder.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/hive_services.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/news_card_without_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';

class SelectCitizen extends StatefulWidget {
  const SelectCitizen({super.key});

  @override
  State<SelectCitizen> createState() => _SelectCitizenState();
}

class _SelectCitizenState extends State<SelectCitizen> {
  final _languageController = Get.find<LanguageController>();
  final _authController = Get.find<AuthController>();

  late PageController _pageController;
  Timer? _autoPlayTimer;
  int _currentPage = 0;

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
    checkUserType();
    WidgetsBinding.instance.addPostFrameCallback((_) => _selectLanguage());
    _languageController.getLocalizationData();
    _pageController = PageController();
    // _startAutoPlay();
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _startAutoPlay() {
    _autoPlayTimer = Timer.periodic(const Duration(seconds: 3), (Timer timer) {
      if (_pageController.hasClients) {
        // Ensure controller is attached
        if (_currentPage <
            BaseConfig.APP_LOGIN_BANNERS_V2.split(',').length - 1) {
          _currentPage++;
        } else {
          _currentPage = 0;
        }
        _pageController.animateToPage(
          _currentPage,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
        );
      }
    });
  }

  Future<void> checkUserType() async {
    final String? userType =
        await HiveService.getData(Constants.USER_TYPE) as String?;

    final user = userType ?? UserType.CITIZEN.name;

    _authController.userType?.value = user;

    await HiveService.setData(
      Constants.USER_TYPE,
      _authController.userType!.value,
    );
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<AuthController>(
      builder: (authController) {
        return OrientationBuilder(
          builder: (context, o) {
            return Scaffold(
              //endDrawer: _buildHomeHeader(context, authController, o),
              appBar: AppBar(
                automaticallyImplyLeading: false,
                title: _buildHomeHeader(context, authController, o),
                elevation: 0.0,
              ),
              body: SizedBox(
                height: Get.height,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SizedBox(height: 20.h),
                    _buildLatestNews(o).paddingSymmetric(
                      horizontal: 16.w,
                    ),
                    SizedBox(height: 50.h),
                    InterText(
                      text: "Identify Yourself",
                      fontWeight: FontWeight.w800,
                      size: o == Orientation.portrait ? 16.sp : 8.sp,
                      color: BaseConfig.textColor2,
                    ).paddingSymmetric(
                      horizontal: 16.w,
                    ),
                    SizedBox(height: 5.h),
                    const Divider(
                      color: BaseConfig.appThemeColor1,
                      thickness: 0.5,
                    ),
                    SizedBox(height: 10.h),
                    Row(
                      children: [
                        Expanded(
                          child: SizedBox(
                            height: 80.h,
                            child: gradientBtn(
                              text: 'Citizen',
                              buttonColor: Colors.white,
                              horizonPadding: 5,
                              textColor: BaseConfig.appThemeColor1,
                              onPressed: () async {
                                await HiveService.deleteData(
                                  HiveConstants.EMP_LOGIN_KEY, //Token Delete
                                );

                                _authController.userType?.value =
                                    UserType.CITIZEN.name;

                                await HiveService.setData(
                                  Constants.USER_TYPE,
                                  _authController.userType?.value,
                                );

                                Get.toNamed(AppRoutes.LOGIN);
                              },
                            ),
                          ),
                        ),
                        Expanded(
                          child: SizedBox(
                            height: 80.h,
                            child: gradientBtn(
                              text: 'Business',
                              buttonColor: Colors.white,
                              horizonPadding: 5,
                              textColor: BaseConfig.appThemeColor1,
                              onPressed: () {
                                Get.toNamed(AppRoutes.SELECT_CATEGORY);
                              },
                            ),
                          ),
                        ),
                      ],
                    ).paddingSymmetric(
                      horizontal: 16.w,
                    ),
                    SizedBox(height: 10.h),
                    Row(
                      children: [
                        Expanded(
                          child: SizedBox(
                            height: 80.h,
                            child: gradientBtn(
                              text: 'Govt. Official',
                              buttonColor: Colors.white,
                              horizonPadding: 5,
                              textColor: BaseConfig.appThemeColor1,
                              onPressed: () async {
                                await HiveService.deleteData(
                                  HiveConstants.EMP_LOGIN_KEY, //Token Delete
                                );

                                _authController.userType?.value =
                                    UserType.EMPLOYEE.name;

                                await HiveService.setData(
                                  Constants.USER_TYPE,
                                  _authController.userType?.value,
                                );

                                Get.toNamed(AppRoutes.EMP_LOGIN);
                              },
                            ),
                          ),
                        ),
                        Expanded(
                          child: SizedBox(
                            height: 80.h,
                            child: gradientBtn(
                              text: 'Visitor',
                              buttonColor: Colors.white,
                              horizonPadding: 5,
                              textColor: BaseConfig.appThemeColor1,
                              onPressed: () async {
                                await HiveService.setData(
                                  HiveConstants.SKIP_BUTTON,
                                  true,
                                );
                                Get.toNamed(AppRoutes.VISITOR);
                              },
                            ),
                          ),
                        ),
                      ],
                    ).paddingSymmetric(
                      horizontal: 16.w,
                    ),
                  ],
                ),
              ),
              bottomNavigationBar: Container(
                color: BaseConfig.shadeAmber,
                height: o == Orientation.portrait
                    ? Platform.isIOS
                        ? 88.h
                        : 75.h
                    : Platform.isIOS
                        ? 88.h
                        : 98.h,
                child: const Center(
                  child: SmallTextNotoSans(
                    text:
                        "Copyright © 2022 National Institute of Urban Affairs",
                    color: BaseConfig.appThemeColor1,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            );
          },
        );
      },
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
        ImagePlaceHolder(
          radius: 20.r,
          iconSize: 30,
          height: 155.h,
          width: 155.w,
          padding: EdgeInsets.zero,
          iconColor: BaseConfig.greyColor1,
        ),
      ],
    );
  }

  Widget _buildLatestNews(Orientation o) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(height: 18.h),
        SizedBox(
          height: o == Orientation.portrait ? 210.h : 220.h,
          child: PageView.builder(
            controller: _pageController,
            itemCount: BaseConfig.APP_LOGIN_BANNERS_V2.split(',').length,
            onPageChanged: (int index) {
              setState(() {
                _currentPage = index; // Update state safely
              });
              _autoPlayTimer?.cancel();
              _startAutoPlay(); // Restart autoplay
            },
            itemBuilder: (BuildContext context, int index) {
              final img = BaseConfig.APP_LOGIN_BANNERS_V2.split(',')[index];
              final imgText =
                  BaseConfig.APP_LOGIN_BANNERS_TEXT.split(',')[index];
              return FractionallySizedBox(
                widthFactor: 1 / _pageController.viewportFraction,
                child: NewsCardWithoutText(
                  img: img,
                  orientation: o,
                  imgText: imgText,
                ),
              );
            },
          ),
        ),
        SizedBox(
          height: 12.h,
        ),
        SmoothPageIndicator(
          controller: _pageController,
          count: BaseConfig.APP_LOGIN_BANNERS_V2.split(',').length,
          effect: ExpandingDotsEffect(
            dotHeight: 6.h,
            dotWidth: o == Orientation.portrait ? 6.w : 3.w,
            expansionFactor: 2,
            spacing: 4.w,
            activeDotColor: BaseConfig.appThemeColor1,
            dotColor: BaseConfig.dotGrayColor,
          ),
        ),
      ],
    );
  }

  Future<void> _selectLanguage() async {
    // Check if a language has been previously selected
    final selectedLanguageIndex =
        await HiveService.getData(Constants.LANG_SELECTION_INDEX);
    dPrint("Retrieved language index: $selectedLanguageIndex");
    // If a language is already selected, skip showing the dialog
    if (selectedLanguageIndex != null) {
      dPrint("Language already selected, skipping dialog.");
      return;
    }

    await Get.dialog(
      AlertDialog(
        insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
        contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.r),
        ),
        backgroundColor: BaseConfig.mainBackgroundColor,
        content: SizedBox(
          width: Get.width,
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                SizedBox(height: 10.h),
                MediumTextNotoSans(
                  text: 'Select Preferred Language',
                  fontWeight: FontWeight.w700,
                  size: 16.h,
                ),
                SizedBox(
                  height: 8.h,
                ),
                SmallTextNotoSans(
                  text:
                      'Please select your preferred language or english will be considered the default selected language.',
                  fontWeight: FontWeight.w400,
                  size: 11.h,
                ),
                SizedBox(
                  height: 16.h,
                ),
                StreamBuilder(
                  stream: _languageController.streamController.stream,
                  builder: (context, AsyncSnapshot snapshot) {
                    if (snapshot.hasData && snapshot.data != 'error') {
                      List<StateInfo> stateInfo = snapshot.data;
                      return Column(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          for (int i = 0;
                              i < stateInfo.first.languages!.length;
                              i++)
                            Obx(
                              () => SizedBox(
                                height: 50.h,
                                child: RadioListTile<String>(
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(8.r),
                                  ),
                                  title: MediumTextNotoSans(
                                    text:
                                        '${stateInfo.first.languages![i].label}'
                                                .capitalize ??
                                            '',
                                    fontWeight: FontWeight.w400,
                                  ),
                                  contentPadding: EdgeInsetsDirectional.zero,
                                  value: stateInfo.first.languages![i].label
                                      .toString(),
                                  groupValue: _languageController
                                      .selectedAppLanguage.value,
                                  activeColor: BaseConfig.appThemeColor1,
                                  onChanged: (value) {
                                    if (value != null) {
                                      _languageController
                                          .selectedAppLanguage.value = value;
                                    }
                                  },
                                ),
                              ),
                            ),
                          const SizedBox(height: 20),
                          const Divider(color: BaseConfig.borderColor),
                          SizedBox(height: 16.h),
                          Row(
                            children: [
                              // Expanded(
                              //   child: SizedBox(
                              //     height: 44.h,
                              //     child: gradientBtn(
                              //       text: 'Cancel',
                              //       buttonColor: Colors.white,
                              //       horizonPadding: 5,
                              //       textColor: BaseConfig.appThemeColor1,
                              //       onPressed: () {
                              //         Get.back();
                              //       },
                              //     ),
                              //   ),
                              // ),
                              Expanded(
                                child: SizedBox(
                                  height: 44.h,
                                  child: gradientBtn(
                                    text:
                                        getLocalizedString(i18.common.CONTINUE),
                                    horizonPadding: 5,
                                    onPressed: () async {
                                      final selectedLanguageIndex =
                                          stateInfo.first.languages!.indexWhere(
                                        (lang) =>
                                            lang.label ==
                                            _languageController
                                                .selectedAppLanguage.value,
                                      );
                                      if (selectedLanguageIndex != -1) {
                                        await HiveService.setData(
                                          Constants.LANG_SELECTION_INDEX,
                                          selectedLanguageIndex,
                                        );
                                        await HiveService.setData(
                                          Constants.TENANT_ID,
                                          stateInfo.first.code,
                                        );
                                        _languageController
                                            .onSelectionOfLanguage(
                                          stateInfo.first.languages![
                                              selectedLanguageIndex],
                                          stateInfo.first.languages!,
                                          selectedLanguageIndex,
                                        );
                                        _languageController
                                            .getLocalizationData();
                                      }
                                      print("Selected language index value");
                                      print(selectedLanguageIndex);
                                      Get.back();
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ],
                      );
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => _languageController.getLocalizationData(),
                      );
                    } else {
                      switch (snapshot.connectionState) {
                        case ConnectionState.waiting:
                        case ConnectionState.active:
                          return showCircularIndicator();
                        default:
                          return const SizedBox.shrink();
                      }
                    }
                  },
                ),
              ],
            ),
          ),
        ),
      ),
      barrierDismissible: false,
    );
  }
}
