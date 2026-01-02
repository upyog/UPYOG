import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/drawer/drawer_item.dart';
import 'package:mobile_app/components/gradient_btn.dart';
import 'package:mobile_app/components/image_placeholder.dart';
import 'package:mobile_app/components/language_card.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/services/secure_storage_service.dart';
import 'package:mobile_app/utils/constants/constants.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/big_text.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class EmpDrawerWidget extends StatelessWidget {
  EmpDrawerWidget({super.key});

  final _authController = Get.find<AuthController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _languageController = Get.find<LanguageController>();

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Drawer(
          backgroundColor: Colors.white,
          width: o == Orientation.portrait
              ? MediaQuery.of(context).size.width / 1.2
              : MediaQuery.of(context).size.width / 1.6,
          child: SafeArea(
            child: SingleChildScrollView(
              physics: const BouncingScrollPhysics(),
              child: _buildItems(context, o),
            ),
          ),
        );
      },
    );
  }

  Widget _buildItems(BuildContext context, Orientation o) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (_authController.isValidUser) ...[
          _buildHeaderProfile(context, o),
          const Divider(),
        ],
        drawerItem(
          label: getLocalizedString(i18.common.HOME),
          icon: const Icon(
            Icons.home,
            size: 25,
            color: BaseConfig.appThemeColor1,
          ),
          textSize: 12.sp,
        ).ripple(() {
          Navigator.of(context).pop();
        }),
        if (isNotNullOrEmpty(_languageController.stateInfo?.languages))
          ListTile(
            title: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                MediumTextNotoSans(
                  text: 'Languages',
                  color: BaseConfig.textColor,
                  size: 12.sp,
                ),
                const SizedBox(
                  height: 10,
                ),
                Wrap(
                  runSpacing: 10,
                  children: [
                    for (int i = 0;
                        i < _languageController.stateInfo!.languages!.length;
                        i++)
                      GetBuilder<LanguageController>(
                        builder: (langCtrl) {
                          return LanguageCard(
                            language:
                                _languageController.stateInfo!.languages![i],
                            widthRect: o == Orientation.portrait ? 70 : 150,
                            cPadding: 6,
                            margin: const EdgeInsets.only(right: 8),
                            onTap: () async {
                              await storage.setInt(
                                Constants.LANG_SELECTION_INDEX,
                                i,
                              );

                              await storage.setString(
                                Constants.TENANT_ID,
                                _languageController.stateInfo!.code!,
                              );

                              langCtrl.onSelectionOfLanguage(
                                _languageController.stateInfo!.languages![i],
                                _languageController.stateInfo!.languages!,
                                i,
                              );
                            },
                            textSize: 12.sp,
                          );
                        },
                      ),
                  ],
                ),
              ],
            ),
            leading: const Icon(
              Icons.translate,
              size: 25,
              color: BaseConfig.appThemeColor1,
            ),
          ),
        drawerItem(
          label: getLocalizedString(i18.common.EDIT_PROFILE),
          icon: const Icon(
            Icons.edit,
            size: 25,
            color: BaseConfig.appThemeColor1,
          ),
          textSize: 12.sp,
        ).ripple(() {
          Get.toNamed(
            AppRoutes.EMP_PROFILE,
            arguments: {
              'showBackBtn': true,
            },
          );
        }),
        drawerItem(
          label: _authController.isValidUser
              ? getLocalizedString(i18.common.LOGOUT)
              : getLocalizedString(i18.common.LOGIN),
          icon: const Icon(
            Icons.login,
            size: 25,
            color: BaseConfig.appThemeColor1,
          ),
          textSize: 12.sp,
        ).ripple(() async {
          if (_authController.isValidUser) {
            return showLogoutDialog();
          } else {
            return Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
            // return Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
          }
        }),
        drawerItem(
          label: getLocalizedString(i18.common.HELPLINE),
          icon: const Icon(
            Icons.phone,
            size: 25,
            color: BaseConfig.appThemeColor1,
          ),
          textSize: 12.sp,
          padding: const EdgeInsets.only(left: 10, top: 10),
        ),
        MediumTextNotoSans(
          text: BaseConfig.helpLineNo,
          color: BaseConfig.redColor2,
          size: 12.sp,
        ).paddingSymmetric(horizontal: 6, vertical: 4).ripple(
          () async {
            await launchPhoneDialer(BaseConfig.helpLineNo);
          },
          customBorder:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(8.r)),
        ).paddingOnly(left: 49),
        const SizedBox(height: 100),
      ],
    );
  }

  Widget _buildHeaderProfile(BuildContext context, Orientation o) {
    return SizedBox(
      width: MediaQuery.sizeOf(context).width,
      height: o == Orientation.portrait ? 200 : 300,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          FutureBuilder(
            future: _editProfileController.getCacheProfileFIleStore(),
            builder: (context, snapshot) {
              // if (snapshot.connectionState == ConnectionState.waiting) {
              //   return showCircularIndicator();
              // }
              final imageData = snapshot.data;
              return ImagePlaceHolder(
                photoUrl: imageData?.getUserPhoto(),
              );
            },
          ),
          if (_editProfileController.userProfile.user?.first.name != null)
            Tooltip(
              message: _editProfileController.userProfile.user!.first.name!,
              child: SizedBox(
                width: Get.width / 2,
                child: BigTextNotoSans(
                  text: _editProfileController.userProfile.user!.first.name!,
                  fontWeight: FontWeight.w600,
                  maxLine: 1,
                  textOverflow: TextOverflow.ellipsis,
                  textAlign: TextAlign.center,
                  size: 12.sp,
                ),
              ),
            ).marginOnly(top: 10),
          if (_editProfileController.userProfile.user?.first.mobileNumber !=
              null)
            MediumTextNotoSans(
              text:
                  _editProfileController.userProfile.user!.first.mobileNumber!,
              color: BaseConfig.redColor2,
              size: 12.sp,
            ).marginOnly(top: 5),
        ],
      ),
    );
  }

  void showLogoutDialog() {
    Get.dialog(
      AlertDialog(
        insetPadding: EdgeInsets.symmetric(horizontal: 16.w),
        contentPadding: EdgeInsets.symmetric(vertical: 20.h, horizontal: 16.w),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.r),
        ),
        backgroundColor: BaseConfig.mainBackgroundColor,
        content: SizedBox(
          // height: 258.h,
          width: Get.width,
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    MediumTextNotoSans(
                      text: getLocalizedString(i18.common.LOGOUT_HEADER),
                      fontWeight: FontWeight.w700,
                      size: 16.h,
                    ),
                  ],
                ),
                SizedBox(
                  height: 20.h,
                ),
                MediumTextNotoSans(
                  text: getLocalizedString(
                    i18.common.LOGOUT_CONFIRMATION_MESSAGE,
                  ),
                  fontWeight: FontWeight.w400,
                  size: 14.h,
                ),
                SizedBox(
                  height: 14.h,
                ),
                SmallTextNotoSans(
                  text: getLocalizedString(i18.common.CORE_LOGOUT_MESSAGE),
                  fontWeight: FontWeight.w700,
                  size: 16.h,
                ),
                SizedBox(
                  height: 16.h,
                ),
                const Divider(
                  color: BaseConfig.borderColor,
                ),
                SizedBox(
                  height: 16.h,
                ),
                Row(
                  children: [
                    Expanded(
                      child: SizedBox(
                        height: 44.h,
                        child: gradientBtn(
                          text: getLocalizedString(i18.common.LOGOUT_CANCEL),
                          buttonColor: BaseConfig.mainBackgroundColor,
                          horizonPadding: 5,
                          fontSize: 12.sp,
                          textColor: BaseConfig.appThemeColor1,
                          onPressed: () {
                            Get.back();
                          },
                        ),
                      ),
                    ),
                    Expanded(
                      child: SizedBox(
                        height: 44.h,
                        child: gradientBtn(
                          text: getLocalizedString(i18.common.LOGOUT_YES),
                          horizonPadding: 5.w,
                          fontSize: 12.sp,
                          onPressed: () async {
                            clearData();
                            _authController.nameController.value.text = '';
                            _authController.passwordController.value.text = '';
                            Get.offAllNamed(AppRoutes.SELECT_CITIZEN);
                            // Get.offAllNamed(AppRoutes.SELECT_CATEGORY);
                          },
                        ),
                      ),
                    ),
                  ],
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
