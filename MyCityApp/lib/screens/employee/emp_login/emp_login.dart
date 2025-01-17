import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/text_form_field.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/location_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/slider_image_widget.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpLoginScreen2 extends StatefulWidget {
  const EmpLoginScreen2({super.key});

  @override
  State<EmpLoginScreen2> createState() => _EmpLoginScreen2State();
}

class _EmpLoginScreen2State extends State<EmpLoginScreen2> {
  final _authController = Get.find<AuthController>();
  final _languageController = Get.find<LanguageController>();
  final _cityController = Get.find<CityController>();

  var isHidePassword = true.obs;

  final _formKey = GlobalKey<FormState>();

  Future<void> validateLoginForm() async {
    // if (!_formKey.currentState!.validate()) return;

    _authController.isLoading.value = true;

    dPrint('Login Success');

    FocusScope.of(context).unfocus();

    await _authController.otpValidate(
      userType: UserType.EMPLOYEE,
    );

    _authController.isLoading.value = false;
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
                                              // Get.toNamed(
                                              //   AppRoutes.SELECT_CITIZEN,
                                              // );
                                              Get.back();
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
                                      SizedBox(height: 40.h),
                                      _buildLoginEmp(),
                                    ],
                                  ),
                                ),

                                // SizedBox(height: 110.h),

                                SizedBox(
                                  width: Get.width,
                                  height: 44.h,
                                  child: Obx(
                                    () => ElevatedButton(
                                      onPressed: (_authController
                                                  .userNameController
                                                  .value
                                                  .text
                                                  .isNotEmpty &&
                                              _authController.passwordController
                                                  .value.text.isNotEmpty &&
                                              _cityController.empSelectedCity
                                                  .value.isNotEmpty)
                                          ? () => validateLoginForm()
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
                                              text: getLocalizedString(
                                                i18.common.CONTINUE,
                                              ),
                                              color: BaseConfig
                                                  .mainBackgroundColor,
                                              size: 16.sp,
                                              fontWeight: FontWeight.w700,
                                            ),
                                    ),
                                  ),
                                ),
                                SizedBox(
                                  height: 20.h,
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
                        ],
                      ).marginOnly(
                        right: 4.w,
                      ),
                    ),
                    Expanded(
                      child: SingleChildScrollView(
                        physics: const NeverScrollableScrollPhysics(),
                        child: Column(
                          children: [
                            Container(
                              padding:
                                  const EdgeInsets.symmetric(horizontal: 10),
                              height: 0.85.sh,
                              child: SingleChildScrollView(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        IconButton(
                                          onPressed: () {
                                            Get.toNamed(
                                              AppRoutes.SELECT_CATEGORY,
                                              // AppRoutes.SELECT_CITIZEN,
                                            );
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
                                      height: 0.6.sh,
                                      child: _buildLoginEmpLand(),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Container(
                                  width: Get.width,
                                  height: 44.h,
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 10,
                                  ),
                                  child: Obx(
                                    () => ElevatedButton(
                                      onPressed: (_authController
                                                  .userNameController
                                                  .value
                                                  .text
                                                  .isNotEmpty &&
                                              _authController.passwordController
                                                  .value.text.isNotEmpty &&
                                              _cityController.empSelectedCity
                                                  .value.isNotEmpty)
                                          ? () => validateLoginForm()
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
                                              text: getLocalizedString(
                                                i18.common.CONTINUE,
                                              ),
                                              color: Colors.white,
                                              size: 8.sp,
                                              fontWeight: FontWeight.w700,
                                            ),
                                    ),
                                  ),
                                ),
                                // SizedBox(
                                //   height: 10.h,
                                // ),
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

  Widget _buildLoginEmp() {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Obx(
            () => Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(
                  15,
                ),
                color: _authController.disableNumberField.value
                    ? BaseConfig.borderColor
                    : Colors.transparent,
              ),
              child: TextFormFieldApp(
                readOnly: _authController.disableNumberField.value,
                controller: _authController.userNameController.value,
                padding: _authController.userNameController.value.text
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
                keyboardType: TextInputType.text,
                maxLine: 1,
                maxLength: 20,
                label: Text(
                  '${getLocalizedString(i18.common.EMPLOYEE_USERNAME)} *',
                ),
                fillColor: _authController.disableNumberField.value
                    ? BaseConfig.borderColor
                    : BaseConfig.mainBackgroundColor,
                hintTextColor: _authController.disableNumberField.value
                    ? BaseConfig.greyColor3
                    : BaseConfig.textColor,
                textInputAction: TextInputAction.next,
              ),
            ).marginOnly(bottom: 16.h),
          ),
          Obx(
            () => TextFormFieldApp(
              controller: _authController.passwordController.value,
              padding: _authController.passwordController.value.text.isNotEmpty
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
              keyboardType: TextInputType.visiblePassword,
              textInputAction: TextInputAction.done,
              label: Text(
                '${getLocalizedString(i18.common.EMPLOYEE_PASSWORD)} *',
              ),
              maxLine: 1,
              obscureText: isHidePassword.value,
              suffixIcon: IconButton(
                onPressed: () {
                  isHidePassword.value = !isHidePassword.value;
                },
                icon: Icon(
                  isHidePassword.value
                      ? Icons.visibility_off
                      : Icons.visibility,
                  color: BaseConfig.textColor,
                ),
              ),
            ),
          ),
          SizedBox(height: 15.h),
          DropdownSearch<String>(
            items: (filter, infiniteScrollProps) =>
                _languageController.mdmsResTenant.tenants!.map((tenant) {
              return '${tenant.code} | ${tenant.name} | ${tenant.city?.name}';
            }).toList(),
            selectedItem: _cityController.empSelectedCity.value.isNotEmpty
                ? _cityController.empSelectedCity.value
                : "Select City",
            decoratorProps: DropDownDecoratorProps(
              decoration: InputDecoration(
                focusedBorder: const OutlineInputBorder(),
                hintText: '${getLocalizedString(i18.common.EMPLOYEE_CITY)} *',
                contentPadding: EdgeInsetsDirectional.symmetric(
                  horizontal: 10.w,
                  vertical: 12.h,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(15.r),
                  borderSide: const BorderSide(
                    color: Colors.grey,
                    width: 1,
                  ),
                ),
              ),
            ),
            onChanged: (value) async {
              var code = value?.split(' | ').first ?? '';
              //_cityController.empSelectedCity.value = code;
              dPrint('City: $code');

              var tenant =
                  _languageController.mdmsResTenant.tenants!.firstWhere(
                (tenant) => tenant.code == code,
                orElse: () =>
                    TenantTenant(code: "", city: City(districtCode: "")),
              );

              await _cityController.setEmpSelectedCity(tenant);
            },
            dropdownBuilder: (context, selectedItem) {
              var code = selectedItem?.split(' | ').first ?? '';
              var item = _languageController.mdmsResTenant.tenants!.firstWhere(
                (tenant) => tenant.code == code,
                orElse: () =>
                    TenantTenant(code: "", city: City(districtCode: "")),
              );

              return Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  //CityIcon(districtCode: item.city!.districtCode!),
                  SizedBox(width: 10.w),
                  item.code!.isEmpty
                      ? SmallTextNotoSans(
                          text:
                              '${getLocalizedString(i18.common.EMPLOYEE_CITY)} *',
                        )
                      : SmallTextNotoSans(
                          text: !item.code!.contains('.')
                              ? getLocalizedString(
                                  '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}'
                                      .toUpperCase(),
                                )
                              : getLocalizedString(
                                  '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${item.code!.split('.').last}'
                                      .toUpperCase(),
                                ),
                          fontWeight: FontWeight.w400,
                        ),
                ],
              );
            },
            popupProps: PopupProps.menu(
              fit: FlexFit.tight,
              showSearchBox: true,
              containerBuilder: (context, popupWidget) {
                return Container(
                  color: BaseConfig.mainBackgroundColor,
                  child: popupWidget,
                );
              },
              constraints: const BoxConstraints(
                maxHeight: 200,
              ),
              searchFieldProps: TextFieldProps(
                decoration: InputDecoration(
                  hintText: getLocalizedString(i18.common.SEARCH),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12.0),
                    borderSide: const BorderSide(
                      color: BaseConfig.borderColor,
                      width: 2.0,
                    ),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12.0),
                    borderSide: const BorderSide(
                      color: BaseConfig.borderColor,
                      width: 2.0,
                    ),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12.0),
                    borderSide: const BorderSide(
                      color: BaseConfig.borderColor,
                      width: 2.0,
                    ),
                  ),
                ),
              ),
              itemBuilder: (context, item, isDisabled, isSelected) {
                var code = item.split(' | ').first;
                var tenant =
                    _languageController.mdmsResTenant.tenants!.firstWhere(
                  (tenant) => tenant.code == code,
                  orElse: () =>
                      TenantTenant(code: "", city: City(districtCode: "")),
                );

                return ListTile(
                  //leading: CityIcon(districtCode: tenant.city!.districtCode!),
                  title: SmallTextNotoSans(
                    text: !tenant.code!.contains('.')
                        ? getLocalizedString(
                            '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}'
                                .toUpperCase(),
                          )
                        : getLocalizedString(
                            '${i18.common.LOCATION_PREFIX}${BaseConfig.STATE_TENANT_ID}_${tenant.code!.split('.').last}'
                                .toUpperCase(),
                          ),
                    fontWeight: FontWeight.w400,
                  ),
                );
              },
            ),
            filterFn: (item, filter) {
              // Implement the custom filter logic
              var tenant =
                  _languageController.mdmsResTenant.tenants!.firstWhere(
                (tenant) => tenant.code == item.split(' | ').first,
                orElse: () =>
                    TenantTenant(code: "", city: City(districtCode: "")),
              );

              return tenant.name!
                      .toLowerCase()
                      .contains(filter.toLowerCase()) ||
                  tenant.city!.name!
                      .toLowerCase()
                      .contains(filter.toLowerCase());
            },
          ),
          SizedBox(height: 10.h),
        ],
      ),
    );
  }

  Widget _buildLoginEmpLand() {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Obx(
            () => Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(
                  15.r,
                ),
                color: _authController.disableNumberField.value
                    ? BaseConfig.borderColor
                    : Colors.transparent,
              ),
              child: TextFormFieldApp(
                readOnly: _authController.disableNumberField.value,
                controller: _authController.userNameController.value,
                padding: _authController.userNameController.value.text
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
                keyboardType: TextInputType.text,
                maxLine: 1,
                maxLength: 20,
                label: Text(
                  '${getLocalizedString(i18.common.EMPLOYEE_USERNAME)} *',
                ),
                hintFontSize: 7.sp,
                fillColor: _authController.disableNumberField.value
                    ? BaseConfig.borderColor
                    : BaseConfig.mainBackgroundColor,
                hintTextColor: _authController.disableNumberField.value
                    ? BaseConfig.greyColor3
                    : BaseConfig.textColor,
                textInputAction: TextInputAction.next,
              ),
            ).marginOnly(bottom: 12.h),
          ),
          TextFormFieldApp(
            controller: _authController.passwordController.value,
            padding: _authController.passwordController.value.text.isNotEmpty
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
            keyboardType: TextInputType.visiblePassword,
            label: Text(
              '${getLocalizedString(i18.common.EMPLOYEE_PASSWORD)} *',
            ),
            hintFontSize: 7.sp,
            maxLine: 1,
            obscureText: isHidePassword.value,
            suffixIcon: IconButton(
              onPressed: () {
                isHidePassword.value = !isHidePassword.value;
              },
              icon: Icon(
                isHidePassword.value ? Icons.visibility_off : Icons.visibility,
                color: BaseConfig.textColor,
              ),
            ),
          ),
          SizedBox(height: 10.h),
          DropdownSearch<String>(
            items: (filter, infiniteScrollProps) =>
                _languageController.mdmsResTenant.tenants!.map((item) {
              return item.code ?? "";
            }).toList(),
            selectedItem: _cityController.empSelectedCity.value.isNotEmpty
                ? _cityController.empSelectedCity.value
                : null,
            decoratorProps: DropDownDecoratorProps(
              decoration: InputDecoration(
                hintText: '${getLocalizedString(i18.common.EMPLOYEE_CITY)} *',
                contentPadding: EdgeInsetsDirectional.symmetric(
                  horizontal: 10.w,
                  vertical: 12.h,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(15.r),
                  borderSide: const BorderSide(
                    color: Colors.grey,
                    width: 1,
                  ),
                ),
              ),
            ),
            onChanged: (value) async {
              //_cityController.empSelectedCity.value = value!;
              dPrint('City: $value');

              // Find the tenant object
              var tenant =
                  _languageController.mdmsResTenant.tenants!.firstWhere(
                (tenant) => tenant.code == value,
                orElse: () =>
                    TenantTenant(code: "", city: City(districtCode: "")),
              );

              await _cityController.setEmpSelectedCity(tenant);
            },
            dropdownBuilder: (context, selectedItem) {
              var item = _languageController.mdmsResTenant.tenants!.firstWhere(
                (tenant) => tenant.code == selectedItem,
                orElse: () =>
                    TenantTenant(code: "", city: City(districtCode: "")),
              );

              return Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  //CityIcon(districtCode: item.city!.districtCode!),
                  SizedBox(width: 10.w),
                  SmallTextNotoSans(
                    text: item.name.toString(),
                    fontWeight: FontWeight.w400,
                    size: 7.sp,
                  ),
                ],
              );
            },
            popupProps: PopupProps.menu(
              showSearchBox: true,
              itemBuilder: (context, item, isDisabled, isSelected) {
                var tenant =
                    _languageController.mdmsResTenant.tenants!.firstWhere(
                  (tenant) => tenant.code == item,
                  orElse: () => TenantTenant(
                    code: "",
                    city: City(districtCode: ""),
                  ),
                );
                return ListTile(
                  //leading: CityIcon(districtCode: tenant.city!.districtCode!),
                  title: SmallTextNotoSans(
                    text: tenant.name.toString(),
                    fontWeight: FontWeight.w400,
                    size: 7.sp,
                  ),
                );
              },
            ),
          ),
          SizedBox(height: 20.h),
        ],
      ),
    );
  }
}
