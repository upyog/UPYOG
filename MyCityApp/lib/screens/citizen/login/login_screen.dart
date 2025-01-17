import 'dart:async';

import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/text_form_field.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/platforms/platforms.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/slider_image_widget.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/tabbar_widget.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen>
    with TickerProviderStateMixin {
  final _authController = Get.find<AuthController>();
  final _languageController = Get.find<LanguageController>();
  final _formKey = GlobalKey<FormState>();
  late AnimationController _animationController;
  int _timerSeconds = 30;
  Timer? _timer;
  bool _resendOtp = false;

  @override
  void initState() {
    super.initState();
    if (_authController.mobileNoController.value.text.trim().isNotEmpty) {
      _authController.mobileNoController.value.clear();
    }
    _authController.mobileNoController.value.addListener(_checkFormFilled);

    //OTP
    //  _listenCode();
    _animationController = AnimationController(
      vsync: this,
      duration: Duration(seconds: _timerSeconds),
    )..forward();
  }

  void _checkFormFilled() {
    _authController.mobileNoController.value.text.trim().length == 10;
  }

  Future<void> validateLoginForm() async {
    // if (!_formKey.currentState!.validate()) return;
    if (!_authController.termsCondition.value) {
      snackBar(
        'Terms and Conditions',
        'Please accept the terms and conditions',
        Theme.of(context).colorScheme.error,
      );
      return;
    }
    _authController.isLoading.value = true;
    await startTimer();
    FocusScope.of(context).unfocus();
    await _authController.loginValidate(
      mobile: _authController.mobileNoController.value.text,
    );
    _authController.isLoading.value = false;
  }

  disableTextField() {
    _authController.mobileNoController.value.text = '';
    _authController.nameController.value.text = '';
    _authController.dobController.value.text = '';
    _authController.disableNumberField.value = false;
    _authController.isNumberValid.value = false;
    _authController.isOtpValid.value = false;
  }

  //   void _listenCode() async {
  //   final sig = await SmsAutoFill().getAppSignature;
  //   dPrint('App Signature: $sig');
  //   SmsAutoFill().listenForCode();
  // }

  Future<void> startTimer() async {
    _resendOtp = false;
    _timerSeconds = 30;
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_timerSeconds == 0) {
        setState(() {
          _resendOtp = true; // Enable the resend button
        });
        _timer?.cancel(); // Stop the timer
      } else {
        setState(() {
          _timerSeconds--;
        });
      }
    });
  }

  void _validateOtp() async {
    FocusScope.of(context).unfocus();
    _authController.isLoading.value = true;
    final uType = _authController.userType!.value == 'citizen'
        ? UserType.CITIZEN
        : UserType.EMPLOYEE;
    await _authController.otpValidate(
      phoneNo: _authController.mobileNoController.value.text,
      otp: _authController.otpEditingController.value.text,
      userType: uType,
    );
    if (_timer!.isActive) {
      _timerSeconds = 0;
      _timer?.cancel();
      _resendOtp = false;
    }
    _authController.isLoading.value = false;
  }

  void _resendOTP() async {
    await startTimer();
    await _authController.loginValidate(
      mobile: _authController.mobileNoController.value.text,
    );
  }

  @override
  void dispose() {
    // SmsAutoFill().unregisterListener();
    _timer?.cancel();
    _resendOtp = false;
    _animationController.dispose();
    _authController.disableNumberField.value = false;
    _authController.mobileNoController.value.removeListener(_checkFormFilled);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: OrientationBuilder(
        builder: (context, orientation) {
          if (orientation == Orientation.portrait) {
            return Form(
              key: _formKey,
              autovalidateMode: AutovalidateMode.disabled,
              child: SingleChildScrollView(
                child: SizedBox(
                  height: Get.height,
                  width: Get.width,
                  child: Stack(
                    children: [
                      SliderImageWidget(
                        height: 320.h,
                      ),
                      Positioned(
                        top: 50.h,
                        left: 25.w,
                        child: Image.asset(
                          BaseConfig.upyogBannerIconPng,
                          width: 60,
                          fit: BoxFit.fill,
                        ),
                      ),
                      // Positioned(
                      //   top: 25.h,
                      //   right: 20.w,
                      //   child: Padding(
                      //     padding: EdgeInsets.only(top: 8.h),
                      //     child: TextButton(
                      //       onPressed: () {
                      //         skipButton();
                      //       },
                      //       child: SmallTextNotoSans(
                      //         text: 'Skip',
                      //         color: BaseConfig.appThemeColor1,
                      //         fontWeight: FontWeight.w700,
                      //         size: 14.sp,
                      //       ),
                      //     ),
                      //   ),
                      // ),
                      Positioned(
                        top: 300.h,
                        child: Container(
                          height: Get.height * 0.62,
                          width: Get.width,
                          decoration: BoxDecoration(
                            color: BaseConfig.mainBackgroundColor,
                            borderRadius: BorderRadius.only(
                              topLeft: Radius.circular(24.r),
                              topRight: Radius.circular(24.r),
                            ),
                          ),
                          child: Padding(
                            padding: EdgeInsets.all(10.0.h),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Expanded(
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      SizedBox(height: 10.h),
                                      Row(
                                        children: [
                                          IconButton(
                                            onPressed: () {
                                              _authController.mobileNoController
                                                  .value.text = '';
                                              _authController.nameController
                                                  .value.text = '';
                                              _authController.dobController
                                                  .value.text = '';
                                              _authController.disableNumberField
                                                  .value = false;
                                              _authController
                                                  .isNumberValid.value = false;
                                              _authController.isOtpValid.value =
                                                  false;
                                              Navigator.of(context).pop();
                                            },
                                            icon: Icon(
                                              Icons.arrow_back_ios,
                                              size: 16.h,
                                            ),
                                          ),
                                          MediumTextNotoSans(
                                            text: getLocalizedString(
                                              i18.common.LOGIN,
                                            ),
                                            fontWeight: FontWeight.w700,
                                            size: 16.sp,
                                          ),
                                        ],
                                      ),
                                      SizedBox(height: 10.h),
                                      SmallTextNotoSans(
                                        text:
                                            'Please select how you want to login to the UPYOG application',
                                        fontWeight: FontWeight.w500,
                                        size: 11.sp,
                                        color: BaseConfig.greyColor3,
                                      ),
                                      SizedBox(height: 10.h),
                                      Expanded(
                                        child: TabBarWidget(
                                          tabText1: getLocalizedString(
                                            i18.common.OTP,
                                          ),
                                          tabText2: "Meri Pehchaan",
                                          children: [
                                            SingleChildScrollView(
                                              child: Column(
                                                children: [
                                                  Obx(
                                                    () => Container(
                                                      decoration: BoxDecoration(
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(15),
                                                        color: _authController
                                                                .disableNumberField
                                                                .value
                                                            ? BaseConfig
                                                                .borderColor
                                                            : Colors
                                                                .transparent,
                                                      ),
                                                      child: Stack(
                                                        alignment: Alignment
                                                            .centerRight,
                                                        children: [
                                                          TextFormFieldApp(
                                                            readOnly:
                                                                _authController
                                                                    .disableNumberField
                                                                    .value,
                                                            controller:
                                                                _authController
                                                                    .mobileNoController
                                                                    .value,
                                                            padding: _authController
                                                                    .mobileNoController
                                                                    .value
                                                                    .text
                                                                    .trim()
                                                                    .isNotEmpty
                                                                ? EdgeInsets
                                                                    .fromLTRB(
                                                                    0,
                                                                    20.h,
                                                                    0,
                                                                    0,
                                                                  )
                                                                : EdgeInsets
                                                                    .fromLTRB(
                                                                    0,
                                                                    10.h,
                                                                    0,
                                                                    10.h,
                                                                  ),
                                                            keyboardType:
                                                                AppPlatforms
                                                                    .platformKeyboardType(),
                                                            textInputAction:
                                                                TextInputAction
                                                                    .done,
                                                            label: Text(
                                                              '${getLocalizedString(i18.common.LOGIN_PHONE_HINT_TEXT)} *',
                                                            ),
                                                            inputFormatters: [
                                                              LengthLimitingTextInputFormatter(
                                                                10,
                                                              ),
                                                              FilteringTextInputFormatter
                                                                  .digitsOnly,
                                                            ],
                                                            onChange: (value) {
                                                              _authController
                                                                  .validateNumber(
                                                                value,
                                                              );
                                                            },
                                                            fillColor: _authController
                                                                    .disableNumberField
                                                                    .value
                                                                ? BaseConfig
                                                                    .borderColor
                                                                : BaseConfig
                                                                    .mainBackgroundColor,
                                                            hintTextColor: _authController
                                                                    .disableNumberField
                                                                    .value
                                                                ? BaseConfig
                                                                    .greyColor3
                                                                : BaseConfig
                                                                    .textColor,
                                                          ),
                                                          if (_authController
                                                              .isNumberValid
                                                              .value)
                                                            const Icon(
                                                              Icons.check,
                                                              color: BaseConfig
                                                                  .appThemeColor1,
                                                              size: 25,
                                                            ).paddingOnly(
                                                              right: 15,
                                                            ),
                                                        ],
                                                      ),
                                                    ).marginOnly(bottom: 16.h),
                                                  ),
                                                  Obx(
                                                    () => _authController
                                                            .disableNumberField
                                                            .value
                                                        ? Column(
                                                            crossAxisAlignment:
                                                                CrossAxisAlignment
                                                                    .start,
                                                            children: [
                                                              Container(
                                                                decoration:
                                                                    BoxDecoration(
                                                                  borderRadius:
                                                                      BorderRadius
                                                                          .circular(
                                                                    15,
                                                                  ),
                                                                  color: BaseConfig
                                                                      .mainBackgroundColor,
                                                                ),
                                                                child: Stack(
                                                                  alignment:
                                                                      Alignment
                                                                          .centerRight,
                                                                  children: [
                                                                    TextFormFieldApp(
                                                                      controller: _authController
                                                                          .otpEditingController
                                                                          .value,
                                                                      padding: _authController
                                                                              .otpEditingController
                                                                              .value
                                                                              .text
                                                                              .isNotEmpty
                                                                          ? EdgeInsets
                                                                              .fromLTRB(
                                                                              0,
                                                                              20.h,
                                                                              0,
                                                                              0,
                                                                            )
                                                                          : EdgeInsets
                                                                              .fromLTRB(
                                                                              0,
                                                                              10.h,
                                                                              0,
                                                                              10.h,
                                                                            ),
                                                                      keyboardType:
                                                                          AppPlatforms
                                                                              .platformKeyboardType(),
                                                                      textInputAction:
                                                                          TextInputAction
                                                                              .done,
                                                                      label:
                                                                          const Text(
                                                                        "Enter OTP",
                                                                      ),
                                                                      inputFormatters: [
                                                                        LengthLimitingTextInputFormatter(
                                                                          6,
                                                                        ), // Limit to 6 digits
                                                                        FilteringTextInputFormatter
                                                                            .digitsOnly, // Allow only digits
                                                                      ],
                                                                      onChange:
                                                                          (value) {
                                                                        _authController
                                                                            .validateOtp(value);
                                                                      },
                                                                    ),
                                                                    if (_authController
                                                                        .isOtpValid
                                                                        .value)
                                                                      const Icon(
                                                                        Icons
                                                                            .check,
                                                                        color: BaseConfig
                                                                            .appThemeColor1,
                                                                        size:
                                                                            25,
                                                                      ).paddingOnly(
                                                                        right:
                                                                            15,
                                                                      ),
                                                                  ],
                                                                ),
                                                              ).marginOnly(
                                                                bottom: 16.h,
                                                              ),
                                                              GestureDetector(
                                                                onTap: _resendOtp
                                                                    ? _resendOTP
                                                                    : null,
                                                                child:
                                                                    Container(
                                                                  child: _resendOtp
                                                                      ? MediumTextNotoSans(
                                                                          text:
                                                                              getLocalizedString(
                                                                            i18.otp.RESEND_OTP,
                                                                          ),
                                                                          fontWeight:
                                                                              FontWeight.w600,
                                                                          size:
                                                                              12.sp,
                                                                          color:
                                                                              BaseConfig.appThemeColor1,
                                                                        )
                                                                      : MediumTextNotoSans(
                                                                          text:
                                                                              "${getLocalizedString(i18.otp.RESEND_ANOTHER_OTP)} $_timerSeconds ${getLocalizedString(i18.otp.RESEND_SECONDS)}",
                                                                          fontWeight:
                                                                              FontWeight.w600,
                                                                          size:
                                                                              12.sp,
                                                                          color:
                                                                              BaseConfig.greyColor3,
                                                                        ),
                                                                ),
                                                              ),
                                                            ],
                                                          )
                                                        : const SizedBox
                                                            .shrink(),
                                                  ),
                                                ],
                                              ),
                                            ),
                                            const Center(
                                              child: Text("Meri Pehchaan"),
                                            ),
                                          ],
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                // SizedBox(height: 110.h),

                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    _buildTermsConditions(false),
                                    SizedBox(
                                      width: Get.width,
                                      height: 44.h,
                                      child: Obx(
                                        () => ElevatedButton(
                                          onPressed: _authController
                                                  .isButtonEnabled.value
                                              ? () {
                                                  if (_authController
                                                      .disableNumberField
                                                      .value) {
                                                    _validateOtp();
                                                  } else {
                                                    validateLoginForm();
                                                  }
                                                }
                                              : null,
                                          style: ButtonStyle(
                                            shape: WidgetStateProperty.all<
                                                RoundedRectangleBorder>(
                                              RoundedRectangleBorder(
                                                borderRadius:
                                                    BorderRadius.circular(
                                                  15.r,
                                                ),
                                              ),
                                            ),
                                            backgroundColor: WidgetStateProperty
                                                .resolveWith<Color>(
                                              (Set<WidgetState> states) {
                                                if (states.contains(
                                                  WidgetState.disabled,
                                                )) {
                                                  return BaseConfig
                                                      .appThemeColor1
                                                      .withValues(
                                                    alpha: 0.4,
                                                  );
                                                }
                                                return BaseConfig
                                                    .appThemeColor1;
                                              },
                                            ),
                                          ),
                                          child: _authController.isLoading.value
                                              ? showCircularIndicator(
                                                  color: BaseConfig
                                                      .mainBackgroundColor,
                                                )
                                              : MediumTextNotoSans(
                                                  text: _authController
                                                          .disableNumberField
                                                          .value
                                                      ? getLocalizedString(
                                                          i18.common.LOGIN,
                                                        )
                                                      : getLocalizedString(
                                                          i18.common.OTP_BUTTON,
                                                        ),
                                                  color: Colors.white,
                                                  size: 16.sp,
                                                  fontWeight: FontWeight.w700,
                                                ),
                                        ),
                                      ),
                                    ),
                                    SizedBox(
                                      height: 20.h,
                                    ),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: [
                                        MediumTextNotoSans(
                                          text: "Don't have an account? ",
                                          fontWeight: FontWeight.w400,
                                          size: 12.sp,
                                          color: BaseConfig.greyColor3,
                                        ),
                                        MediumTextNotoSans(
                                          text: getLocalizedString(
                                            i18.common.SIGNUP_REGISTER_HEADING,
                                          ),
                                          fontWeight: FontWeight.w600,
                                          size: 12.sp,
                                          color: BaseConfig.appThemeColor1,
                                        ).ripple(() {
                                          disableTextField();
                                          Get.offAndToNamed(AppRoutes.SIGN_UP);
                                        }),
                                      ],
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          } else {
            return SizedBox(
              height: Get.height,
              width: Get.width,
              child: Form(
                key: _formKey,
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Expanded(
                      child: Stack(
                        children: [
                          SliderImagePortraitWidget(
                            width: Get.width,
                          ),
                          Positioned(
                            top: 50.h,
                            left: 20.w,
                            child: Image.asset(
                              BaseConfig.upyogBannerIconPng,
                              width: 60,
                              fit: BoxFit.fill,
                            ),
                          ),
                          Positioned(
                            top: 25.h,
                            right: 15.w,
                            child: Padding(
                              padding: EdgeInsets.only(top: 8.h),
                              child: TextButton(
                                onPressed: () {
                                  skipButton();
                                },
                                child: SmallTextNotoSans(
                                  text: 'Skip',
                                  color: BaseConfig.appThemeColor1,
                                  fontWeight: FontWeight.w700,
                                  size: 8.sp,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ).marginOnly(
                        right: 4.w,
                      ),
                    ),
                    Expanded(
                      child: SingleChildScrollView(
                        child: Column(
                          children: [
                            Container(
                              padding:
                                  const EdgeInsets.symmetric(horizontal: 10),
                              height: 0.65.sh,
                              child: SingleChildScrollView(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        IconButton(
                                          onPressed: () {
                                            _authController.mobileNoController
                                                .value.text = '';
                                            _authController
                                                .nameController.value.text = '';
                                            _authController
                                                .dobController.value.text = '';
                                            _authController.disableNumberField
                                                .value = false;
                                            _authController
                                                .isNumberValid.value = false;
                                            _authController.isOtpValid.value =
                                                false;
                                            Navigator.of(context).pop();
                                          },
                                          icon: Icon(
                                            Icons.arrow_back_ios,
                                            size: 20.h,
                                          ),
                                        ),
                                        MediumTextNotoSans(
                                          text: getLocalizedString(
                                            i18.common.LOGIN,
                                          ),
                                          fontWeight: FontWeight.w700,
                                          size: 7.sp,
                                        ),
                                      ],
                                    ),
                                    SizedBox(height: 5.h),
                                    SmallTextNotoSans(
                                      text:
                                          'Please select how you want to login to the UPYOG application',
                                      fontWeight: FontWeight.w500,
                                      size: 6.sp,
                                      color: BaseConfig.greyColor3,
                                    ),
                                    SizedBox(height: 10.h),
                                    SizedBox(
                                      height: 0.8.sh,
                                      child: TabBarWidget(
                                        paddingWidth: 3.w,
                                        tabHeight: 50,
                                        tabWidth: 0.5.sw,
                                        tabText1:
                                            getLocalizedString(i18.common.OTP),
                                        tabText2: "Meri Pehchaan",
                                        children: [
                                          SingleChildScrollView(
                                            child: Column(
                                              children: [
                                                Obx(
                                                  () => Container(
                                                    decoration: BoxDecoration(
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                        15,
                                                      ),
                                                      color: _authController
                                                              .disableNumberField
                                                              .value
                                                          ? BaseConfig
                                                              .borderColor
                                                          : Colors.transparent,
                                                    ),
                                                    child: Stack(
                                                      alignment:
                                                          Alignment.centerRight,
                                                      children: [
                                                        TextFormFieldApp(
                                                          readOnly: _authController
                                                              .disableNumberField
                                                              .value,
                                                          controller:
                                                              _authController
                                                                  .mobileNoController
                                                                  .value,
                                                          padding: _authController
                                                                  .mobileNoController
                                                                  .value
                                                                  .text
                                                                  .trim()
                                                                  .isNotEmpty
                                                              ? EdgeInsets
                                                                  .fromLTRB(
                                                                  0,
                                                                  20.h,
                                                                  0,
                                                                  0,
                                                                )
                                                              : EdgeInsets
                                                                  .fromLTRB(
                                                                  0,
                                                                  10.h,
                                                                  0,
                                                                  10.h,
                                                                ),
                                                          keyboardType: AppPlatforms
                                                              .platformKeyboardType(),
                                                          textInputAction:
                                                              TextInputAction
                                                                  .done,
                                                          label: Text(
                                                            '${getLocalizedString(i18.common.LOGIN_PHONE_HINT_TEXT)} *',
                                                          ),
                                                          hintFontSize: 7.sp,
                                                          inputFormatters: [
                                                            LengthLimitingTextInputFormatter(
                                                              10,
                                                            ), // Limit to 10 digits
                                                            FilteringTextInputFormatter
                                                                .digitsOnly, // Allow only digits
                                                          ],
                                                          onChange: (value) {
                                                            _authController
                                                                .validateNumber(
                                                              value,
                                                            );
                                                          },
                                                          fillColor: _authController
                                                                  .disableNumberField
                                                                  .value
                                                              ? BaseConfig
                                                                  .borderColor
                                                              : BaseConfig
                                                                  .mainBackgroundColor,
                                                          hintTextColor:
                                                              _authController
                                                                      .disableNumberField
                                                                      .value
                                                                  ? BaseConfig
                                                                      .greyColor3
                                                                  : BaseConfig
                                                                      .textColor,
                                                        ),
                                                        if (_authController
                                                            .isNumberValid
                                                            .value)
                                                          const Icon(
                                                            Icons.check,
                                                            color: BaseConfig
                                                                .appThemeColor1,
                                                            size: 25,
                                                          ).paddingOnly(
                                                            right: 15,
                                                          ),
                                                      ],
                                                    ),
                                                  ).marginOnly(bottom: 12.h),
                                                ),
                                                Obx(
                                                  () => _authController
                                                          .disableNumberField
                                                          .value
                                                      ? Column(
                                                          crossAxisAlignment:
                                                              CrossAxisAlignment
                                                                  .start,
                                                          children: [
                                                            Container(
                                                              decoration:
                                                                  BoxDecoration(
                                                                borderRadius:
                                                                    BorderRadius
                                                                        .circular(
                                                                  15,
                                                                ),
                                                                color: BaseConfig
                                                                    .mainBackgroundColor,
                                                              ),
                                                              child: Stack(
                                                                alignment: Alignment
                                                                    .centerRight,
                                                                children: [
                                                                  TextFormFieldApp(
                                                                    controller:
                                                                        _authController
                                                                            .otpEditingController
                                                                            .value,
                                                                    padding: _authController
                                                                            .otpEditingController
                                                                            .value
                                                                            .text
                                                                            .isNotEmpty
                                                                        ? EdgeInsets
                                                                            .fromLTRB(
                                                                            0,
                                                                            20.h,
                                                                            0,
                                                                            0,
                                                                          )
                                                                        : EdgeInsets
                                                                            .fromLTRB(
                                                                            0,
                                                                            10.h,
                                                                            0,
                                                                            10.h,
                                                                          ),
                                                                    keyboardType:
                                                                        AppPlatforms
                                                                            .platformKeyboardType(),
                                                                    textInputAction:
                                                                        TextInputAction
                                                                            .done,
                                                                    label:
                                                                        const Text(
                                                                      "Enter OTP",
                                                                    ),
                                                                    hintFontSize:
                                                                        7.sp,
                                                                    inputFormatters: [
                                                                      LengthLimitingTextInputFormatter(
                                                                        6,
                                                                      ), // Limit to 6 digits
                                                                      FilteringTextInputFormatter
                                                                          .digitsOnly, // Allow only digits
                                                                    ],
                                                                    onChange:
                                                                        (value) {
                                                                      _authController
                                                                          .validateOtp(
                                                                        value,
                                                                      );
                                                                    },
                                                                  ),
                                                                  if (_authController
                                                                      .isOtpValid
                                                                      .value)
                                                                    const Icon(
                                                                      Icons
                                                                          .check,
                                                                      color: BaseConfig
                                                                          .appThemeColor1,
                                                                      size: 25,
                                                                    ).paddingOnly(
                                                                      right: 15,
                                                                    ),
                                                                ],
                                                              ),
                                                            ).marginOnly(
                                                              bottom: 16.h,
                                                            ),
                                                            GestureDetector(
                                                              onTap: _resendOtp
                                                                  ? _resendOTP
                                                                  : null,
                                                              child: Container(
                                                                child: _resendOtp
                                                                    ? MediumTextNotoSans(
                                                                        text:
                                                                            getLocalizedString(
                                                                          i18.otp
                                                                              .RESEND_OTP,
                                                                        ),
                                                                        fontWeight:
                                                                            FontWeight.w600,
                                                                        size: 7
                                                                            .sp,
                                                                        color: BaseConfig
                                                                            .appThemeColor1,
                                                                      )
                                                                    : MediumTextNotoSans(
                                                                        text:
                                                                            "${getLocalizedString(i18.otp.RESEND_ANOTHER_OTP)} $_timerSeconds ${getLocalizedString(i18.otp.RESEND_SECONDS)}",
                                                                        fontWeight:
                                                                            FontWeight.w600,
                                                                        size: 7
                                                                            .sp,
                                                                        color: BaseConfig
                                                                            .greyColor3,
                                                                      ),
                                                              ),
                                                            ),
                                                          ],
                                                        )
                                                      : const SizedBox.shrink(),
                                                ),
                                              ],
                                            ),
                                          ),
                                          const Center(
                                            child: Text("Meri Pehchaan"),
                                          ),
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                _buildTermsConditions(true),
                                Container(
                                  width: Get.width,
                                  height: 44.h,
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 10,
                                  ),
                                  child: Obx(
                                    () => ElevatedButton(
                                      onPressed:
                                          _authController.isButtonEnabled.value
                                              ? () {
                                                  if (_authController
                                                      .disableNumberField
                                                      .value) {
                                                    _validateOtp();
                                                  } else {
                                                    validateLoginForm();
                                                  }
                                                }
                                              : null,
                                      style: ButtonStyle(
                                        shape: WidgetStateProperty.all<
                                            RoundedRectangleBorder>(
                                          RoundedRectangleBorder(
                                            borderRadius: BorderRadius.circular(
                                              15.r,
                                            ),
                                          ),
                                        ),
                                        backgroundColor: WidgetStateProperty
                                            .resolveWith<Color>(
                                          (Set<WidgetState> states) {
                                            if (states.contains(
                                              WidgetState.disabled,
                                            )) {
                                              return BaseConfig.appThemeColor1
                                                  .withValues(
                                                alpha: 0.4,
                                              );
                                            }
                                            return BaseConfig.appThemeColor1;
                                          },
                                        ),
                                      ),
                                      child: _authController.isLoading.value
                                          ? showCircularIndicator(
                                              color: BaseConfig
                                                  .mainBackgroundColor,
                                            )
                                          : MediumTextNotoSans(
                                              text: _authController
                                                      .disableNumberField.value
                                                  ? getLocalizedString(
                                                      i18.common.LOGIN,
                                                    )
                                                  : getLocalizedString(
                                                      i18.common.OTP_BUTTON,
                                                    ),
                                              color: Colors.white,
                                              size: 8.sp,
                                              fontWeight: FontWeight.w700,
                                            ),
                                    ),
                                  ),
                                ),
                                SizedBox(
                                  height: 10.h,
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    MediumTextNotoSans(
                                      text: getLocalizedString(
                                        i18.common.DONT_HAVE_ACC,
                                      ),
                                      fontWeight: FontWeight.w400,
                                      size: 6.sp,
                                      color: BaseConfig.greyColor3,
                                    ),
                                    MediumTextNotoSans(
                                      text: getLocalizedString(
                                        i18.common.SIGNUP_REGISTER_HEADING,
                                      ),
                                      fontWeight: FontWeight.w600,
                                      size: 6.sp,
                                      color: BaseConfig.appThemeColor1,
                                    ).ripple(() {
                                      disableTextField();
                                      Get.offAndToNamed(AppRoutes.SIGN_UP);
                                    }),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            );
          }
        },
      ),
    );
  }

  Widget _buildTermsConditions(bool isLandscape) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Obx(
          () => Checkbox.adaptive(
            value: _authController.termsCondition.value,
            onChanged: (value) {
              if (value != null) {
                _authController.termsCondition.value = value;
                _authController.onTermsConditionChanged(value);
              }
            },
            activeColor: BaseConfig.appThemeColor1,
          ),
        ),
        Expanded(
          child: RichText(
            text: TextSpan(
              children: <TextSpan>[
                TextSpan(
                  text: getLocalizedString(i18.common.LOGIN_AGREE_TERM),
                  style: TextStyle(
                    color: BaseConfig.textColor,
                    fontSize: isLandscape ? 6.sp : 12.sp,
                    fontWeight: FontWeight.w400,
                  ),
                ),
                TextSpan(
                  text: getLocalizedString(i18.common.LOGIN_CONDITION),
                  //text: getLocalizedString(i18.login.PRIVACY_POLICY),
                  style: TextStyle(
                    color: BaseConfig.appThemeColor1,
                    decoration: TextDecoration.underline,
                    fontSize: isLandscape ? 6.sp : 12.sp,
                    fontWeight: FontWeight.w500,
                  ),
                  recognizer: TapGestureRecognizer()
                    ..onTap = () {
                      _authController.isLoading.value = false;
                      showWebViewDialogue(
                        context,
                        url: _languageController
                            .citizenConsentForm.checkBoxLabels!.first.enIn!,
                        enablePrivacyTitleSize: true,
                      );
                    },
                ),
              ],
            ),
          ),
        ),
      ],
    ).paddingSymmetric(horizontal: 8);
  }
}
