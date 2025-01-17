import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/benefit_point.dart';
import 'package:mobile_app/widgets/grid_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:url_launcher/url_launcher.dart';

class MiscCollections extends StatefulWidget {
  const MiscCollections({super.key});

  @override
  State<MiscCollections> createState() => _MiscCollectionsState();
}

class _MiscCollectionsState extends State<MiscCollections> {
  final _commonController = Get.find<CommonController>();
  final _languageController = Get.find<LanguageController>();

  var ucModuleLoaded = false.obs;

  @override
  void initState() {
    super.initState();
    init();
  }

  init() async {
    await _commonController.fetchLabels(modules: Modules.UC);
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        orientation: o,
        title: getLocalizedString(i18.common.MCOLLECT),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
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
                        color: BaseConfig.appThemeColor1.withValues(alpha: 0.1),
                      ),
                    ),
                    Positioned(
                      left: 0,
                      right: 0,
                      top: o == Orientation.portrait ? Get.height * 0.09 : 50,
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
                                  physics: const NeverScrollableScrollPhysics(),
                                  primary: false,
                                  crossAxisSpacing: 5,
                                  crossAxisCount: 2,
                                  childAspectRatio: 2 / 5,
                                  children: [
                                    GridCardWidget(
                                      o: o,
                                      text: 'My\nChallans',
                                      onPressed: () {
                                        Get.toNamed(
                                          AppRoutes.MY_CHALLANS,
                                        );
                                      },
                                      icon: const Icon(Icons.list_alt),
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
                                text: (isNotNullOrEmpty(
                                  _languageController.mdmsStaticData?.mdmsRes
                                      ?.commonMasters?.staticData,
                                ))
                                    ? _languageController
                                            .mdmsStaticData!
                                            .mdmsRes!
                                            .commonMasters!
                                            .staticData!
                                            .first
                                            .obps
                                            ?.helpline
                                            ?.contactOne ??
                                        "-"
                                    : "-",
                                color: BaseConfig.textColor2,
                                size: o == Orientation.portrait ? 12.sp : 6.sp,
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
                                text: (isNotNullOrEmpty(
                                  _languageController.mdmsStaticData?.mdmsRes
                                      ?.commonMasters?.staticData,
                                ))
                                    ? _languageController
                                            .mdmsStaticData!
                                            .mdmsRes!
                                            .commonMasters!
                                            .staticData!
                                            .first
                                            .obps
                                            ?.helpline
                                            ?.contactTwo ??
                                        "-"
                                    : "-",
                                color: BaseConfig.textColor2,
                                size: o == Orientation.portrait ? 12.sp : 6.sp,
                                fontWeight: FontWeight.w500,
                              ),
                            ],
                          ),
                          SizedBox(height: 2.h),
                          const Divider(
                            color: BaseConfig.borderColor,
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              SmallTextNotoSans(
                                text: 'Citizen Service Center',
                                size: o == Orientation.portrait ? 10.sp : 6.sp,
                                fontWeight: FontWeight.w500,
                              ),
                              TextButtonNotoSans(
                                text: 'View on Map',
                                fontSize:
                                    o == Orientation.portrait ? 10.sp : 6.sp,
                                onPressed: () async {
                                  Uri googleUrl = Uri.parse(
                                    (isNotNullOrEmpty(
                                      _languageController.mdmsStaticData
                                          ?.mdmsRes?.commonMasters?.staticData,
                                    ))
                                        ? _languageController
                                                .mdmsStaticData!
                                                .mdmsRes!
                                                .commonMasters!
                                                .staticData!
                                                .first
                                                .obps
                                                ?.viewMapLocation ??
                                            ""
                                        : "-",
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
                                              .obps
                                              ?.serviceCenter ??
                                          "-"
                                      : "-",
                                  fontWeight: FontWeight.w500,
                                  color: BaseConfig.textColor2,
                                  maxLine: 2,
                                  size:
                                      o == Orientation.portrait ? 12.sp : 6.sp,
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
                  ),
                  SizedBox(height: 8.h),
                  Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(12.r),
                      border: Border.all(
                        color: BaseConfig.borderColor,
                        width: 1.w,
                      ),
                    ),
                    child: Padding(
                      padding: const EdgeInsets.all(10.0),
                      child: BenefitPoint(
                        text:
                            '${getLocalizedString(i18.common.CHALLAN_VALIDITY)} ${isNotNullOrEmpty(_languageController.mdmsStaticData?.mdmsRes?.commonMasters?.staticData) ? _languageController.mdmsStaticData!.mdmsRes!.commonMasters!.staticData!.first.obps?.validity ?? "-" : "-"} ${getLocalizedString(i18.common.DAY)}',
                        o: o,
                      ),
                    ),
                  ),
                ],
              ).paddingAll(16.w),
            ],
          ),
        ),
      ),
    );
  }
}
