import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/controller/trade_license_controller.dart';
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/model/citizen/trade_license/trade_license.dart'
    as tl;
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/benefit_point.dart';
import 'package:mobile_app/widgets/grid_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:url_launcher/url_launcher.dart';

class TradeLicenseApplicationScreen extends StatefulWidget {
  const TradeLicenseApplicationScreen({super.key});

  @override
  State<TradeLicenseApplicationScreen> createState() =>
      _TradeLicenseApplicationScreenState();
}

class _TradeLicenseApplicationScreenState
    extends State<TradeLicenseApplicationScreen> {
  final _languageController = Get.find<LanguageController>();
  final _tlController = Get.find<TradeLicenseController>();
  final _authController = Get.find<AuthController>();
  final _commonController = Get.find<CommonController>();

  @override
  void initState() {
    getTlApplication();
    super.initState();
  }

  void getTlApplication() async {
    await _commonController.fetchLabels(modules: Modules.TL);

    TenantTenant tenant = await getCityTenant();

    _tlController.getTlApplications(
      token: _authController.token!.accessToken!,
      tenantId: tenant.code,
      renewalTlApp: TradeAppType.NONE.name,
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
            title: getLocalizedString(i18.common.TRADE_LICENSE),
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: SingleChildScrollView(
              child: Column(
                children: [
                  SizedBox(
                    height: Get.height * 0.3,
                    child: Stack(
                      children: [
                        Container(
                          height: o == Orientation.portrait ? 150.h : 100.h,
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
                        ),
                        Positioned(
                          left: 0,
                          right: 0,
                          top: o == Orientation.portrait
                              ? Get.height * 0.09
                              : 50,
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
                            child: Padding(
                              padding: EdgeInsets.all(20.h),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  SizedBox(height: 12.h),
                                  SizedBox(
                                    height: o == Orientation.portrait
                                        ? Get.height * 0.12
                                        : Get.height * 0.3,
                                    child: GridView.count(
                                      physics:
                                          const NeverScrollableScrollPhysics(),
                                      primary: false,
                                      crossAxisSpacing: 5,
                                      crossAxisCount: 2,
                                      childAspectRatio: 2 / 5,
                                      children: [
                                        GridCardWidget(
                                          o: o,
                                          text: 'New TL\nApplications',
                                          onPressed: () {
                                            Get.toNamed(
                                              AppRoutes.NEW_TL_APPLICATIONS,
                                            );
                                          },
                                          icon: const Icon(Icons.list_alt),
                                        ),
                                        GridCardWidget(
                                          o: o,
                                          text: 'Renewal\nApplications',
                                          onPressed: () {
                                            Get.toNamed(
                                              AppRoutes.TL_APP_RENEWAL,
                                            );
                                          },
                                          icon: const Icon(Icons.autorenew),
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                            ),
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
                                                .tl
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
                                                .tl
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
                                                    .tl
                                                    ?.viewMapLocation ??
                                                ""
                                            : "",
                                      );
                                      if (await canLaunchUrl(
                                        googleUrl,
                                      )) {
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
                                                  .tl
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
                      SizedBox(
                        height: o == Orientation.portrait ? 10.h : 12.h,
                      ),
                    ],
                  ).paddingAll(16.w),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      MediumTextNotoSans(
                        text: 'Benefits',
                        fontWeight: FontWeight.w700,
                        size: o == Orientation.portrait ? 14.sp : 8.sp,
                      ).paddingOnly(left: 16.w),
                      SizedBox(height: 8.h),
                      StreamBuilder(
                        stream: _tlController.streamCtrl.stream,
                        builder: (context, snapshot) {
                          if (snapshot.hasData) {
                            if (snapshot.data is String ||
                                snapshot.data == null) {
                              return const Center(
                                child: Text("No Data"),
                              );
                            }
                            final tl.TradeLicense tlLicense = snapshot.data;
                            return Card(
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
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Column(
                                        children: [
                                          BenefitPoint(
                                            text:
                                                '${tlLicense.applicationsIssued ?? 0} Trade Licenses issued in the last 12 months',
                                            o: o,
                                          ),
                                          SizedBox(height: 12.h),
                                          BenefitPoint(
                                            text:
                                                '${tlLicense.applicationsRenewed ?? 0} Trade Licenses renewed in the last 12 months',
                                            o: o,
                                          ),
                                          SizedBox(height: 12.h),
                                          BenefitPoint(
                                            o: o,
                                            text:
                                                'Trade License validity is ${tlLicense.applicationValidity ?? 0} year',
                                          ),
                                        ],
                                      ),
                                    ],
                                  ).paddingAll(16.w),
                                ],
                              ),
                            );
                          } else if (snapshot.hasError) {
                            return networkErrorPage(
                              context,
                              () => getTlApplication(),
                            );
                          } else {
                            switch (snapshot.connectionState) {
                              case ConnectionState.waiting:
                                return showCircularIndicator()
                                    .marginOnly(top: 20.h);
                              case ConnectionState.active:
                                return showCircularIndicator()
                                    .marginOnly(top: 20.h);
                              default:
                                return const SizedBox.shrink();
                            }
                          }
                        },
                      ),
                    ],
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
