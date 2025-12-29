import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/controller/inbox_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/grievance/grievance.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart' as timeline;
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/localization/localize_utils.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:mobile_app/widgets/text_button_noto_sans.dart';
import 'package:mobile_app/widgets/timeline_widget.dart/timeline_widget_2.dart';
import 'package:mobile_app/widgets/view_more.dart';
import 'package:mobile_app/widgets/wrap_text.dart';

class EmpGrievanceDetails extends StatefulWidget {
  const EmpGrievanceDetails({super.key});

  @override
  State<EmpGrievanceDetails> createState() => _EmpGrievanceDetailsState();
}

class _EmpGrievanceDetailsState extends State<EmpGrievanceDetails> {
  final _authController = Get.find<AuthController>();
  final _grievanceController = Get.find<GrievanceController>();
  final _timelineController = Get.find<TimelineController>();
  final _inboxController = Get.find<InboxController>();

  final pageController = PageController(viewportFraction: 1.05, keepPage: true);
  final _isLoading = false.obs;
  ServiceWrapper? serviceWrapper;
  List<timeline.Action> actions = [];
  timeline.ProcessInstanceTimeline? services;

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

        await Get.find<CommonController>().fetchLabels(modules: Modules.PGR);

        await _grievanceController.getIndividualGrievance(
          token: _authController.token!.accessToken!,
          tenantId: serviceWrapper!.service!.tenantId!,
          serviceRequestId: serviceWrapper!.service!.serviceRequestId!,
        );
        await _timelineController.getTimelineHistory(
          token: _authController.token!.accessToken!,
          tenantId: serviceWrapper!.service!.tenantId!,
          businessIds: serviceWrapper!.service!.serviceRequestId!,
        );

        services = _timelineController.timeline?.processInstancesList
            ?.where(
              (e) =>
                  e.state?.state ==
                      serviceWrapper?.service?.applicationStatus &&
                  e.state?.applicationStatus ==
                      serviceWrapper?.service?.applicationStatus,
            )
            .toList()
            .first;

        actions.addAll(services!.nextActions!);

        if (showTakeActionButton()) {
          await _getWorkflow();
        }

        _isLoading.value = false;
      }
    } catch (e) {
      dPrint('Error in _getServiceWrapper: $e');
    }
  }

  Future<void> _getWorkflow() async {
    try {
      await _timelineController.getWorkFlow(
        token: _authController.token!.accessToken!,
        tenantId: serviceWrapper!.service!.tenantId!,
        workFlow: services!.businessService!,
      );
    } catch (e) {
      dPrint('Error in getting workflow: $e');
    }
  }

  bool showTakeActionButton() {
    final filteredActions = actions
        .where(
          (e) =>
              e.roles?.contains(InspectorType.PGR_LME_INSPECTOR.name) ?? false,
        )
        .toList();

    return filteredActions.isNotEmpty;
  }

  @override
  Widget build(BuildContext context) {
    final o = MediaQuery.of(context).orientation;
    return Scaffold(
      appBar: HeaderTop(
        title: getLocalizedString(
          i18.grievance.DETAILS_HEADER,
          module: Modules.PGR,
        ),
        onPressed: () => Get.back(),
      ),
      bottomNavigationBar: Obx(
        () => _isLoading.value
            ? const SizedBox.shrink()
            : showTakeActionButton()
                ? Container(
                    height: 44.h,
                    width: Get.width,
                    margin: EdgeInsets.symmetric(
                      horizontal: o == Orientation.portrait ? 12.w : 4.w,
                      vertical: 8.h,
                    ),
                    child: PopupMenuButton(
                      style: FilledButton.styleFrom(
                        backgroundColor: BaseConfig.appThemeColor1,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(
                            o == Orientation.portrait ? 12.w : 6.w,
                          ),
                        ),
                      ),
                      icon: MediumSelectableTextNotoSans(
                        text: getLocalizedString(i18.common.TAKE_ACTION),
                        size: o == Orientation.portrait ? 14.sp : 8.sp,
                        color: BaseConfig.mainBackgroundColor,
                        fontWeight: FontWeight.w600,
                      ),
                      itemBuilder: (context) => actions
                          .map(
                            (action) => PopupMenuItem<String>(
                              value: action.action,
                              child: SmallSelectableTextNotoSans(
                                text: LocalizeUtils.getTakeActionLocal(
                                  action.action,
                                  workflowCode: services!.businessService!,
                                  module: Modules.PGR,
                                  isCitizen: true,
                                ),
                                color: BaseConfig.textColor,
                                fontWeight: FontWeight.w600,
                                size: o == Orientation.portrait ? 14.sp : 8.sp,
                              ),
                            ),
                          )
                          .toList(),
                      onSelected: (value) async {
                        dPrint(value);

                        String uuid = _timelineController
                                .workflowBusinessServices
                                .businessServices
                                ?.first
                                .states
                                ?.where((s) => s.uuid == services!.state?.uuid)
                                .first
                                .actions
                                ?.where((a) => a.action == value)
                                .first
                                .nextState ??
                            '';

                        dPrint('UUID: $uuid');

                        if (uuid.isEmpty) {
                          snackBar(
                            'InComplete',
                            'Next State is Empty',
                            Colors.green,
                          );
                          return;
                        }

                        if (value == BaseAction.reassign.name) {
                          await _timelineController.getEmployees(
                            token: _authController.token!.accessToken!,
                            tenantId: serviceWrapper!.service!.tenantId!,
                            uuid: uuid,
                          );
                        }

                        if (!context.mounted) return;

                        _inboxController.actionDialogue(
                          context,
                          workFlowId: _timelineController.timeline!
                              .processInstancesList!.first.businessService!,
                          action: value,
                          module: Modules.PGR,
                          sectionType: ModulesEmp.PGR_SERVICES,
                          tenantId: serviceWrapper!.service!.tenantId!,
                          businessService: BusinessService.PGR,
                        );
                      },
                    ),
                  )
                : const SizedBox.shrink(),
      ),
      body: Container(
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
                                  size:
                                      o == Orientation.portrait ? 14.sp : 8.sp,
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
                                serviceWrapper?.service?.applicationStatus ??
                                    '',
                              ),
                              child: Tooltip(
                                message: getLocalizedString(
                                  '${i18.common.CS_COMMON}${serviceWrapper?.service?.applicationStatus}',
                                  module: Modules.PGR,
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
                                        serviceWrapper
                                                ?.service?.applicationStatus ??
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
                          ),
                        ],
                      ),
                      SizedBox(height: 16.h),
                      _DetailsBody(
                        o: o,
                        serviceWrapper: serviceWrapper,
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
                          status: serviceWrapper!.service!.applicationStatus!,
                        ),
                      ],
                    ],
                  ),
                ),
        ),
      ),
    );
  }
}

class _DetailsBody extends StatelessWidget {
  const _DetailsBody({
    this.o = Orientation.portrait,
    this.serviceWrapper,
  });
  final ServiceWrapper? serviceWrapper;
  final Orientation o;

  @override
  Widget build(BuildContext context) {
    final grievanceController = Get.find<GrievanceController>();
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
              text: getLocalizedString(
                '${i18.common.SERVICE_DEFS}${grievanceController.getDepartment(serviceWrapper?.service?.serviceCode ?? '')?.menuPath ?? ''}'
                    .toUpperCase(),
                module: Modules.PGR,
              ),
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
              text: getLocalizedString(
                '${i18.common.SERVICE_DEFS}${grievanceController.getDepartment(serviceWrapper?.service?.serviceCode ?? '')?.menuPath ?? ''}'
                    .toUpperCase(),
                module: Modules.PGR,
              ),
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
                SmallSelectableTextNotoSans(
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
                      SmallSelectableTextNotoSans(
                        text: serviceWrapper!.service!.address!.landmark!,
                        maxLine: 4,
                        fontWeight: FontWeight.w600,
                        size: o == Orientation.portrait ? 12.sp : 6.sp,
                      ),
                      const SmallSelectableTextNotoSans(text: ', '),
                    ],
                    if (isNotNullOrEmpty(
                      serviceWrapper?.service?.address?.locality?.code,
                    )) ...[
                      SmallSelectableTextNotoSans(
                        text: getLocalizedString(
                          serviceWrapper!.service!.address!.locality!.code,
                        ),
                        maxLine: 4,
                        fontWeight: FontWeight.w600,
                        size: o == Orientation.portrait ? 12.sp : 6.sp,
                      ),
                      const SmallSelectableTextNotoSans(text: ', '),
                    ],
                    if (isNotNullOrEmpty(
                      serviceWrapper?.service?.address?.city,
                    )) ...[
                      SmallSelectableTextNotoSans(
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
