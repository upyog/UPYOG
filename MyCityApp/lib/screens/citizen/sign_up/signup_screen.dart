import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';
import 'package:mobile_app/components/text_form_field.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/slider_image_widget.dart';
import 'package:mobile_app/widgets/small_text.dart';

class SignUpScreen extends StatefulWidget {
  const SignUpScreen({super.key});

  @override
  State<SignUpScreen> createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  final _formKey = GlobalKey<FormState>();

  final _authController = Get.find<AuthController>();

  // final authController = Get.find<AuthController>();

  DateTime? selectedDate;
  String formattedDate = '';

  Future<void> selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      builder: (BuildContext context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: const ColorScheme.light(
              primary: BaseConfig.appThemeColor1,
            ),
          ),
          child: child!,
        );
      },
      initialDate: selectedDate ?? DateTime.now(),
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
    );
    if (picked == null) return;
    if (picked != selectedDate) {
      setState(() {
        selectedDate = picked;
        formattedDate = formatDate(picked, format: 'dd-MM-yyyy');
        _authController.dobController.value.text = formattedDate;
      });
    }
  }

  Future<void> validatedForm() async {
    if (!_formKey.currentState!.validate()) return;
    DateFormat currentFormat = DateFormat('dd-MM-yyyy');
    DateFormat desiredFormat = DateFormat('yyyy-MM-dd');
    DateTime dob =
        currentFormat.parse(_authController.dobController.value.text);
    String formattedDob = desiredFormat.format(dob);

    _authController.signUpOTP(
      mobile: _authController.mobileNoController.value.text,
      dob: formattedDob,
      name: _authController.nameController.value.text,
    );
  }

  Future<void> validateSignUpOtp() async {
    _authController.createUser(
      name: _authController.nameController.value.text,
      phoneNo: _authController.mobileNoController.value.text,
      otp: _authController.otpEditingController.value.text,
    );
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
                      Positioned(
                        top: 25.h,
                        right: 20.w,
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
                              size: 14.sp,
                            ),
                          ),
                        ),
                      ),
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
                                  child: SingleChildScrollView(
                                    child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        const SizedBox(height: 10),
                                        Row(
                                          children: [
                                            IconButton(
                                              onPressed: () {
                                                _authController
                                                    .disableNumberField
                                                    .value = false;
                                                _authController.isNumberValid
                                                    .value = false;
                                                _authController
                                                    .mobileNoController
                                                    .value
                                                    .text = '';
                                                Get.offNamed(AppRoutes.LOGIN);
                                              },
                                              icon: Icon(
                                                Icons.arrow_back_ios,
                                                size: 16.h,
                                              ),
                                            ),
                                            MediumTextNotoSans(
                                              text: getLocalizedString(
                                                i18.common
                                                    .SIGNUP_REGISTER_HEADING,
                                              ),
                                              fontWeight: FontWeight.w700,
                                              size: 16.sp,
                                            ),
                                          ],
                                        ),
                                        const SizedBox(height: 30),
                                        Obx(
                                          () => Container(
                                            decoration: BoxDecoration(
                                              borderRadius:
                                                  BorderRadius.circular(15),
                                              color: _authController
                                                      .disableNumberField.value
                                                  ? BaseConfig.borderColor
                                                  : Colors.transparent,
                                            ),
                                            child: Stack(
                                              alignment: Alignment.centerRight,
                                              children: [
                                                TextFormFieldApp(
                                                  readOnly: _authController
                                                      .disableNumberField.value,
                                                  controller: _authController
                                                      .mobileNoController.value,
                                                  padding: _authController
                                                          .mobileNoController
                                                          .value
                                                          .text
                                                          .trim()
                                                          .isNotEmpty
                                                      ? EdgeInsets.fromLTRB(
                                                          0,
                                                          20.h,
                                                          0,
                                                          0,
                                                        )
                                                      : EdgeInsets.fromLTRB(
                                                          0,
                                                          10.h,
                                                          0,
                                                          10.h,
                                                        ),
                                                  keyboardType:
                                                      TextInputType.phone,
                                                  label: Text(
                                                    '${getLocalizedString(i18.common.LOGIN_PHONE_HINT_TEXT)} *',
                                                  ),
                                                  inputFormatters: [
                                                    LengthLimitingTextInputFormatter(
                                                      10,
                                                    ), // Limit to 10 digits
                                                    FilteringTextInputFormatter
                                                        .digitsOnly, // Allow only digits
                                                  ],
                                                  onChange: (value) {
                                                    //_authController.mobileNoController.value.text = value;
                                                    _authController
                                                        .validateNumber(value);
                                                  },
                                                  fillColor: _authController
                                                          .disableNumberField
                                                          .value
                                                      ? BaseConfig.borderColor
                                                      : BaseConfig
                                                          .mainBackgroundColor,
                                                  hintTextColor: _authController
                                                          .disableNumberField
                                                          .value
                                                      ? BaseConfig.greyColor3
                                                      : BaseConfig.textColor,
                                                ),
                                                if (_authController
                                                    .isNumberValid.value)
                                                  const Icon(
                                                    Icons.check,
                                                    color: BaseConfig
                                                        .appThemeColor1,
                                                    size: 25,
                                                  ).paddingOnly(right: 15),
                                              ],
                                            ),
                                          ).marginOnly(bottom: 16.h),
                                        ),
                                        const SizedBox(height: 10),
                                        Container(
                                          decoration: BoxDecoration(
                                            borderRadius: BorderRadius.circular(
                                              15.r,
                                            ),
                                            color: _authController
                                                    .disableNumberField.value
                                                ? BaseConfig.borderColor
                                                : Colors.transparent,
                                          ),
                                          child: TextFormFieldApp(
                                            textInputAction:
                                                TextInputAction.next,
                                            readOnly: _authController
                                                .disableNumberField.value,
                                            controller: _authController
                                                .nameController.value,
                                            padding: _authController
                                                    .userNameController
                                                    .value
                                                    .text
                                                    .trim()
                                                    .isNotEmpty
                                                ? EdgeInsets.fromLTRB(
                                                    0,
                                                    10.h,
                                                    0,
                                                    0,
                                                  )
                                                : EdgeInsets.fromLTRB(
                                                    0,
                                                    5.h,
                                                    0,
                                                    5.h,
                                                  ),
                                            label: Text(
                                              '${getLocalizedString(i18.common.NAME_HINT_TEXT)} *',
                                            ),
                                            hintFontSize: 15.sp,
                                            inputFormatters: <TextInputFormatter>[]
                                                .addAlphanumericSpaceFormatter(),
                                            keyboardType: TextInputType.name,
                                            validator: (value) {
                                              if (value!.trim().isEmpty) {
                                                return '${getLocalizedString(i18.common.NAME_HINT_TEXT)} *';
                                              }
                                              return null;
                                            },
                                            fillColor: _authController
                                                    .disableNumberField.value
                                                ? BaseConfig.borderColor
                                                : BaseConfig
                                                    .mainBackgroundColor,
                                            hintTextColor: _authController
                                                    .disableNumberField.value
                                                ? BaseConfig.greyColor3
                                                : BaseConfig.textColor,
                                          ),
                                        ).marginOnly(bottom: 12.h),
                                        const SizedBox(height: 10),
                                        Container(
                                          decoration: BoxDecoration(
                                            borderRadius: BorderRadius.circular(
                                              15.r,
                                            ),
                                            color: _authController
                                                    .disableNumberField.value
                                                ? BaseConfig.borderColor
                                                : Colors.transparent,
                                          ),
                                          child: TextFormFieldApp(
                                            onTap: () => selectDate(context),
                                            keyboardType: TextInputType.none,
                                            readOnly: _authController
                                                .disableNumberField.value,
                                            padding: _authController
                                                    .userNameController
                                                    .value
                                                    .text
                                                    .trim()
                                                    .isNotEmpty
                                                ? EdgeInsets.fromLTRB(
                                                    0,
                                                    10.h,
                                                    0,
                                                    0,
                                                  )
                                                : EdgeInsets.fromLTRB(
                                                    0,
                                                    5.h,
                                                    0,
                                                    5.h,
                                                  ),
                                            label: const Text(
                                              "dd-mm-yyyy *",
                                            ),
                                            hintFontSize: 15.sp,
                                            controller: _authController
                                                .dobController.value,
                                            textInputAction:
                                                TextInputAction.done,
                                            suffixIcon: const Icon(
                                              Icons.calendar_month,
                                              color: BaseConfig.appThemeColor1,
                                            ),
                                            validator: (value) {
                                              if (value!.trim().isEmpty) {
                                                return 'Enter your date of birth';
                                              }
                                              if (value.isValidDob()) {
                                                return 'Invalid date of birth';
                                              }
                                              return null;
                                            },
                                            fillColor: _authController
                                                    .disableNumberField.value
                                                ? BaseConfig.borderColor
                                                : BaseConfig
                                                    .mainBackgroundColor,
                                            hintTextColor: _authController
                                                    .disableNumberField.value
                                                ? BaseConfig.greyColor3
                                                : BaseConfig.textColor,
                                          ),
                                        ).marginOnly(bottom: 12.h),
                                        Obx(
                                          () => _authController
                                                  .disableNumberField.value
                                              ? Container(
                                                  decoration: BoxDecoration(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                      15,
                                                    ),
                                                    color: BaseConfig
                                                        .mainBackgroundColor,
                                                  ),
                                                  child: Stack(
                                                    alignment:
                                                        Alignment.centerRight,
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
                                                            TextInputType
                                                                .number,
                                                        label: const Text(
                                                          "Enter OTP",
                                                        ),
                                                        inputFormatters: [
                                                          LengthLimitingTextInputFormatter(
                                                            6,
                                                          ), // Limit to 6 digits
                                                          FilteringTextInputFormatter
                                                              .digitsOnly, // Allow only digits
                                                        ],
                                                        onChange: (value) {
                                                          //_authController.otpEditingController.value.text = value;
                                                          _authController
                                                              .validateOtp(
                                                            value,
                                                          );
                                                        },
                                                      ),
                                                      if (_authController
                                                          .isOtpValid.value)
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
                                                ).marginOnly(bottom: 16.h)
                                              : const SizedBox.shrink(),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                                // SizedBox(height: 110.h),

                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
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
                                                    validateSignUpOtp();
                                                  } else {
                                                    validatedForm();
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
                                                  ); // Color when the button is disabled
                                                }
                                                return BaseConfig
                                                    .appThemeColor1; // Color when the button is enabled
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
                                                          i18.common.SUBMIT,
                                                        )
                                                      : getLocalizedString(
                                                          i18.common.NEXT,
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
                                          text:
                                              '${getLocalizedString(i18.common.HAVE_AN_ACCOUNT)} ',
                                          fontWeight: FontWeight.w400,
                                          size: 12.sp,
                                          color: BaseConfig.greyColor3,
                                        ),
                                        MediumTextNotoSans(
                                          text: getLocalizedString(
                                            i18.common.LOGIN,
                                          ),
                                          fontWeight: FontWeight.w600,
                                          size: 12.sp,
                                          color: BaseConfig.appThemeColor1,
                                        ).ripple(() {
                                          _authController
                                              .disableNumberField.value = false;
                                          _authController.isNumberValid.value =
                                              false;
                                          _authController.otpEditingController
                                              .value.text = '';
                                          Get.toNamed(AppRoutes.LOGIN);
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
                                    const SizedBox(height: 10),
                                    Row(
                                      children: [
                                        IconButton(
                                          onPressed: () {
                                            _authController.disableNumberField
                                                .value = false;
                                            _authController.mobileNoController
                                                .value.text = '';
                                            Get.offNamed(AppRoutes.LOGIN);
                                          },
                                          icon: Icon(
                                            Icons.arrow_back_ios,
                                            size: 16.h,
                                          ),
                                        ),
                                        MediumTextNotoSans(
                                          text: getLocalizedString(
                                            i18.common.SIGNUP_REGISTER_HEADING,
                                          ),
                                          fontWeight: FontWeight.w700,
                                          size: 7.sp,
                                        ),
                                      ],
                                    ),
                                    const SizedBox(height: 30),
                                    Obx(
                                      () => Container(
                                        decoration: BoxDecoration(
                                          borderRadius:
                                              BorderRadius.circular(15),
                                          color: _authController
                                                  .disableNumberField.value
                                              ? BaseConfig.borderColor
                                              : Colors.transparent,
                                        ),
                                        child: Stack(
                                          alignment: Alignment.centerRight,
                                          children: [
                                            TextFormFieldApp(
                                              readOnly: _authController
                                                  .disableNumberField.value,
                                              controller: _authController
                                                  .mobileNoController.value,
                                              padding: _authController
                                                      .mobileNoController
                                                      .value
                                                      .text
                                                      .trim()
                                                      .isNotEmpty
                                                  ? EdgeInsets.fromLTRB(
                                                      0,
                                                      20.h,
                                                      0,
                                                      0,
                                                    )
                                                  : EdgeInsets.fromLTRB(
                                                      0,
                                                      10.h,
                                                      0,
                                                      10.h,
                                                    ),
                                              keyboardType: TextInputType.phone,
                                              hintFontSize: 7.sp,
                                              label: Text(
                                                '${getLocalizedString(i18.common.LOGIN_PHONE_HINT_TEXT)} *',
                                              ),
                                              inputFormatters: [
                                                LengthLimitingTextInputFormatter(
                                                  10,
                                                ), // Limit to 10 digits
                                                FilteringTextInputFormatter
                                                    .digitsOnly, // Allow only digits
                                              ],
                                              onChange: (value) {
                                                // _authController.mobileNoController.value.text = value;
                                                _authController
                                                    .validateNumber(value);
                                              },
                                              fillColor: _authController
                                                      .disableNumberField.value
                                                  ? BaseConfig.borderColor
                                                  : BaseConfig
                                                      .mainBackgroundColor,
                                              hintTextColor: _authController
                                                      .disableNumberField.value
                                                  ? BaseConfig.greyColor3
                                                  : BaseConfig.textColor,
                                            ),
                                            if (_authController
                                                .isNumberValid.value)
                                              const Icon(
                                                Icons.check,
                                                color:
                                                    BaseConfig.appThemeColor1,
                                                size: 25,
                                              ).paddingOnly(right: 15),
                                          ],
                                        ),
                                      ).marginOnly(bottom: 16.h),
                                    ),
                                    const SizedBox(height: 10),
                                    Container(
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(
                                          15.r,
                                        ),
                                        color: _authController
                                                .disableNumberField.value
                                            ? BaseConfig.borderColor
                                            : Colors.transparent,
                                      ),
                                      child: TextFormFieldApp(
                                        textInputAction: TextInputAction.next,
                                        readOnly: _authController
                                            .disableNumberField.value,
                                        controller: _authController
                                            .nameController.value,
                                        padding: _authController
                                                .userNameController.value.text
                                                .trim()
                                                .isNotEmpty
                                            ? EdgeInsets.fromLTRB(
                                                0,
                                                10.h,
                                                0,
                                                0,
                                              )
                                            : EdgeInsets.fromLTRB(
                                                0,
                                                5.h,
                                                0,
                                                5.h,
                                              ),
                                        label: Text(
                                          '${getLocalizedString(i18.common.NAME_HINT_TEXT)} *',
                                        ),
                                        hintFontSize: 7.sp,
                                        keyboardType: TextInputType.name,
                                        inputFormatters: <TextInputFormatter>[]
                                            .addAlphanumericSpaceFormatter(),
                                        validator: (value) {
                                          if (value!.trim().isEmpty) {
                                            return '${getLocalizedString(i18.common.NAME_HINT_TEXT)} *';
                                          }
                                          return null;
                                        },
                                        fillColor: _authController
                                                .disableNumberField.value
                                            ? BaseConfig.borderColor
                                            : BaseConfig.mainBackgroundColor,
                                        hintTextColor: _authController
                                                .disableNumberField.value
                                            ? BaseConfig.greyColor3
                                            : BaseConfig.textColor,
                                      ),
                                    ).marginOnly(bottom: 12.h),
                                    const SizedBox(height: 10),
                                    Container(
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(
                                          15.r,
                                        ),
                                        color: _authController
                                                .disableNumberField.value
                                            ? BaseConfig.borderColor
                                            : Colors.transparent,
                                      ),
                                      child: TextFormFieldApp(
                                        onTap: () => selectDate(context),
                                        keyboardType: TextInputType.none,
                                        readOnly: _authController
                                            .disableNumberField.value,
                                        padding: _authController
                                                .userNameController.value.text
                                                .trim()
                                                .isNotEmpty
                                            ? EdgeInsets.fromLTRB(
                                                0,
                                                10.h,
                                                0,
                                                0,
                                              )
                                            : EdgeInsets.fromLTRB(
                                                0,
                                                5.h,
                                                0,
                                                5.h,
                                              ),
                                        label: const Text(
                                          "dd-mm-yyyy *",
                                        ),
                                        hintFontSize: 7.sp,
                                        controller:
                                            _authController.dobController.value,
                                        textInputAction: TextInputAction.done,
                                        suffixIcon: const Icon(
                                          Icons.calendar_month,
                                          color: BaseConfig.appThemeColor1,
                                        ),
                                        validator: (value) {
                                          if (value!.trim().isEmpty) {
                                            return 'Enter your date of birth *';
                                          }
                                          if (value.isValidDob()) {
                                            return 'Invalid date of birth';
                                          }
                                          return null;
                                        },
                                        fillColor: _authController
                                                .disableNumberField.value
                                            ? BaseConfig.borderColor
                                            : BaseConfig.mainBackgroundColor,
                                        hintTextColor: _authController
                                                .disableNumberField.value
                                            ? BaseConfig.greyColor3
                                            : BaseConfig.textColor,
                                      ),
                                    ).marginOnly(bottom: 12.h),
                                    Obx(
                                      () => _authController
                                              .disableNumberField.value
                                          ? Container(
                                              decoration: BoxDecoration(
                                                borderRadius:
                                                    BorderRadius.circular(15),
                                                color: BaseConfig
                                                    .mainBackgroundColor,
                                              ),
                                              child: Stack(
                                                alignment:
                                                    Alignment.centerRight,
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
                                                        ? EdgeInsets.fromLTRB(
                                                            0,
                                                            20.h,
                                                            0,
                                                            0,
                                                          )
                                                        : EdgeInsets.fromLTRB(
                                                            0,
                                                            10.h,
                                                            0,
                                                            10.h,
                                                          ),
                                                    keyboardType:
                                                        TextInputType.number,
                                                    hintFontSize: 7.sp,
                                                    label:
                                                        const Text("Enter OTP"),
                                                    inputFormatters: [
                                                      LengthLimitingTextInputFormatter(
                                                        6,
                                                      ), // Limit to 6 digits
                                                      FilteringTextInputFormatter
                                                          .digitsOnly, // Allow only digits
                                                    ],
                                                    onChange: (value) {
                                                      //_authController.otpEditingController.value.text = value;
                                                      _authController
                                                          .validateOtp(value);
                                                    },
                                                  ),
                                                  if (_authController
                                                      .isOtpValid.value)
                                                    const Icon(
                                                      Icons.check,
                                                      color: BaseConfig
                                                          .appThemeColor1,
                                                      size: 25,
                                                    ).paddingOnly(right: 15),
                                                ],
                                              ),
                                            ).marginOnly(bottom: 16.h)
                                          : const SizedBox.shrink(),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                SizedBox(
                                  height: 20.h,
                                ),
                                SizedBox(
                                  width: Get.width,
                                  height: 44.h,
                                  child: Obx(
                                    () => ElevatedButton(
                                      onPressed:
                                          _authController.isButtonEnabled.value
                                              ? () {
                                                  if (_authController
                                                      .disableNumberField
                                                      .value) {
                                                    validateSignUpOtp();
                                                  } else {
                                                    validatedForm();
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
                                              ); // Color when the button is disabled
                                            }
                                            return BaseConfig
                                                .appThemeColor1; // Color when the button is enabled
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
                                                      i18.common.SUBMIT,
                                                    )
                                                  : getLocalizedString(
                                                      i18.common.NEXT,
                                                    ),
                                              color: Colors.white,
                                              size: 8.sp,
                                              fontWeight: FontWeight.w700,
                                            ),
                                    ),
                                  ),
                                ),
                                SizedBox(
                                  height: 20.h,
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    MediumTextNotoSans(
                                      text:
                                          '${getLocalizedString(i18.common.HAVE_AN_ACCOUNT)} ',
                                      fontWeight: FontWeight.w400,
                                      size: 7.sp,
                                      color: BaseConfig.greyColor3,
                                    ),
                                    MediumTextNotoSans(
                                      text:
                                          getLocalizedString(i18.common.LOGIN),
                                      fontWeight: FontWeight.w600,
                                      size: 7.sp,
                                      color: BaseConfig.appThemeColor1,
                                    ).ripple(() {
                                      _authController.disableNumberField.value =
                                          false;
                                      _authController
                                          .otpEditingController.value.text = '';
                                      Get.toNamed(AppRoutes.LOGIN);
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
}
