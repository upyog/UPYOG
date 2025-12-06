import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/properties_tax_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/benefit_point.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:url_launcher/url_launcher.dart';

class MyPropertiesScreen extends StatefulWidget {
  const MyPropertiesScreen({super.key});

  @override
  State<MyPropertiesScreen> createState() => _MyPropertiesScreenState();
}

class _MyPropertiesScreenState extends State<MyPropertiesScreen> {
  final _authController = Get.find<AuthController>();
  final _propertiesTaxController = Get.find<PropertiesTaxController>();
  final _commonController = Get.find<CommonController>();
  final _languageController = Get.find<LanguageController>();

  var issuedValue = 0.obs, renewedValue = 0.obs;

  @override
  void initState() {
    super.initState();
    _propertiesTaxController.setDefaultLimit();
    _init();
  }

  Future<void> _init() async {
    final tenant = await getCityTenant();

    await _commonController.fetchLabels(modules: Modules.PT);

    issuedValue.value = await _propertiesTaxController.getPTApplicationCount(
      tenantId: tenant.code,
      token: _authController.token!.accessToken!,
    );

    renewedValue.value = await _propertiesTaxController.getPTApplicationCount(
      tenantId: tenant.code,
      token: _authController.token!.accessToken!,
      isMyApplication: true,
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Scaffold(
          appBar: HeaderTop(
            orientation: o,
            onPressed: () {
              Navigator.of(context).pop();
            },
            title: getLocalizedString(i18.common.PROPERTY_TAX),
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: SingleChildScrollView(
              child: Column(
                children: [
                  SizedBox(
                    height: o == Orientation.portrait
                        ? Get.height * 0.3
                        : Get.height * 0.9,
                    child: Stack(
                      children: [
                        Container(
                          height: o == Orientation.portrait ? 150.h : 180.h,
                          width: Get.width,
                          padding: EdgeInsetsDirectional.only(
                            top: 20.h,
                            start: 16.w,
                            end: 16.w,
                            bottom: 8.w,
                          ),
                          decoration: BoxDecoration(
                            color: BaseConfig.appThemeColor1
                                .withValues(alpha: 0.1),
                          ),
                          child: const Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [],
                          ),
                        ),
                        Positioned(
                          left: 0,
                          right: 0,
                          top: o == Orientation.portrait
                              ? Get.height * 0.09
                              : 90,
                          child: Card(
                            elevation: 0,
                            margin: EdgeInsets.symmetric(horizontal: 16.w),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12.r),
                              side: BorderSide(
                                color: BaseConfig.borderColor,
                                width: 1.w,
                              ),
                            ),
                            child: SizedBox(
                              height: Get.height * 0.15,
                              child: GridView.count(
                                physics: const NeverScrollableScrollPhysics(),
                                primary: false,
                                crossAxisSpacing: 5,
                                crossAxisCount: 4,
                                childAspectRatio: 2 / 5,
                                padding: const EdgeInsets.only(top: 20),
                                children: <Widget>[
                                  Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      IconButton.filled(
                                        style: IconButton.styleFrom(
                                          backgroundColor:
                                              BaseConfig.appThemeColor1,
                                          shape: RoundedRectangleBorder(
                                            borderRadius:
                                                BorderRadius.circular(12.r),
                                          ),
                                          padding: EdgeInsets.all(12.w),
                                        ),
                                        onPressed: () {
                                          Get.toNamed(
                                            AppRoutes.PROPERTY_APPLICATIONS,
                                          );
                                        },
                                        icon: const Icon(
                                          BaseConfig.ptMyApplicationsIcon,
                                        ),
                                      ),
                                      SizedBox(height: 4.h),
                                      SmallTextNotoSans(
                                        text: "My\nApplications",
                                        size: 10.sp,
                                        maxLine: 2,
                                        fontWeight: FontWeight.w600,
                                        textAlign: TextAlign.center,
                                        textOverflow: TextOverflow.ellipsis,
                                      ),
                                    ],
                                  ),
                                  Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      IconButton.filled(
                                        style: IconButton.styleFrom(
                                          backgroundColor:
                                              BaseConfig.appThemeColor1,
                                          shape: RoundedRectangleBorder(
                                            borderRadius:
                                                BorderRadius.circular(12.r),
                                          ),
                                          padding: EdgeInsets.all(12.w),
                                        ),
                                        onPressed: () {
                                          Get.toNamed(AppRoutes.PROPERTY_TAX);
                                        },
                                        icon: const Icon(
                                          BaseConfig.ptMyPropertiesIcon,
                                        ),
                                      ),
                                      SizedBox(height: 4.h),
                                      SmallTextNotoSans(
                                        text: "My\nProperties",
                                        size: 10.sp,
                                        maxLine: 2,
                                        fontWeight: FontWeight.w600,
                                        textAlign: TextAlign.center,
                                        textOverflow: TextOverflow.ellipsis,
                                      ),
                                    ],
                                  ),
                                  Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      IconButton.filled(
                                        style: IconButton.styleFrom(
                                          backgroundColor:
                                              BaseConfig.appThemeColor1,
                                          shape: RoundedRectangleBorder(
                                            borderRadius:
                                                BorderRadius.circular(12.r),
                                          ),
                                          padding: EdgeInsets.all(12.w),
                                        ),
                                        onPressed: () {
                                          Get.toNamed(
                                            AppRoutes.PROPERTY_MY_BILLS_SCREEN,
                                          );
                                        },
                                        icon:
                                            const Icon(BaseConfig.wMyBillsIcon),
                                      ),
                                      SizedBox(height: 4.h),
                                      SmallTextNotoSans(
                                        text: "My\nBills",
                                        size: 10.sp,
                                        maxLine: 2,
                                        fontWeight: FontWeight.w600,
                                        textAlign: TextAlign.center,
                                        textOverflow: TextOverflow.ellipsis,
                                      ),
                                    ],
                                  ),
                                  Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      IconButton.filled(
                                        style: IconButton.styleFrom(
                                          backgroundColor:
                                              BaseConfig.appThemeColor1,
                                          shape: RoundedRectangleBorder(
                                            borderRadius:
                                                BorderRadius.circular(12.r),
                                          ),
                                          padding: EdgeInsets.all(12.w),
                                        ),
                                        onPressed: () {
                                          Get.toNamed(
                                            AppRoutes
                                                .PROPERTY_MY_PAYMENT_SCREEN,
                                          );
                                        },
                                        icon: const Icon(
                                          BaseConfig.my_payment_icon,
                                        ),
                                      ),
                                      SizedBox(height: 4.h),
                                      SmallTextNotoSans(
                                        text: "My\nPayments",
                                        size: 10.sp,
                                        maxLine: 2,
                                        fontWeight: FontWeight.w600,
                                        textAlign: TextAlign.center,
                                        textOverflow: TextOverflow.ellipsis,
                                      ),
                                    ],
                                  ),
                                ],
                              ),
                            ).paddingAll(16.w),
                          ),
                        ),
                      ],
                    ),
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      MediumTextNotoSans(
                        text: 'Help Desk',
                        fontWeight: FontWeight.w700,
                        size: o == Orientation.portrait ? 14.sp : 8.sp,
                      ),
                      SizedBox(
                        height: 12.h,
                      ),
                      Container(
                        height: o == Orientation.portrait ? 190.h : 225.h,
                        width: Get.width,
                        decoration: BoxDecoration(
                          image: DecorationImage(
                            image: Image.asset(
                              BaseConfig.cardBackgroundImg,
                            ).image,
                            fit: BoxFit.fill,
                          ),
                        ),
                        child: Padding(
                          padding: o == Orientation.portrait
                              ? EdgeInsets.all(16.w)
                              : EdgeInsets.all(8.w),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              SmallTextNotoSans(
                                text: 'Call Center / Helpline',
                                size: o == Orientation.portrait ? 10.sp : 6.sp,
                                fontWeight: FontWeight.w500,
                              ),
                              SizedBox(height: 8.h),
                              Wrap(
                                crossAxisAlignment: WrapCrossAlignment.center,
                                children: [
                                  SvgPicture.asset(
                                    BaseConfig.phoneIconSvg,
                                  ),
                                  SizedBox(width: 8.w),
                                  SmallTextNotoSans(
                                    text: (_languageController
                                                    .mdmsStaticData
                                                    ?.mdmsRes
                                                    ?.commonMasters
                                                    ?.staticData !=
                                                null &&
                                            _languageController
                                                .mdmsStaticData!
                                                .mdmsRes!
                                                .commonMasters!
                                                .staticData!
                                                .isNotEmpty)
                                        ? _languageController
                                                .mdmsStaticData!
                                                .mdmsRes!
                                                .commonMasters!
                                                .staticData!
                                                .first
                                                .pt
                                                ?.helpline
                                                ?.contactOne ??
                                            "-"
                                        : "-",
                                    color: BaseConfig.textColor2,
                                    size: o == Orientation.portrait
                                        ? 12.sp
                                        : 6.sp,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ],
                              ),
                              SizedBox(height: 8.h),
                              Wrap(
                                crossAxisAlignment: WrapCrossAlignment.center,
                                children: [
                                  SvgPicture.asset(
                                    BaseConfig.phoneIconSvg,
                                  ),
                                  SizedBox(width: 8.w),
                                  SmallTextNotoSans(
                                    text: (_languageController
                                                    .mdmsStaticData
                                                    ?.mdmsRes
                                                    ?.commonMasters
                                                    ?.staticData !=
                                                null &&
                                            _languageController
                                                .mdmsStaticData!
                                                .mdmsRes!
                                                .commonMasters!
                                                .staticData!
                                                .isNotEmpty)
                                        ? _languageController
                                                .mdmsStaticData!
                                                .mdmsRes!
                                                .commonMasters!
                                                .staticData!
                                                .first
                                                .pt
                                                ?.helpline
                                                ?.contactTwo ??
                                            "-"
                                        : "-",
                                    color: BaseConfig.textColor2,
                                    size: o == Orientation.portrait
                                        ? 12.sp
                                        : 6.sp,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ],
                              ),
                              SizedBox(height: 2.h),
                              const Divider(
                                color: BaseConfig.borderColor,
                              ),
                              Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  SmallTextNotoSans(
                                    text: 'Citizen Service Center',
                                    size: o == Orientation.portrait
                                        ? 10.sp
                                        : 6.sp,
                                    fontWeight: FontWeight.w500,
                                  ),
                                  TextButtonNotoSans(
                                    text: 'View on Map',
                                    fontSize: o == Orientation.portrait
                                        ? 10.sp
                                        : 6.sp,
                                    onPressed: () async {
                                      Uri googleUrl = Uri.parse(
                                        (_languageController
                                                        .mdmsStaticData
                                                        ?.mdmsRes
                                                        ?.commonMasters
                                                        ?.staticData !=
                                                    null &&
                                                _languageController
                                                    .mdmsStaticData!
                                                    .mdmsRes!
                                                    .commonMasters!
                                                    .staticData!
                                                    .isNotEmpty)
                                            ? _languageController
                                                    .mdmsStaticData!
                                                    .mdmsRes!
                                                    .commonMasters!
                                                    .staticData!
                                                    .first
                                                    .pt
                                                    ?.viewMapLocation ??
                                                "-"
                                            : "-",
                                      );
                                      if (await canLaunchUrl(googleUrl)) {
                                        await launchUrl(
                                          googleUrl,
                                          mode: LaunchMode.externalApplication,
                                        );
                                      }
                                    },
                                    padding: EdgeInsets.symmetric(
                                      horizontal: 4.w,
                                    ),
                                  ),
                                ],
                              ),
                              SizedBox(height: 8.h),
                              Row(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  SvgPicture.asset(
                                    BaseConfig.buildingIconSvg,
                                  ).paddingOnly(top: 4.h),
                                  SizedBox(width: 8.w),
                                  Expanded(
                                    child: SmallTextNotoSans(
                                      text: (_languageController
                                                      .mdmsStaticData
                                                      ?.mdmsRes
                                                      ?.commonMasters
                                                      ?.staticData !=
                                                  null &&
                                              _languageController
                                                  .mdmsStaticData!
                                                  .mdmsRes!
                                                  .commonMasters!
                                                  .staticData!
                                                  .isNotEmpty)
                                          ? _languageController
                                                  .mdmsStaticData!
                                                  .mdmsRes!
                                                  .commonMasters!
                                                  .staticData!
                                                  .first
                                                  .pt
                                                  ?.serviceCenter ??
                                              "-"
                                          : "-",
                                      fontWeight: FontWeight.w500,
                                      color: BaseConfig.textColor2,
                                      maxLine: 2,
                                      size: o == Orientation.portrait
                                          ? 12.sp
                                          : 6.sp,
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                      //TabBar widget
                      // _buildPropertyTabBar(
                      //   o: o,
                      // ),
                    ],
                  ).paddingAll(16.w),
                  const SizedBox(
                    height: 20,
                  ),
                  Card(
                    elevation: 0,
                    margin: EdgeInsets.symmetric(horizontal: 16.w),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12.r),
                      side: BorderSide(
                        color: BaseConfig.borderColor,
                        width: 1.w,
                      ),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Obx(
                              () => BenefitPoint(
                                text:
                                    '${issuedValue.value} Application issued in last 12 months',
                                o: o,
                              ),
                            ),
                            SizedBox(height: 12.h),
                            Obx(
                              () => BenefitPoint(
                                text:
                                    '${renewedValue.value} Application renewed in last 12 months',
                                o: o,
                              ),
                            ),
                            SizedBox(height: 12.h),
                            BenefitPoint(
                              text:
                                  'Average time of processing an application is less than ${(_languageController.mdmsStaticData?.mdmsRes?.commonMasters?.staticData != null && _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.isNotEmpty) ? _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.first.pt?.staticDataOne ?? "-" : "-"}',
                              o: o,
                            ),
                            SizedBox(height: 12.h),
                            BenefitPoint(
                              text:
                                  'Total fee collected for application processing is about Rs ${(_languageController.mdmsStaticData?.mdmsRes?.commonMasters?.staticData != null && _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.isNotEmpty) ? _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.first.pt?.staticDataTwo ?? "-" : "-"}',
                              o: o,
                            ),
                          ],
                        ).paddingAll(16.w),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
