import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/model/citizen/grievance/grievance.dart';
import 'package:mobile_app/model/citizen/localization/mdms_static_data.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_widget_2.dart';
import 'package:mobile_app/widgets/view_more.dart';
import 'package:mobile_app/widgets/wrap_text.dart';

class GrievanceDetailsScreen extends StatefulWidget {
  const GrievanceDetailsScreen({super.key});

  @override
  State<GrievanceDetailsScreen> createState() => _GrievanceDetailsScreenState();
}

class _GrievanceDetailsScreenState extends State<GrievanceDetailsScreen> {
  final _authController = Get.find<AuthController>();
  final _grievanceController = Get.find<GrievanceController>();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  final _isLoading = false.obs;
  ServiceWrapper? serviceWrapper;
  RainmakerPgr? rainmaker;

  @override
  void initState() {
    super.initState();
    _getServiceWrapper();
  }

  void _getServiceWrapper() async {
    try {
      serviceWrapper = Get.arguments['serviceWrappers'];

      _grievanceController.serviceWrapper = serviceWrapper!;

      if (_authController.isValidUser) {
        _isLoading.value = true;

        rainmaker = await _grievanceController.getMdmsPgr();

        await _grievanceController.getIndividualGrievance(
          token: _authController.token!.accessToken!,
          tenantId: serviceWrapper!.service!.tenantId!,
          serviceRequestId: serviceWrapper!.service!.serviceRequestId!,
        );
        _isLoading.value = false;
      }
    } catch (e) {
      dPrint('Error in _getServiceWrapper: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.grievance.DETAILS_HEADER,
          module: Modules.PGR,
        ),
        onPressed: () => Get.back(),
      ),
      body: OrientationBuilder(
        builder: (context, o) {
          return Container(
            height: Get.height,
            width: Get.width,
            padding: EdgeInsets.all(o == Orientation.portrait ? 16.w : 12.w),
            child: Obx(
              () => _isLoading.value
                  ? showCircularIndicator()
                  : SingleChildScrollView(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Expanded(
                                child: SizedBox(
                                  width: Get.width * 0.5.w,
                                  child: Tooltip(
                                    message: isNotNullOrEmpty(
                                      serviceWrapper?.service?.serviceCode,
                                    )
                                        ? getLocalizedString(
                                            '${i18.common.SERVICE_DEFS}${serviceWrapper?.service?.serviceCode}'
                                                .toUpperCase(),
                                            module: Modules.PGR,
                                          )
                                        : 'N/A',
                                    child: MediumTextNotoSans(
                                      text: isNotNullOrEmpty(
                                        serviceWrapper?.service?.serviceCode,
                                      )
                                          ? getLocalizedString(
                                              '${i18.common.SERVICE_DEFS}${serviceWrapper?.service?.serviceCode}'
                                                  .toUpperCase(),
                                              module: Modules.PGR,
                                            )
                                          : 'N/A',
                                      fontWeight: FontWeight.w700,
                                      size: o == Orientation.portrait
                                          ? 14.sp
                                          : 8.sp,
                                      maxLine: 2,
                                      textOverflow: TextOverflow.ellipsis,
                                    ),
                                  ),
                                ),
                              ),
                              Expanded(
                                child: Card(
                                  elevation: 0,
                                  shape: RoundedRectangleBorder(
                                    side: BorderSide(
                                      color: getGrievanceStatusTextColor(
                                        serviceWrapper
                                                ?.service?.applicationStatus ??
                                            '',
                                      ),
                                      width: 1.w,
                                    ),
                                    borderRadius: BorderRadius.circular(8.r),
                                  ),
                                  color: getGrievanceStatusBackColor(
                                    serviceWrapper
                                            ?.service?.applicationStatus ??
                                        '',
                                  ),
                                  child: Padding(
                                    padding: EdgeInsets.symmetric(
                                      horizontal: 14.w,
                                      vertical: 8.h,
                                    ),
                                    child: Center(
                                      child: MediumTextNotoSans(
                                        text: getLocalizedString(
                                          '${i18.common.CS_COMMON}${serviceWrapper?.service?.applicationStatus}',
                                          module: Modules.PGR,
                                        ),
                                        color: getGrievanceStatusTextColor(
                                          serviceWrapper?.service
                                                  ?.applicationStatus ??
                                              '',
                                        ),
                                        size: o == Orientation.portrait
                                            ? 12.sp
                                            : 8.sp,
                                        maxLine: 1,
                                        textOverflow: TextOverflow.ellipsis,
                                      ),
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          SizedBox(height: 16.h),
                          _DetailsBody(
                            o: o,
                            serviceWrapper: serviceWrapper,
                            rainmaker: rainmaker,
                          ),
                          SizedBox(height: 16.h),
                          const Divider(
                            color: BaseConfig.borderColor,
                          ),
                          if (isNotNullOrEmpty(serviceWrapper?.service)) ...[
                            SizedBox(height: 16.h),
                            TimelineWidget2(
                              modules: Modules.PGR,
                              tenantId: serviceWrapper!.service!.tenantId!,
                              businessIds:
                                  serviceWrapper!.service!.serviceRequestId!,
                              status:
                                  serviceWrapper!.service!.applicationStatus!,
                            ),
                          ],
                        ],
                      ),
                    ),
            ),
          );
        },
      ),
    );
  }
}

class _DetailsBody extends StatelessWidget {
  const _DetailsBody({
    this.o = Orientation.portrait,
    this.serviceWrapper,
    this.rainmaker,
  });
  final ServiceWrapper? serviceWrapper;
  final Orientation o;
  final RainmakerPgr? rainmaker;

  @override
  Widget build(BuildContext context) {
    final grievanceController = Get.find<GrievanceController>();

    final grievanceType = rainmaker?.serviceDefs?.firstWhereOrNull((e) {
      return e.serviceCode == serviceWrapper?.service?.serviceCode;
    });

    return Obx(
      () => ViewMore(
        isMore: grievanceController.isMore.value,
        lessWidget: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            WrapText(
              title:
                  '${getLocalizedString(i18.grievance.DETAILS_ID, module: Modules.PGR)}: ',
              text: '${serviceWrapper?.service?.serviceRequestId}',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_COMPLAINT_FILED_DATE,
                module: Modules.PGR,
              )}: ',
              text: serviceWrapper?.service?.auditDetails?.createdTime
                      .toCustomDateFormat() ??
                  'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_TYPE,
                module: Modules.PGR,
              )}: ',
              text: isNotNullOrEmpty(grievanceType?.menuPath)
                  ? getLocalizedString(
                      '${i18.common.SERVICE_DEFS}${grievanceType!.menuPath!.toUpperCase()}',
                      module: Modules.PGR,
                    )
                  : 'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_SUB_TYPE,
                module: Modules.PGR,
              )}: ',
              text: isNotNullOrEmpty(serviceWrapper?.service?.serviceCode)
                  ? getLocalizedString(
                      '${i18.common.SERVICE_DEFS}${serviceWrapper!.service!.serviceCode}'
                          .toUpperCase(),
                      module: Modules.PGR,
                    )
                  : 'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            SizedBox(
              height: 20.h,
              width: o == Orientation.portrait
                  ? Get.width * 0.25
                  : Get.width * 0.14,
              child: TextButtonNotoSans(
                fontSize: o == Orientation.portrait ? 12.sp : 6.sp,
                text: 'View More',
                onPressed: () {
                  grievanceController.isMore.value = true;
                },
                icon: Padding(
                  padding: EdgeInsets.only(left: 4.w),
                  child: Icon(
                    Icons.keyboard_arrow_down,
                    size: o == Orientation.portrait ? 22.sp : 12.sp,
                    color: BaseConfig.appThemeColor1,
                  ),
                ),
              ),
            ),
          ],
        ),
        moreWidget: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            WrapText(
              title:
                  '${getLocalizedString(i18.grievance.DETAILS_ID, module: Modules.PGR)}: ',
              text: '${serviceWrapper?.service?.serviceRequestId}',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_COMPLAINT_FILED_DATE,
                module: Modules.PGR,
              )}: ',
              text: serviceWrapper?.service?.auditDetails?.createdTime
                      .toCustomDateFormat() ??
                  'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_TYPE,
                module: Modules.PGR,
              )}: ',
              text: isNotNullOrEmpty(grievanceType)
                  ? getLocalizedString(
                      '${i18.common.SERVICE_DEFS}${grievanceType!.menuPath!.toUpperCase()}',
                      module: Modules.PGR,
                    )
                  : 'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_SUB_TYPE,
                module: Modules.PGR,
              )}: ',
              text: isNotNullOrEmpty(serviceWrapper?.service?.serviceCode)
                  ? getLocalizedString(
                      '${i18.common.SERVICE_DEFS}${serviceWrapper!.service!.serviceCode}'
                          .toUpperCase(),
                      module: Modules.PGR,
                    )
                  : 'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title:
                  '${getLocalizedString(i18.grievance.DETAILS_PRIORITY, module: Modules.PGR)}: ',
              text: serviceWrapper?.service?.priority ?? 'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            WrapText(
              title: '${getLocalizedString(
                i18.grievance.DETAILS_ADDITIONAL_DETAILS,
                module: Modules.PGR,
              )}: ',
              text: isNotNullOrEmpty(serviceWrapper?.service?.description)
                  ? '${serviceWrapper!.service!.description}'
                  : 'N/A',
              o: o,
            ),
            SizedBox(height: 8.h),
            Row(
              children: [
                SmallTextNotoSans(
                  text:
                      '${getLocalizedString(i18.grievance.ADDRESS, module: Modules.PGR)}: ',
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 12.sp : 6.sp,
                ),
                Row(
                  children: [
                    if (isNotNullOrEmpty(
                      serviceWrapper?.service?.address?.landmark,
                    )) ...[
                      SmallTextNotoSans(
                        text: serviceWrapper!.service!.address!.landmark!,
                        maxLine: 4,
                        fontWeight: FontWeight.w600,
                        size: o == Orientation.portrait ? 12.sp : 6.sp,
                      ),
                      const SmallTextNotoSans(text: ', '),
                    ],
                    if (isNotNullOrEmpty(
                      serviceWrapper?.service?.address?.locality?.code,
                    )) ...[
                      SmallTextNotoSans(
                        text: getLocalizedString(
                          serviceWrapper!.service!.address!.locality!.code,
                        ),
                        maxLine: 4,
                        fontWeight: FontWeight.w600,
                        size: o == Orientation.portrait ? 12.sp : 6.sp,
                      ),
                      const SmallTextNotoSans(text: ', '),
                    ],
                    if (isNotNullOrEmpty(
                      serviceWrapper?.service?.address?.city,
                    )) ...[
                      SmallTextNotoSans(
                        text: serviceWrapper!.service!.address!.city!,
                        maxLine: 4,
                        fontWeight: FontWeight.w600,
                        size: o == Orientation.portrait ? 12.sp : 6.sp,
                      ),
                    ],
                  ],
                ),
              ],
            ),
            SizedBox(height: 8.h),
            SizedBox(
              height: 20.h,
              width: o == Orientation.portrait
                  ? Get.width * 0.25
                  : Get.width * 0.14,
              child: TextButtonNotoSans(
                fontSize: o == Orientation.portrait ? 12.sp : 6.sp,
                text: 'View Less',
                onPressed: () {
                  grievanceController.isMore.value = false;
                },
                icon: Padding(
                  padding: EdgeInsets.only(left: 4.w),
                  child: Icon(
                    Icons.keyboard_arrow_up,
                    size: o == Orientation.portrait ? 22.sp : 12.sp,
                    color: BaseConfig.appThemeColor1,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
