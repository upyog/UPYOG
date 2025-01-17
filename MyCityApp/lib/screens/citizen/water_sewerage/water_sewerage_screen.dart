import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/widgets/benefit_point.dart';
import 'package:mobile_app/widgets/grid_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:url_launcher/url_launcher.dart';

class WaterSewerageScreen extends StatefulWidget {
  const WaterSewerageScreen({super.key});

  @override
  State<WaterSewerageScreen> createState() => _WaterSewerageScreenState();
}

class _WaterSewerageScreenState extends State<WaterSewerageScreen> {
  final _languageController = Get.find<LanguageController>();
  final _commonController = Get.find<CommonController>();

  @override
  void initState() {
    super.initState();
    _fetchLabelsAsync();
  }

  Future<void> _fetchLabelsAsync() async {
    await _commonController.fetchLabels(modules: Modules.WS);
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
            title: 'Water & Sewerage',
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
                            child: Padding(
                              padding: EdgeInsets.all(20.h),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  MediumTextNotoSans(
                                    text: 'Water',
                                    fontWeight: FontWeight.w700,
                                    size: o == Orientation.portrait
                                        ? 14.sp
                                        : 8.sp,
                                  ),
                                  SizedBox(height: 8.h),
                                  SizedBox(
                                    height: o == Orientation.portrait
                                        ? Get.height * 0.12
                                        : Get.height * 0.3,
                                    child: GridView.count(
                                      physics:
                                          const NeverScrollableScrollPhysics(),
                                      primary: false,
                                      crossAxisSpacing: 5,
                                      crossAxisCount: 4,
                                      childAspectRatio: 2 / 5,
                                      children: [
                                        GridCardWidget(
                                          o: o,
                                          text: 'My\nApplications',
                                          onPressed: () => Get.toNamed(
                                            AppRoutes.WATER_MY_APPLICATIONS,
                                          ),
                                          icon: const Icon(Icons.list_alt),
                                        ),
                                        GridCardWidget(
                                          o: o,
                                          text: 'My\nConnections',
                                          onPressed: () {
                                            Get.toNamed(
                                              AppRoutes.WATER_MY_CONNECTION,
                                            );
                                          },
                                          icon: const Icon(Icons.apartment),
                                        ),
                                        GridCardWidget(
                                          o: o,
                                          text: 'My\nBills',
                                          onPressed: () {
                                            Get.toNamed(
                                              AppRoutes.WATER_MY_BILLS,
                                            );
                                          },
                                          icon:
                                              const Icon(Icons.currency_rupee),
                                        ),
                                        GridCardWidget(
                                          o: o,
                                          text: 'My\nPayments',
                                          onPressed: () {
                                            Get.toNamed(
                                              AppRoutes.WATER_MY_PAYMENT,
                                            );
                                          },
                                          icon: const Icon(Icons.receipt_long),
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
                  const SizedBox(
                    height: 30,
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
                    child: Padding(
                      padding: EdgeInsets.all(20.h),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          MediumTextNotoSans(
                            text: 'Sewerage',
                            fontWeight: FontWeight.w700,
                            size: o == Orientation.portrait ? 14.sp : 8.sp,
                          ),
                          SizedBox(height: 8.h),
                          SizedBox(
                            height: o == Orientation.portrait
                                ? Get.height * 0.12
                                : Get.height * 0.3,
                            child: GridView.count(
                              physics: const NeverScrollableScrollPhysics(),
                              primary: false,
                              crossAxisSpacing: 5,
                              crossAxisCount: 4,
                              childAspectRatio: 2 / 5,
                              children: [
                                GridCardWidget(
                                  o: o,
                                  text: 'My\nApplications',
                                  onPressed: () => Get.toNamed(
                                    AppRoutes.SEWERAGE_MY_APPLICATIONS,
                                  ),
                                  icon: const Icon(Icons.list_alt),
                                ),
                                GridCardWidget(
                                  o: o,
                                  text: 'My\nConnections',
                                  onPressed: () {
                                    Get.toNamed(
                                      AppRoutes.SEWERAGE_MY_CONNECTION,
                                    );
                                  },
                                  icon: const Icon(Icons.apartment),
                                ),
                                GridCardWidget(
                                  o: o,
                                  text: 'My\nBills',
                                  onPressed: () {
                                    Get.toNamed(AppRoutes.SEWERAGE_MY_BILLS);
                                  },
                                  icon: const Icon(Icons.currency_rupee),
                                ),
                                GridCardWidget(
                                  o: o,
                                  text: 'My\nPayments',
                                  onPressed: () {
                                    Get.toNamed(AppRoutes.SEWERAGE_MY_PAYMENT);
                                  },
                                  icon: const Icon(Icons.receipt_long),
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
                                                .ws
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
                                                .ws
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
                                                    .ws
                                                    ?.viewMapLocation ??
                                                ""
                                            : "",
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
                                                  .ws
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
                                Column(
                                  children: [
                                    BenefitPoint(
                                      text:
                                          'Pay Water charges by ${(_languageController.mdmsStaticData?.mdmsRes?.commonMasters?.staticData != null && _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.isNotEmpty) ? _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.first.ws?.staticDataOne ?? "-" : "-"} days of bill generation to avoid late fee.',
                                      o: o,
                                    ),
                                    SizedBox(height: 12.h),
                                    BenefitPoint(
                                      text:
                                          'Average time of processing an application is less than ${(_languageController.mdmsStaticData?.mdmsRes?.commonMasters?.staticData != null && _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.isNotEmpty) ? _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.first.ws?.staticDataTwo ?? "-" : "-"} days',
                                      o: o,
                                    ),
                                  ],
                                ),
                              ],
                            ).paddingAll(16.w),
                          ],
                        ),
                      ),
                      SizedBox(
                        height: 24.h,
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
