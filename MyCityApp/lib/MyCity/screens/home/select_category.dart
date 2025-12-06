import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/MyCity/screens/home/calender_home/calender_home.dart';
import 'package:mobile_app/MyCity/widget/header/city_header.dart';
import 'package:mobile_app/MyCity/widget/latest_news/latest_news.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/gradient_btn.dart';
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
import 'package:mobile_app/widgets/small_text.dart';

class SelectCategory extends StatefulWidget {
  const SelectCategory({super.key});

  @override
  State<SelectCategory> createState() => _SelectCategoryState();
}

class _SelectCategoryState extends State<SelectCategory> {
  final _languageController = Get.find<LanguageController>();
  final _authController = Get.find<AuthController>();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _selectLanguage());
    checkUserType();
    _languageController.getLocalizationData();
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

  bool isCalender = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: MyCityHeader(
        title: isCalender ? 'Calender' : 'My City My App',
        isBackButton: isCalender,
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
                  //       onPressed: () {
                  //         //TODO: Voice search
                  //       },
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
                  SizedBox(height: 20.h),
                  const BigTextNotoSans(
                    text: 'Select Category',
                    fontWeight: FontWeight.w800,
                    color: BaseConfig.textColor2,
                  ).paddingSymmetric(
                    vertical: 16.h,
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: () async {
                            await HiveService.deleteData(
                              HiveConstants.EMP_LOGIN_KEY, //Token Delete
                            );

                            _authController.userType?.value =
                                UserType.CITIZEN.name;

                            await HiveService.setData(
                              Constants.USER_TYPE,
                              _authController.userType?.value,
                            );

                            Get.toNamed(AppRoutes.CITIZEN_HOME);
                          },
                          customBorder: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(25.r),
                          ),
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Colors.black,
                                width: 1.5,
                              ),
                              borderRadius: BorderRadius.circular(
                                25,
                              ),
                            ),
                            padding: EdgeInsets.all(8.w),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                SizedBox(
                                  height: 80.h,
                                  width: 60.w,
                                  child: Image.asset(
                                    'assets/images/group.png',
                                    fit: BoxFit.contain,
                                  ),
                                ),
                                SizedBox(
                                  height: 5.h,
                                ),
                                const Center(
                                  child: MediumTextNotoSans(
                                    text: 'CITIZEN',
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                      SizedBox(
                        width: 16.w,
                      ),
                      Expanded(
                        child: InkWell(
                          onTap: () async {
                            await HiveService.deleteData(
                              HiveConstants.EMP_LOGIN_KEY, //Token Delete
                            );

                            _authController.userType?.value =
                                UserType.EMPLOYEE.name;

                            await HiveService.setData(
                              Constants.USER_TYPE,
                              _authController.userType?.value,
                            );

                            Get.toNamed(AppRoutes.BUSINESS_HOME);
                          },
                          customBorder: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(25.r),
                          ),
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Colors.black,
                                width: 1.5,
                              ),
                              borderRadius: BorderRadius.circular(25),
                            ),
                            padding: EdgeInsets.all(8.w),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                SizedBox(
                                  height: 80.h,
                                  width: 60.w,
                                  child: Image.asset(
                                    'assets/images/cooperation.png',
                                    fit: BoxFit.contain,
                                  ),
                                ),
                                SizedBox(
                                  height: 5.h,
                                ),
                                const Center(
                                  child: MediumTextNotoSans(
                                    text: 'BUSINESS',
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
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
                        child: InkWell(
                          onTap: () async {
                            await HiveService.deleteData(
                              HiveConstants.EMP_LOGIN_KEY, //Token Delete
                            );

                            _authController.userType?.value =
                                UserType.CITIZEN.name;

                            await HiveService.setData(
                              Constants.USER_TYPE,
                              _authController.userType?.value,
                            );

                            Get.toNamed(AppRoutes.VISITOR);
                          },
                          customBorder: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(25.r),
                          ),
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Colors.black,
                                width: 1.5,
                              ),
                              borderRadius: BorderRadius.circular(25),
                            ),
                            padding: EdgeInsets.all(8.w),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                SizedBox(
                                  height: 80.h,
                                  width: 60.w,
                                  child: Image.asset(
                                    'assets/images/baggage.png',
                                    fit: BoxFit.contain,
                                  ),
                                ),
                                SizedBox(
                                  height: 5.h,
                                ),
                                const Center(
                                  child: MediumTextNotoSans(
                                    text: 'VISITOR',
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                      SizedBox(
                        width: 16.w,
                      ),
                      Expanded(
                        child: InkWell(
                          onTap: () async {
                            await HiveService.deleteData(
                              HiveConstants.EMP_LOGIN_KEY, //Token Delete
                            );

                            _authController.userType?.value =
                                UserType.EMPLOYEE.name;

                            await HiveService.setData(
                              Constants.USER_TYPE,
                              _authController.userType?.value,
                            );

                            Get.toNamed(AppRoutes.OFFICIAL);
                          },
                          customBorder: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(25.r),
                          ),
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Colors.black,
                                width: 1.5,
                              ),
                              borderRadius: BorderRadius.circular(
                                25,
                              ),
                            ),
                            padding: EdgeInsets.all(8.w),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                SizedBox(
                                  height: 80.h,
                                  width: 60.w,
                                  child: Image.asset(
                                    'assets/images/bureau.png',
                                    fit: BoxFit.contain,
                                  ),
                                ),
                                SizedBox(
                                  height: 5.h,
                                ),
                                const Center(
                                  child: MediumTextNotoSans(
                                    text: 'OFFICIAL',
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
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
