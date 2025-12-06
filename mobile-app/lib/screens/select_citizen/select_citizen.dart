// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';
// import 'package:flutter_screenutil/flutter_screenutil.dart';
// import 'package:get/get.dart';
// import 'package:mobile_app/components/error_page/network_error.dart';
// import 'package:mobile_app/components/gradient_btn.dart';
// import 'package:mobile_app/config/base_config.dart';
// import 'package:mobile_app/controller/auth_controller.dart';
// import 'package:mobile_app/controller/common_controller.dart';
// import 'package:mobile_app/controller/language_controller.dart';
// import 'package:mobile_app/controller/location_controller.dart';
// import 'package:mobile_app/model/citizen/localization/language.dart';
// import 'package:mobile_app/routes/routes.dart';
// import 'package:mobile_app/services/hive_services.dart';
// import 'package:mobile_app/utils/constants/constants.dart';
// import 'package:mobile_app/utils/constants/i18_key_constants.dart';
// import 'package:mobile_app/utils/enums/app_enums.dart';
// import 'package:mobile_app/utils/extension/extension.dart';
// import 'package:mobile_app/utils/utils.dart';
// import 'package:mobile_app/widgets/big_text.dart';
// import 'package:mobile_app/widgets/medium_text.dart';
// import 'package:mobile_app/widgets/slider_image_widget.dart';
// import 'package:mobile_app/widgets/small_text.dart';
//
// class SelectCitizen extends StatefulWidget {
//   const SelectCitizen({super.key});
//
//   @override
//   State<SelectCitizen> createState() => _SelectCitizenState();
// }
//
// class _SelectCitizenState extends State<SelectCitizen> {
//   final _languageController = Get.find<LanguageController>();
//   final _authController = Get.find<AuthController>();
//
//   @override
//   void initState() {
//     SystemChrome.setEnabledSystemUIMode(
//       SystemUiMode.manual,
//       overlays: [
//         SystemUiOverlay.top,
//         SystemUiOverlay.bottom,
//       ],
//     );
//     super.initState();
//     checkUserType();
//     WidgetsBinding.instance.addPostFrameCallback((_) => _selectLanguage());
//     _languageController.getLocalizationData();
//   }
//
//   Future<void> checkUserType() async {
//     final String? userType =
//         await HiveService.getData(Constants.USER_TYPE) as String?;
//
//     final user = userType ?? UserType.CITIZEN.name;
//
//     _authController.userType?.value = user;
//
//     await HiveService.setData(
//       Constants.USER_TYPE,
//       _authController.userType!.value,
//     );
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: OrientationBuilder(
//         builder: (context, orientation) {
//           if (orientation == Orientation.portrait) {
//             return SizedBox(
//               height: Get.height,
//               width: Get.width,
//               child: Stack(
//                 children: [
//                   const SliderImageWidget(),
//                   Positioned(
//                     top: 50.h,
//                     left: 25.w,
//                     child: Image.asset(
//                       BaseConfig.upyogBannerIconPng,
//                       width: 60,
//                       fit: BoxFit.fill,
//                     ),
//                   ),
//                   Positioned(
//                     top: 315.h,
//                     child: Container(
//                       height: Get.height,
//                       width: Get.width,
//                       decoration: BoxDecoration(
//                         color: Colors.white,
//                         borderRadius: BorderRadius.circular(25.r),
//                       ),
//                       child: Padding(
//                         padding: EdgeInsets.all(20.0.h),
//                         child: _buildView(),
//                       ),
//                     ),
//                   ),
//                 ],
//               ),
//             );
//           } else {
//             return Row(
//               children: [
//                 Expanded(
//                   child: Stack(
//                     children: [
//                       SliderImagePortraitWidget(
//                         width: Get.width,
//                       ),
//                       Positioned(
//                         top: 50.h,
//                         left: 20.w,
//                         child: Image.asset(
//                           BaseConfig.upyogBannerIconPng,
//                           width: 60,
//                           fit: BoxFit.fill,
//                         ),
//                       ),
//                     ],
//                   ).marginOnly(
//                     right: 4.w,
//                   ),
//                 ),
//                 Expanded(
//                   child: Container(
//                     height: Get.height,
//                     decoration: BoxDecoration(
//                       color: Colors.white,
//                       borderRadius: BorderRadius.circular(25.r),
//                     ),
//                     child: SingleChildScrollView(
//                       child: Padding(
//                         padding: EdgeInsets.all(16.w),
//                         child: _buildViewLand(),
//                       ),
//                     ),
//                   ),
//                 ),
//               ],
//             );
//           }
//         },
//       ),
//     );
//   }
//
//   Widget _buildView() {
//     return Column(
//       mainAxisAlignment: MainAxisAlignment.spaceBetween,
//       mainAxisSize: MainAxisSize.min,
//       children: [
//         Expanded(
//           child: Column(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: [
//               SizedBox(height: 20.h),
//               MediumTextNotoSans(
//                 text: 'I am a',
//                 fontWeight: FontWeight.w700,
//                 size: 16.sp,
//               ),
//               SizedBox(height: 16.h),
//               SmallTextNotoSans(
//                 text: 'Please select your user category.',
//                 fontWeight: FontWeight.w500,
//                 size: 11.sp,
//                 color: BaseConfig.greyColor3,
//               ),
//               SizedBox(height: 16.h),
//               Obx(
//                 () => SizedBox(
//                   height: 120.h,
//                   child: ListView(
//                     shrinkWrap: true,
//                     padding: EdgeInsets.zero,
//                     children: [
//                       RadioListTile<String>(
//                         title: MediumTextNotoSans(
//                           text: getLocalizedString(i18.common.SELECT_CITIZEN),
//                           fontWeight: FontWeight.w400,
//                           size: 14.sp,
//                         ),
//                         contentPadding: EdgeInsetsDirectional.zero,
//                         value: UserType.CITIZEN.name,
//                         groupValue: _authController.userType?.value,
//                         activeColor: BaseConfig.appThemeColor1,
//                         onChanged: (value) async {
//                           await HiveService.deleteData(
//                             HiveConstants.EMP_LOGIN_KEY,
//                           );
//                           _authController.userType?.value = value!;
//                           await HiveService.setData(
//                             Constants.USER_TYPE,
//                             value!,
//                           );
//                         },
//                       ),
//                       RadioListTile<String>(
//                         title: MediumTextNotoSans(
//                           text: getLocalizedString(i18.common.SELECT_EMPLOYEE),
//                           fontWeight: FontWeight.w400,
//                           size: 14.sp,
//                         ),
//                         value: UserType.EMPLOYEE.name,
//                         contentPadding: EdgeInsetsDirectional.zero,
//                         groupValue: _authController.userType?.value,
//                         activeColor: BaseConfig.appThemeColor1,
//                         onChanged: (value) async {
//                           await HiveService.deleteData(HiveConstants.LOGIN_KEY);
//                           _authController.userType?.value = value!;
//                           Get.find<CityController>().empSelectedCity.value = "";
//                           await HiveService.setData(
//                             Constants.USER_TYPE,
//                             value!,
//                           );
//                         },
//                       ),
//                     ],
//                   ),
//                 ),
//               ),
//             ],
//           ),
//         ),
//         Expanded(
//           child: Column(
//             children: [
//               gradientBtn(
//                 text: getLocalizedString(i18.common.NEXT),
//                 fontSize: 16.sp,
//                 width: Get.width,
//                 fontWeight: FontWeight.w700,
//                 horizonPadding: 0,
//                 onPressed: () {
//                   if (_authController.userType == null) {
//                     snackBar(
//                       'Error',
//                       'Please select user type',
//                       Colors.red,
//                     );
//                     return;
//                   }
//                   if (_authController.userType?.value ==
//                       UserType.CITIZEN.name) {
//                     _authController.otpEditingController.value.text = '';
//                     _authController.isButtonEnabled.value = false;
//                     _authController.isOtpValid.value = false;
//                     _authController.isNumberValid.value = false;
//                     _authController.disableNumberField.value = false;
//                     print("Testing OTP Text field");
//                     print(_authController.otpEditingController.value.text);
//                     Get.toNamed(AppRoutes.LOGIN);
//                   } else {
//                     Get.toNamed(AppRoutes.EMP_LOGIN);
//                   }
//                 },
//               ),
//             ],
//           ),
//         ),
//         SizedBox(height: 20.h),
//       ],
//     );
//   }
//
//   Widget _buildViewLand() {
//     return SingleChildScrollView(
//       physics: const NeverScrollableScrollPhysics(),
//       child: Column(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         children: [
//           BigTextNotoSans(
//             text: 'I am a',
//             fontWeight: FontWeight.w700,
//             size: 8.sp,
//           ),
//           SizedBox(
//             height: 16.h,
//           ),
//           SmallTextNotoSans(
//             text: 'Please select your user category.',
//             fontWeight: FontWeight.w500,
//             size: 7.sp,
//             color: BaseConfig.greyColor3,
//           ),
//           SizedBox(
//             height: 16.h,
//           ),
//           Obx(
//             () => Column(
//               crossAxisAlignment: CrossAxisAlignment.start,
//               mainAxisAlignment: MainAxisAlignment.start,
//               mainAxisSize: MainAxisSize.min,
//               children: [
//                 RadioListTile<String>(
//                   title: MediumTextNotoSans(
//                     text: getLocalizedString(i18.common.SELECT_CITIZEN),
//                     fontWeight: FontWeight.w400,
//                     size: 7.sp,
//                   ),
//                   contentPadding: EdgeInsetsDirectional.zero,
//                   value: UserType.CITIZEN.name,
//                   groupValue: _authController.userType?.value,
//                   activeColor: BaseConfig.appThemeColor1,
//                   onChanged: (value) async {
//                     _authController.userType?.value = value!;
//                     await HiveService.setData(
//                       Constants.USER_TYPE,
//                       value!,
//                     );
//                   },
//                 ),
//                 RadioListTile<String>(
//                   title: MediumTextNotoSans(
//                     text: getLocalizedString(i18.common.SELECT_EMPLOYEE),
//                     fontWeight: FontWeight.w400,
//                     size: 7.sp,
//                   ),
//                   value: UserType.EMPLOYEE.name,
//                   contentPadding: EdgeInsetsDirectional.zero,
//                   groupValue: _authController.userType?.value,
//                   activeColor: BaseConfig.appThemeColor1,
//                   onChanged: (value) async {
//                     _authController.userType?.value = value!;
//                     await HiveService.setData(
//                       Constants.USER_TYPE,
//                       value!,
//                     );
//                   },
//                 ),
//               ],
//             ),
//           ),
//           SizedBox(height: 50.h),
//           gradientBtn(
//             text: getLocalizedString(i18.common.NEXT),
//             fontSize: 9.sp,
//             width: Get.width,
//             horizonPadding: 0,
//             onPressed: () {
//               if (_authController.userType == null) {
//                 snackBar(
//                   'Error',
//                   'Please select user type',
//                   Colors.red,
//                 );
//                 return;
//               }
//               if (_authController.userType?.value == UserType.CITIZEN.name) {
//                 Get.toNamed(AppRoutes.LOGIN);
//               } else {
//                 Get.offNamed(AppRoutes.EMP_LOGIN);
//               }
//             },
//           ),
//           //Don't have an account? Sign Up
//           SizedBox(
//             height: 10.h,
//           ),
//           Row(
//             mainAxisAlignment: MainAxisAlignment.center,
//             children: [
//               MediumTextNotoSans(
//                 text: "Don't have an account? ",
//                 fontWeight: FontWeight.w400,
//                 size: 6.sp,
//                 color: BaseConfig.greyColor3,
//               ),
//               MediumTextNotoSans(
//                 text: getLocalizedString(i18.common.SIGNUP_REGISTER_HEADING),
//                 fontWeight: FontWeight.w600,
//                 size: 6.sp,
//                 textDecoration: TextDecoration.underline,
//                 color: BaseConfig.appThemeColor1,
//               ).ripple(() {
//                 _authController.mobileNoController.value.text = '';
//                 _authController.nameController.value.text = '';
//                 _authController.dobController.value.text = '';
//                 Get.toNamed(AppRoutes.SIGN_UP);
//               }),
//             ],
//           ),
//         ],
//       ),
//     );
//   }
//
//   Future<void> _selectLanguage() async {
//     // Check if a language has been previously selected
//     final selectedLanguageIndex =
//         await HiveService.getData(Constants.LANG_SELECTION_INDEX);
//     print("Retrieved language index: $selectedLanguageIndex");
//     // If a language is already selected, skip showing the dialog
//     if (selectedLanguageIndex != null) {
//       print("Language already selected, skipping dialog.");
//       return;
//     }
//
//     await Get.dialog(
//       AlertDialog(
//         insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
//         contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
//         shape: RoundedRectangleBorder(
//           borderRadius: BorderRadius.circular(8.r),
//         ),
//         backgroundColor: BaseConfig.mainBackgroundColor,
//         content: SizedBox(
//           width: Get.width,
//           child: SingleChildScrollView(
//             child: Column(
//               crossAxisAlignment: CrossAxisAlignment.start,
//               children: <Widget>[
//                 SizedBox(height: 10.h),
//                 MediumTextNotoSans(
//                   text: 'Select Preferred Language',
//                   fontWeight: FontWeight.w700,
//                   size: 16.h,
//                 ),
//                 SizedBox(
//                   height: 8.h,
//                 ),
//                 SmallTextNotoSans(
//                   text:
//                       'Please select your preferred language or english will be considered the default selected language.',
//                   fontWeight: FontWeight.w400,
//                   size: 11.h,
//                 ),
//                 SizedBox(
//                   height: 16.h,
//                 ),
//                 StreamBuilder(
//                   stream: _languageController.streamController.stream,
//                   builder: (context, AsyncSnapshot snapshot) {
//                     if (snapshot.hasData && snapshot.data != 'error') {
//                       List<StateInfo> stateInfo = snapshot.data;
//                       return Column(
//                         crossAxisAlignment: CrossAxisAlignment.center,
//                         children: [
//                           for (int i = 0;
//                               i < stateInfo.first.languages!.length;
//                               i++)
//                             Obx(
//                               () => SizedBox(
//                                 height: 50.h,
//                                 child: RadioListTile<String>(
//                                   shape: RoundedRectangleBorder(
//                                     borderRadius: BorderRadius.circular(8.r),
//                                   ),
//                                   title: MediumTextNotoSans(
//                                     text:
//                                         '${stateInfo.first.languages![i].label}'
//                                                 .capitalize ??
//                                             '',
//                                     fontWeight: FontWeight.w400,
//                                   ),
//                                   contentPadding: EdgeInsetsDirectional.zero,
//                                   value: stateInfo.first.languages![i].label
//                                       .toString(),
//                                   groupValue: _languageController
//                                       .selectedAppLanguage.value,
//                                   activeColor: BaseConfig.appThemeColor1,
//                                   onChanged: (value) {
//                                     if (value != null) {
//                                       _languageController
//                                           .selectedAppLanguage.value = value;
//                                     }
//                                   },
//                                 ),
//                               ),
//                             ),
//                           const SizedBox(height: 20),
//                           const Divider(color: BaseConfig.borderColor),
//                           SizedBox(height: 16.h),
//                           Row(
//                             children: [
//                               // Expanded(
//                               //   child: SizedBox(
//                               //     height: 44.h,
//                               //     child: gradientBtn(
//                               //       text: 'Cancel',
//                               //       buttonColor: Colors.white,
//                               //       horizonPadding: 5,
//                               //       textColor: BaseConfig.appThemeColor1,
//                               //       onPressed: () {
//                               //         Get.back();
//                               //       },
//                               //     ),
//                               //   ),
//                               // ),
//                               Expanded(
//                                 child: SizedBox(
//                                   height: 44.h,
//                                   child: gradientBtn(
//                                     text:
//                                         getLocalizedString(i18.common.CONTINUE),
//                                     horizonPadding: 5,
//                                     onPressed: () async {
//                                       final selectedLanguageIndex =
//                                           stateInfo.first.languages!.indexWhere(
//                                         (lang) =>
//                                             lang.label ==
//                                             _languageController
//                                                 .selectedAppLanguage.value,
//                                       );
//                                       if (selectedLanguageIndex != -1) {
//                                         await HiveService.setData(
//                                           Constants.LANG_SELECTION_INDEX,
//                                           selectedLanguageIndex,
//                                         );
//                                         await HiveService.setData(
//                                           Constants.TENANT_ID,
//                                           stateInfo.first.code,
//                                         );
//                                         _languageController
//                                             .onSelectionOfLanguage(
//                                           stateInfo.first.languages![
//                                               selectedLanguageIndex],
//                                           stateInfo.first.languages!,
//                                           selectedLanguageIndex,
//                                         );
//                                         _languageController
//                                             .getLocalizationData();
//                                       }
//                                       print("Selected language index value");
//                                       print(selectedLanguageIndex);
//                                       Get.back();
//                                     },
//                                   ),
//                                 ),
//                               ),
//                             ],
//                           ),
//                         ],
//                       );
//                     } else if (snapshot.hasError) {
//                       return networkErrorPage(
//                         context,
//                         () => _languageController.getLocalizationData(),
//                       );
//                     } else {
//                       switch (snapshot.connectionState) {
//                         case ConnectionState.waiting:
//                         case ConnectionState.active:
//                           return showCircularIndicator();
//                         default:
//                           return const SizedBox.shrink();
//                       }
//                     }
//                   },
//                 ),
//               ],
//             ),
//           ),
//         ),
//       ),
//       barrierDismissible: false,
//     );
//   }
// }
