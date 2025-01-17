import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/edit_profile_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/controller/language_controller.dart';
import 'package:mobile_app/model/citizen/grievance/grievance.dart' as gr;
import 'package:mobile_app/model/citizen/localization/language.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/benefit_point.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/tabbar_widget.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:url_launcher/url_launcher.dart';

class GrievancesScreen extends StatefulWidget {
  const GrievancesScreen({super.key});

  @override
  State<GrievancesScreen> createState() => _GrievancesScreenState();
}

class _GrievancesScreenState extends State<GrievancesScreen> {
  final _authController = Get.find<AuthController>();
  final _editProfileController = Get.find<EditProfileController>();
  final _grievanceController = Get.find<GrievanceController>();
  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  final _commonController = Get.find<CommonController>();
  final _languageController = Get.find<LanguageController>();

  bool get isValidToken => _authController.token?.accessToken != null;

  final _selectedIndex = 0.obs;

  @override
  void initState() {
    super.initState();
    _fetchLabelsAsync();
  }

  Future<void> _fetchLabelsAsync() async {
    _grievanceController.length.value = 0;
    await _commonController.fetchLabels(modules: Modules.PGR);
    await _fetchGrievance();
  }

  Future<void> _fetchGrievance() async {
    try {
      TenantTenant tenantCity = await getCityTenant();
      await _grievanceController.getGrievance(
        token: _authController.token!.accessToken!,
        tenantId: '${tenantCity.code}',
        mobileNo: _editProfileController.userProfile.user!.first.mobileNumber!,
      );
    } catch (e) {
      dPrint('Grievance Error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Scaffold(
          appBar: HeaderTop(
            orientation: o,
            title: getLocalizedString(i18.grievance.GRIEVANCE),
            onPressed: () => Navigator.of(context).pop(),
          ),
          body: SizedBox(
            height: Get.height,
            width: Get.width,
            child: StreamBuilder(
              stream: _grievanceController.streamCtrl.stream,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  if (snapshot.data is String || snapshot.data == null) {
                    return const NoApplicationFoundWidget();
                  }
                  final gr.Grievance data = snapshot.data;
                  final complaintResolved = data.complaintsResolved ?? 0;
                  final averageComplaintResolved =
                      data.averageResolutionTime ?? 0;
                  final complaintTypes = data.complaintTypes ?? 0;
                  final openGrievanceList = data.serviceWrappers
                      ?.where(
                        (element) => (element.service?.applicationStatus ==
                                GrievanceStatus.PENDING.name ||
                            element.service?.applicationStatus ==
                                GrievanceStatus.PENDING_SUPERVISOR.name),
                      )
                      .toList();

                  final closedGrievanceList = data.serviceWrappers
                      ?.where(
                        (element) => (element.service?.applicationStatus ==
                                GrievanceStatus.CLOSED_SOLUTION.name ||
                            element.service?.applicationStatus ==
                                GrievanceStatus.CLOSED_REJECTION.name ||
                            element.service?.applicationStatus ==
                                GrievanceStatus.REJECTED.name ||
                            element.service?.applicationStatus ==
                                GrievanceStatus.RESOLVED.name),
                      )
                      .toList();

                  if (!isNotNullOrEmpty(data.serviceWrappers)) {
                    return const NoApplicationFoundWidget();
                  } else {
                    return SingleChildScrollView(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          SizedBox(
                            height: o == Orientation.portrait
                                ? Get.height * 0.34
                                : 330, //Get.height * 0.8,
                            child: Stack(
                              children: [
                                Container(
                                  height:
                                      o == Orientation.portrait ? 150.h : 180.h,
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
                                      ? 50 //Get.height * 0.14
                                      : 90,
                                  child: Container(
                                    margin:
                                        EdgeInsets.symmetric(horizontal: 16.w),
                                    child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        MediumTextNotoSans(
                                          text: 'Help Desk',
                                          fontWeight: FontWeight.w700,
                                          size: o == Orientation.portrait
                                              ? 14.sp
                                              : 8.sp,
                                        ),
                                        SizedBox(
                                          height: 12.h,
                                        ),
                                        Container(
                                          decoration: BoxDecoration(
                                            borderRadius:
                                                BorderRadius.circular(15),
                                            color: Colors.white,
                                            image: DecorationImage(
                                              image: Image.asset(
                                                BaseConfig.cardBackgroundImg,
                                              ).image,
                                              fit: BoxFit.fill,
                                            ),
                                          ),
                                          child: Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              SmallTextNotoSans(
                                                text: 'Call Center / Helpline',
                                                size: o == Orientation.portrait
                                                    ? 10.sp
                                                    : 6.sp,
                                                fontWeight: FontWeight.w500,
                                              ),
                                              SizedBox(height: 8.h),
                                              Wrap(
                                                crossAxisAlignment:
                                                    WrapCrossAlignment.center,
                                                children: [
                                                  SvgPicture.asset(
                                                    BaseConfig.phoneIconSvg,
                                                  ),
                                                  SizedBox(width: 8.w),
                                                  SmallTextNotoSans(
                                                    text: (isNotNullOrEmpty(
                                                      _languageController
                                                          .mdmsStaticData
                                                          ?.mdmsRes
                                                          ?.commonMasters
                                                          ?.staticData,
                                                    ))
                                                        ? _languageController
                                                                .mdmsStaticData
                                                                ?.mdmsRes
                                                                ?.commonMasters
                                                                ?.staticData
                                                                ?.firstOrNull
                                                                ?.pgr
                                                                ?.helpline
                                                                ?.contactOne ??
                                                            "-"
                                                        : "-",
                                                    color:
                                                        BaseConfig.textColor2,
                                                    size: o ==
                                                            Orientation.portrait
                                                        ? 12.sp
                                                        : 6.sp,
                                                    fontWeight: FontWeight.w500,
                                                  ),
                                                ],
                                              ),
                                              SizedBox(height: 8.h),
                                              Wrap(
                                                crossAxisAlignment:
                                                    WrapCrossAlignment.center,
                                                children: [
                                                  SvgPicture.asset(
                                                    BaseConfig.phoneIconSvg,
                                                  ),
                                                  SizedBox(width: 8.w),
                                                  SmallTextNotoSans(
                                                    text: (isNotNullOrEmpty(
                                                      _languageController
                                                          .mdmsStaticData
                                                          ?.mdmsRes
                                                          ?.commonMasters
                                                          ?.staticData,
                                                    ))
                                                        ? _languageController
                                                                .mdmsStaticData
                                                                ?.mdmsRes
                                                                ?.commonMasters
                                                                ?.staticData
                                                                ?.firstOrNull
                                                                ?.pgr
                                                                ?.helpline
                                                                ?.contactTwo ??
                                                            "-"
                                                        : "-",
                                                    color:
                                                        BaseConfig.textColor2,
                                                    size: o ==
                                                            Orientation.portrait
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
                                                    MainAxisAlignment
                                                        .spaceBetween,
                                                children: [
                                                  SmallTextNotoSans(
                                                    text:
                                                        'Citizen Service Center',
                                                    size: o ==
                                                            Orientation.portrait
                                                        ? 10.sp
                                                        : 6.sp,
                                                    fontWeight: FontWeight.w500,
                                                  ),
                                                  TextButtonNotoSans(
                                                    text: 'View on Map',
                                                    fontSize: o ==
                                                            Orientation.portrait
                                                        ? 10.sp
                                                        : 6.sp,
                                                    onPressed: () async {
                                                      Uri googleUrl = Uri.parse(
                                                        (isNotNullOrEmpty(
                                                          _languageController
                                                              .mdmsStaticData
                                                              ?.mdmsRes
                                                              ?.commonMasters
                                                              ?.staticData,
                                                        ))
                                                            ? _languageController
                                                                    .mdmsStaticData
                                                                    ?.mdmsRes
                                                                    ?.commonMasters
                                                                    ?.staticData
                                                                    ?.firstOrNull
                                                                    ?.pgr
                                                                    ?.viewMapLocation ??
                                                                ""
                                                            : "",
                                                      );
                                                      if (await canLaunchUrl(
                                                        googleUrl,
                                                      )) {
                                                        await launchUrl(
                                                          googleUrl,
                                                          mode: LaunchMode
                                                              .externalApplication,
                                                        );
                                                      }
                                                    },
                                                    padding:
                                                        EdgeInsets.symmetric(
                                                      horizontal: 4.w,
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              SizedBox(height: 8.h),
                                              Row(
                                                crossAxisAlignment:
                                                    CrossAxisAlignment.start,
                                                mainAxisSize: MainAxisSize.min,
                                                children: [
                                                  SvgPicture.asset(
                                                    BaseConfig.buildingIconSvg,
                                                  ).paddingOnly(top: 4.h),
                                                  SizedBox(width: 8.w),
                                                  Expanded(
                                                    child: SmallTextNotoSans(
                                                      text: (isNotNullOrEmpty(
                                                        _languageController
                                                            .mdmsStaticData
                                                            ?.mdmsRes
                                                            ?.commonMasters
                                                            ?.staticData,
                                                      ))
                                                          ? _languageController
                                                                  .mdmsStaticData
                                                                  ?.mdmsRes
                                                                  ?.commonMasters
                                                                  ?.staticData
                                                                  ?.firstOrNull
                                                                  ?.pgr
                                                                  ?.serviceCenter ??
                                                              "-"
                                                          : "-",
                                                      fontWeight:
                                                          FontWeight.w500,
                                                      color:
                                                          BaseConfig.textColor2,
                                                      maxLine: 2,
                                                      size: o ==
                                                              Orientation
                                                                  .portrait
                                                          ? 12.sp
                                                          : 6.sp,
                                                    ),
                                                  ),
                                                ],
                                              ),
                                            ],
                                          ).paddingAll(16.w),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Obx(
                                    () => MediumTextNotoSans(
                                      text:
                                          'My Complaints ${_selectedIndex.value == 0 ? '(${openGrievanceList?.length ?? 0})' : '(${closedGrievanceList?.length ?? 0})'}',
                                      fontWeight: FontWeight.w700,
                                      size: o == Orientation.portrait
                                          ? 14.sp
                                          : 8.sp,
                                    ),
                                  ),
                                  TextButtonNotoSans(
                                    padding: EdgeInsets.symmetric(
                                      horizontal: 8.w,
                                      vertical: 2.h,
                                    ),
                                    text: 'View All',
                                    onPressed: () {
                                      Get.toNamed(
                                        AppRoutes
                                            .GRIEVANCES_COMPLAINTS_VIEW_ALL,
                                      );
                                    },
                                    fontWeight: FontWeight.w600,
                                    fontSize: o == Orientation.portrait
                                        ? 14.sp
                                        : 8.sp,
                                  ),
                                ],
                              ),
                              SizedBox(
                                height: 12.h,
                              ),

                              //TabBar widget
                              SizedBox(
                                height: o == Orientation.portrait
                                    ? Get.height * 0.55
                                    : 500,
                                child: _buildTabBar(
                                  o: o,
                                  openGrievanceList: openGrievanceList,
                                  closedGrievanceList: closedGrievanceList,
                                ),
                              ),
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
                                    MediumTextNotoSans(
                                      text: 'Benefits',
                                      fontWeight: FontWeight.w700,
                                      size: o == Orientation.portrait
                                          ? 14.sp
                                          : 8.sp,
                                    ),
                                    SizedBox(height: 8.h),
                                    BenefitPoint(
                                      text:
                                          '$complaintResolved complaints resolved in the last 30 days',
                                      o: o,
                                    ),
                                    SizedBox(height: 12.h),
                                    BenefitPoint(
                                      text:
                                          '$averageComplaintResolved days is the average complaint resolution time',
                                      o: o,
                                    ),
                                    SizedBox(height: 12.h),
                                    BenefitPoint(
                                      o: o,
                                      text:
                                          'Categories of complaint types can be submitted on grievance portal $complaintTypes',
                                    ),
                                  ],
                                ).paddingAll(16.w),
                                SizedBox(height: 16.h),
                              ],
                            ),
                          ),
                          const SizedBox(
                            height: 30,
                          ),
                        ],
                      ),
                    );
                  }
                } else if (snapshot.hasError) {
                  return networkErrorPage(
                    context,
                    () => _fetchGrievance(),
                  );
                } else {
                  switch (snapshot.connectionState) {
                    case ConnectionState.waiting:
                      return showCircularIndicator().marginOnly(top: 20.h);
                    case ConnectionState.active:
                      return showCircularIndicator().marginOnly(top: 20.h);
                    default:
                      return const SizedBox.shrink();
                  }
                }
              },
            ),
          ),
        );
      },
    );
  }

  Widget _buildTabBar({
    required Orientation o,
    openGrievanceList,
    closedGrievanceList,
  }) {
    return ConstrainedBox(
      constraints: BoxConstraints(
        maxHeight:
            o == Orientation.portrait ? Get.height * 0.6 : Get.height * 0.8,
      ),
      child: TabBarWidget(
        tabHeight: o == Orientation.portrait ? null : 70.h,
        tabText1: 'Open Requests',
        tabText2: 'Closed Requests',
        onTap: (index) {
          dPrint('TabBarWidget onTap: $index');
          _selectedIndex.value = index;
        },
        children: [
          //Tab -1: Open
          Obx(
            () => _grievanceController.isLoading.value
                ? showCircularIndicator()
                : SingleChildScrollView(
                    physics: const BouncingScrollPhysics(),
                    child: ListView.builder(
                      itemCount: openGrievanceList?.length ?? 0,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemBuilder: (context, index) {
                        final serviceWrappers = openGrievanceList?[index];

                        return isNotNullOrEmpty(serviceWrappers)
                            ? OpenRequestWidget(
                                serviceWrapper: serviceWrappers,
                                o: o,
                              )
                            : const SizedBox.shrink();
                      },
                    ),
                  ),
          ),

          //Tab-2: CLosed Requests
          Obx(
            () => _grievanceController.isLoading.value
                ? showCircularIndicator()
                : SingleChildScrollView(
                    physics: const BouncingScrollPhysics(),
                    child: ListView.builder(
                      itemCount: closedGrievanceList?.length ?? 0,
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemBuilder: (context, i) {
                        final serviceWrappers = closedGrievanceList?[i];
                        return isNotNullOrEmpty(serviceWrappers)
                            ? ClosedRequestWidget(
                                serviceWrapper: serviceWrappers,
                                o: o,
                              )
                            : const SizedBox.shrink();
                      },
                    ),
                  ),
          ),
        ],
      ),
    );
  }
}

class OpenRequestWidget extends StatelessWidget {
  const OpenRequestWidget({
    super.key,
    this.o = Orientation.portrait,
    required this.serviceWrapper,
  });
  final Orientation o;
  final gr.ServiceWrapper serviceWrapper;

  @override
  Widget build(BuildContext context) {
    return ComplainCard(
      title: isNotNullOrEmpty(serviceWrapper.service?.serviceCode)
          ? getLocalizedString(
              '${i18.common.SERVICE_DEFS}${serviceWrapper.service?.serviceCode}'
                  .toUpperCase(),
              module: Modules.PGR,
            )
          : 'N/A',
      id: 'Grievance ID: ${serviceWrapper.service?.serviceRequestId ?? ''}',
      date:
          'Date: ${serviceWrapper.service?.auditDetails?.createdTime.toCustomDateFormat() ?? 'd-MMM-yyyy'}',
      status: getLocalizedString(
        '${i18.common.CS_COMMON}${serviceWrapper.service?.applicationStatus}',
        module: Modules.PGR,
      ),
      rating: serviceWrapper.service?.rating,
      statusColor: getGrievanceStatusTextColor(
        serviceWrapper.service!.applicationStatus!,
      ),
      statusBackColor: getGrievanceStatusBackColor(
        serviceWrapper.service?.applicationStatus ?? '',
      ),
      o: o,
      onTap: () {
        Get.toNamed(
          AppRoutes.GRIEVANCES_DETAILS_SCREEN,
          arguments: {
            'serviceWrappers': serviceWrapper,
          },
        );
      },
    ).paddingOnly(bottom: 16);
  }
}

class ClosedRequestWidget extends StatelessWidget {
  const ClosedRequestWidget({
    super.key,
    this.o = Orientation.portrait,
    required this.serviceWrapper,
  });
  final Orientation o;
  final gr.ServiceWrapper serviceWrapper;

  @override
  Widget build(BuildContext context) {
    return ComplainCard(
      title: isNotNullOrEmpty(serviceWrapper.service?.serviceCode)
          ? getLocalizedString(
              '${i18.common.SERVICE_DEFS}${serviceWrapper.service?.serviceCode}'
                  .toUpperCase(),
              module: Modules.PGR,
            )
          : 'N/A',
      id: 'Grievance ID: ${serviceWrapper.service?.serviceRequestId ?? ''}',
      date:
          'Date: ${serviceWrapper.service?.auditDetails?.createdTime.toCustomDateFormat() ?? 'd-MMM-yyyy'}',
      status: getLocalizedString(
        '${i18.common.CS_COMMON}${serviceWrapper.service?.applicationStatus}',
        module: Modules.PGR,
      ),
      rating: serviceWrapper.service?.rating,
      statusColor: getGrievanceStatusTextColor(
        serviceWrapper.service?.applicationStatus ?? '',
      ),
      statusBackColor: getGrievanceStatusBackColor(
        serviceWrapper.service?.applicationStatus ?? '',
      ),
      o: o,
      onTap: () {
        Get.toNamed(
          AppRoutes.GRIEVANCES_DETAILS_SCREEN,
          arguments: {
            'serviceWrappers': serviceWrapper,
          },
        );
      },
    ).paddingOnly(bottom: 16);
  }
}
