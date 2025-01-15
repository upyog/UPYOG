import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/filled_button_app.dart';
import 'package:mobile_app/components/network_img.dart';
import 'package:mobile_app/components/rating_bar_widget.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/grievance_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart' as timeline;
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:photo_view/photo_view.dart';
import 'package:timelines_plus/timelines_plus.dart' as tl;

class TimelineWidget2 extends StatefulWidget {
  final String tenantId, businessIds, status;
  final Modules modules;
  final String? workflowId;
  const TimelineWidget2({
    super.key,
    required this.tenantId,
    required this.businessIds,
    required this.status,
    this.modules = Modules.COMMON,
    this.workflowId,
  });

  @override
  State<TimelineWidget2> createState() => _TimelineWidget2State();
}

class _TimelineWidget2State extends State<TimelineWidget2> {
  final _authController = Get.find<AuthController>();
  final _timelineHistoryController = Get.find<TimelineController>();
  final _isLoading = false.obs;
  bool _firstExpansion = false;

  @override
  void initState() {
    super.initState();
    _timelineHistoryController.countApiCall = 0;
  }

  Future<void> timelineApiCall() async {
    _isLoading.value = true;
    _timelineHistoryController.countApiCall += 1;
    if (_timelineHistoryController.countApiCall == 1) {
      _firstExpansion = true;
      await _getTimeline();
    }
    _isLoading.value = false;
  }

  Future<void> _getTimeline() async {
    await _timelineHistoryController.getTimelineHistory(
      token: _authController.token!.accessToken!,
      tenantId: widget.tenantId,
      businessIds: widget.businessIds,
    );
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, o) {
        return Obx(
          () => _isLoading.value
              ? SizedBox(
                  height: Get.height / 2,
                  child: showCircularIndicator(),
                )
              : ExpansionTile(
                  initiallyExpanded: _firstExpansion,
                  onExpansionChanged: (value) async {
                    await timelineApiCall();
                  },
                  shape: const Border(),
                  title: MediumTextNotoSans(
                    text: 'Complaint Timeline',
                    fontWeight: FontWeight.w600,
                    size: o == Orientation.portrait ? 14.sp : 7.sp,
                  ),
                  children: [
                    ComplaintTimeline(
                      o: o,
                      modules: widget.modules,
                      status: widget.status,
                      tenantId: widget.tenantId,
                      workflowId: widget.workflowId,
                    ),
                  ],
                ),
        );
      },
    );
  }
}

class ComplaintTimeline extends StatelessWidget {
  final Orientation o;
  final Modules modules;
  final String status, tenantId;
  final String? workflowId;
  const ComplaintTimeline({
    super.key,
    this.o = Orientation.portrait,
    required this.modules,
    required this.status,
    required this.tenantId,
    this.workflowId,
  });

  @override
  Widget build(BuildContext context) {
    final timelineHistoryController = Get.find<TimelineController>();

    final timelineData =
        timelineHistoryController.timeline?.processInstancesList;

    return SizedBox(
      height: o == Orientation.portrait ? Get.height * 0.38 : 200.h,
      width: Get.width,
      child: TimelineScreen(
        o: o,
        timelineData: timelineData,
        tenantId: tenantId,
        modules: modules,
        status: status,
        workflowId: workflowId,
      ),
    );
  }
}

class TimelineScreen extends StatelessWidget {
  const TimelineScreen({
    super.key,
    this.o = Orientation.portrait,
    required this.timelineData,
    required this.tenantId,
    required this.modules,
    required this.status,
    this.workflowId,
  });

  final Orientation o;
  final List<timeline.ProcessInstanceTimeline>? timelineData;
  final String tenantId;
  final Modules modules;
  final String status;
  final String? workflowId;

  @override
  Widget build(BuildContext context) {
    final List<timeline.ProcessInstanceTimeline> timelineDataReverse =
        List.from(
      timelineData?.reversed ?? <timeline.ProcessInstanceTimeline>[],
    );

    final isPGR = timelineDataReverse.first.businessService == 'PGR';

    final dashConnector = tl.DashedLineConnector(
      color: BaseConfig.timelineDashColor,
      thickness: 2.w,
      gap: 6.h,
    );

    return Column(
      children: [
        Expanded(
          child: SingleChildScrollView(
            scrollDirection: Axis.vertical,
            primary: true,
            physics: const AlwaysScrollableScrollPhysics(),
            child: Stack(
              children: [
                tl.FixedTimeline.tileBuilder(
                  theme: tl.TimelineThemeData(
                    indicatorPosition: 0,
                    indicatorTheme: tl.IndicatorThemeData(
                      size: o == Orientation.portrait ? 28.w : 14.w,
                    ),
                  ),
                  builder: tl.TimelineTileBuilder.connected(
                    connectionDirection: tl.ConnectionDirection.before,
                    itemCount: isPGR
                        ? timelineDataReverse.length + 1
                        : timelineDataReverse.length,
                    contentsBuilder: (context, index) {
                      if (index.isEven) {
                        final i = isPGR ? index ~/ 2 : index;
                        final timeDataEven = timelineDataReverse[i];
                        dPrint('timeData: ${timeDataEven.businessService}');

                        //Only for PGR
                        if (index == 0 && isPGR) {
                          final pgrServiceWrapper =
                              Get.find<GrievanceController>()
                                  .timelineServiceWrapper;
                          return Padding(
                            padding: EdgeInsets.only(
                              left: 16.w,
                              bottom: (i == timelineDataReverse.length - 1)
                                  ? 60.h
                                  : 40.h,
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                SmallTextNotoSans(
                                  text: getLocalizedString(
                                    i18.common.COMPLAINT_FILED,
                                    module: Modules.PT,
                                  ),
                                  fontWeight: FontWeight.w600,
                                  size:
                                      o == Orientation.portrait ? 12.sp : 7.sp,
                                  textAlign: index.isOdd
                                      ? TextAlign.end
                                      : TextAlign.start,
                                ),
                                SmallTextNotoSans(
                                  text: isNotNullOrEmpty(
                                    pgrServiceWrapper
                                        .service?.auditDetails?.createdTime,
                                  )
                                      ? pgrServiceWrapper
                                          .service!.auditDetails!.createdTime
                                          .toCustomDateFormat(
                                          pattern: 'd/MM/yyyy',
                                        )!
                                      : getLocalizedString(i18.common.NA),
                                  fontWeight: FontWeight.w400,
                                  size:
                                      o == Orientation.portrait ? 10.sp : 5.sp,
                                  textAlign: index.isOdd
                                      ? TextAlign.end
                                      : TextAlign.start,
                                ),
                                SmallTextNotoSans(
                                  text: isNotNullOrEmpty(
                                    pgrServiceWrapper.service?.citizen?.name,
                                  )
                                      ? pgrServiceWrapper
                                          .service!.citizen!.name!
                                      : getLocalizedString(i18.common.NA),
                                  fontWeight: FontWeight.w400,
                                  size:
                                      o == Orientation.portrait ? 10.sp : 5.sp,
                                  textAlign: index.isOdd
                                      ? TextAlign.end
                                      : TextAlign.start,
                                ),
                                SmallTextNotoSans(
                                  text: isNotNullOrEmpty(
                                    pgrServiceWrapper
                                        .service?.citizen?.mobileNumber,
                                  )
                                      ? pgrServiceWrapper
                                          .service!.citizen!.mobileNumber!
                                      : getLocalizedString(i18.common.NA),
                                  fontWeight: FontWeight.w400,
                                  size:
                                      o == Orientation.portrait ? 10.sp : 5.sp,
                                  textAlign: index.isOdd
                                      ? TextAlign.end
                                      : TextAlign.start,
                                ),
                                SmallTextNotoSans(
                                  text: isNotNullOrEmpty(
                                    pgrServiceWrapper.service?.source,
                                  )
                                      ? getLocalizedString(
                                          '${i18.common.FILED_VIA}${pgrServiceWrapper.service!.source!}'
                                              .toUpperCase(),
                                          module: Modules.PT,
                                        )
                                      : getLocalizedString(i18.common.NA),
                                  fontWeight: FontWeight.w400,
                                  size:
                                      o == Orientation.portrait ? 10.sp : 5.sp,
                                  textAlign: index.isOdd
                                      ? TextAlign.end
                                      : TextAlign.start,
                                ),
                              ],
                            ),
                          );
                        }

                        return Padding(
                          padding: EdgeInsets.only(
                            left: 16.w,
                            bottom: (i == timelineDataReverse.length - 1)
                                ? 60.h
                                : 40.h,
                          ),
                          child: TimelineItem(
                            timeData: timeDataEven,
                            index: i,
                            tenantId: tenantId,
                            o: o,
                            modules: modules,
                            workflowId: workflowId,
                          ),
                        );
                      }
                      return null;
                    },
                    oppositeContentsBuilder: (context, index) {
                      final i = isPGR ? index ~/ 2 : index;
                      if (index.isOdd) {
                        final timeDataOdd = timelineDataReverse[i];
                        return Padding(
                          padding: EdgeInsets.only(
                            right: 16.w,
                            bottom: (i == timelineDataReverse.length - 1)
                                ? 60.h
                                : 40.h,
                          ),
                          child: TimelineItem(
                            timeData: timeDataOdd,
                            index: i,
                            tenantId: tenantId,
                            o: o,
                            modules: modules,
                            workflowId: workflowId,
                          ),
                        );
                      }
                      return null;
                    },
                    indicatorBuilder: (context, index) {
                      return const tl.DotIndicator(
                        color: BaseConfig.timelineCircleColor,
                      );
                    },
                    connectorBuilder: (context, index, connectorType) =>
                        dashConnector,
                    firstConnectorBuilder: (context) => dashConnector,
                    lastConnectorBuilder: (context) => dashConnector,
                  ),
                ),
                Positioned(
                  bottom: 0,
                  left: Get.width * 0.35,
                  right: Get.width * 0.35,
                  child: Container(
                    padding: EdgeInsets.only(bottom: 4.h),
                    color: BaseConfig.mainBackgroundColor,
                    child: Container(
                      height: 20.h,
                      decoration: BoxDecoration(
                        color: getGrievanceStatusTextColor(status),
                        borderRadius: BorderRadius.circular(12.w),
                      ),
                      child: Center(
                        child: SmallTextNotoSans(
                          text: closedStatusList.contains(status)
                              ? 'Closed'
                              : resolvedStatusList.contains(status)
                                  ? 'Resolved'
                                  : rejectedStatusList.contains(status)
                                      ? 'Rejected'
                                      : 'Open',
                          color: BaseConfig.mainBackgroundColor,
                          size: o == Orientation.portrait ? 12.sp : 6.sp,
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class TimelineItem extends StatelessWidget {
  final timeline.ProcessInstanceTimeline? timeData;
  final int index;
  final String tenantId;
  final Orientation o;
  final Modules modules;
  final String? workflowId;

  const TimelineItem({
    super.key,
    this.timeData,
    required this.index,
    required this.tenantId,
    this.o = Orientation.portrait,
    required this.modules,
    this.workflowId,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment:
          index.isOdd ? CrossAxisAlignment.end : CrossAxisAlignment.start,
      children: [
        if (timeData?.state?.state != null)
          SmallTextNotoSans(
            text: getLocalizedString(
              timeData!.state!.state ==
                      TimelineStateType.CLOSEDAFTERRESOLUTION.name
                  ? workflowId != null
                      ? 'WF_${workflowId}_${timeData!.state!.state}'
                          .toUpperCase()
                      : '${i18.common.CS_COMMON}${i18.common.CS_COMMON}${timeData!.state!.state}'
                  : workflowId != null
                      ? 'WF_${workflowId}_${timeData!.state!.state}'
                          .toUpperCase()
                      : '${i18.common.CS_COMMON}${timeData!.state!.state}'
                          .toUpperCase(),
              module: timeData!.state!.state ==
                      TimelineStateType.CLOSEDAFTERRESOLUTION.name
                  ? Modules.COMMON
                  : modules,
            ),
            fontWeight: FontWeight.w600,
            size: o == Orientation.portrait ? 12.sp : 7.sp,
            textAlign: index.isOdd ? TextAlign.end : TextAlign.start,
          ),
        if (timeData?.assignes != null &&
            timeData!.state!.state == TimelineStateType.PENDINGATLME.name)
          Column(
            children: timeData!.assignes!
                .map(
                  (e) => IconButton(
                    onPressed: () async {
                      await launchPhoneDialer(e.mobileNumber!);
                    },
                    icon: Wrap(
                      children: [
                        Icon(
                          Icons.phone,
                          color: BaseConfig.textColor,
                          size: o == Orientation.portrait ? 12.sp : 5.sp,
                        ),
                        SizedBox(width: 5.w),
                        SmallTextNotoSans(
                          text: e.mobileNumber!,
                          fontWeight: FontWeight.w400,
                          size: o == Orientation.portrait ? 10.sp : 5.sp,
                        ),
                      ],
                    ),
                  ),
                )
                .toList(),
          ),
        if (timeData?.comment != null) ...[
          Wrap(
            alignment: index.isOdd ? WrapAlignment.end : WrapAlignment.start,
            children: [
              SmallTextNotoSans(
                text: '${getLocalizedString(i18.common.COMMENTS)}: ',
                fontWeight: FontWeight.w400,
                size: o == Orientation.portrait ? 10.sp : 5.sp,
                textAlign: index.isOdd ? TextAlign.end : TextAlign.start,
              ),
              SmallTextNotoSans(
                text: timeData!.comment!,
                fontWeight: FontWeight.w400,
                size: o == Orientation.portrait ? 10.sp : 5.sp,
                textAlign: index.isOdd ? TextAlign.end : TextAlign.start,
              ),
            ],
          ),
        ],
        if (timeData?.documents != null) ...[
          for (var file in timeData!.documents!)
            TimelineDocumentPhoto(
              documentTimeLine: file,
              tenantId: tenantId,
              o: o,
            ),
        ],
        if (timeData?.assigner?.name != null) ...[
          const SizedBox(height: 4.0),
          Tooltip(
            message:
                '${timeData!.assigner!.name!}-${timeData!.assigner!.mobileNumber!}'
                    .capitalize,
            child: Wrap(
              alignment: index.isOdd ? WrapAlignment.end : WrapAlignment.start,
              children: [
                SmallTextNotoSans(
                  text: timeData!.assigner!.name!.capitalize!,
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 10.sp : 5.sp,
                  textAlign: index.isOdd ? TextAlign.end : TextAlign.start,
                ),
                SmallTextNotoSans(
                  text: '-${timeData!.assigner!.mobileNumber!}',
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 10.sp : 5.sp,
                  textAlign: index.isOdd ? TextAlign.end : TextAlign.start,
                ).ripple(() async {
                  if (isNotNullOrEmpty(timeData?.assigner?.mobileNumber)) {
                    await launchPhoneDialer(timeData!.assigner!.mobileNumber!);
                  }
                }),
              ],
            ),
          ),
        ],
        if (timeData?.state?.state != null) ...[
          if (timeData!.state!.state ==
              TimelineStateType.CLOSEDAFTERRESOLUTION.name) ...[
            SizedBox(height: 4.h),
            Wrap(
              direction: Axis.horizontal,
              alignment: index.isOdd ? WrapAlignment.end : WrapAlignment.start,
              children: [
                SmallTextNotoSans(
                  text:
                      '${getLocalizedString(i18.common.YOU_RATED, module: modules)}:',
                  fontWeight: FontWeight.w400,
                  size: o == Orientation.portrait ? 10.sp : 5.sp,
                ),
                RatingBarApp(
                  rating: timeData!.rating ??= 0,
                  size: o == Orientation.portrait ? 14.sp : 6.sp,
                  onRatingUpdate: (rating) {
                    dPrint('Rating: $rating');
                  },
                ),
              ],
            ),
          ],
        ],
        if (timeData?.auditDetails?.createdTime != null) ...[
          const SizedBox(height: 4.0),
          SmallTextNotoSans(
            text: timeData!.auditDetails!.createdTime!.toCustomDateFormat()!,
            fontWeight: FontWeight.w400,
            size: o == Orientation.portrait ? 10.sp : 5.sp,
            color: BaseConfig.timelineGreyTextColor,
          ),
        ],
      ],
    );
  }
}

class TimelineDocumentPhoto extends StatelessWidget {
  final timeline.DocumentTimeLine documentTimeLine;
  final String tenantId;
  final Orientation o;
  const TimelineDocumentPhoto({
    super.key,
    required this.documentTimeLine,
    required this.tenantId,
    required this.o,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: Get.width,
      margin: EdgeInsets.only(top: 4.h),
      decoration: BoxDecoration(
        color: BaseConfig.mainBackgroundColor,
        borderRadius: BorderRadius.circular(10.r),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SmallTextNotoSans(
            text: '${getLocalizedString(i18.common.DOCUMENTS)}:',
            fontWeight: FontWeight.w400,
            size: o == Orientation.portrait ? 10.sp : 5.sp,
          ),
          SizedBox(height: 5.h),
          FutureBuilder(
            future: Get.find<FileController>().getFiles(
              token: Get.find<AuthController>().token!.accessToken!,
              tenantId: tenantId,
              fileStoreIds: documentTimeLine.fileStoreId!,
            ),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return showCircularIndicator();
              }
              if (!snapshot.hasError && snapshot.hasData) {
                return SizedBox(
                  height:
                      snapshot.data!.fileStoreIds!.length > 4 ? 100.h : 50.h,
                  child: GridView.builder(
                    shrinkWrap: true,
                    itemCount: snapshot.data?.fileStoreIds?.length,
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 4,
                    ),
                    physics: const AlwaysScrollableScrollPhysics(),
                    itemBuilder: (context, index) {
                      final commentImg = snapshot
                          .data?.fileStoreIds?[index].url!
                          .split(',')
                          .first;

                      // dPrint(commentImg.toString());
                      return ClipRRect(
                        borderRadius: BorderRadius.circular(10.r),
                        child: NetworkImg(
                          imgUrl: commentImg ?? '',
                        ).ripple(() {
                          if (commentImg == null) return;
                          showAdaptiveDialog(
                            context: context,
                            builder: (context) => AlertDialog(
                              backgroundColor: BaseConfig.mainBackgroundColor,
                              surfaceTintColor: BaseConfig.mainBackgroundColor,
                              scrollable: false,
                              content: SizedBox(
                                width: Get.width,
                                height: Get.height * 0.5,
                                child: Column(
                                  children: [
                                    Row(
                                      mainAxisAlignment: MainAxisAlignment.end,
                                      children: [
                                        IconButton(
                                          onPressed: () => Get.back(),
                                          icon: const Icon(
                                            Icons.cancel_presentation_outlined,
                                            size: 30,
                                            color: BaseConfig.appThemeColor1,
                                          ),
                                        ),
                                      ],
                                    ),
                                    Expanded(
                                      child: PhotoView(
                                        backgroundDecoration: BoxDecoration(
                                          color: BaseConfig.mainBackgroundColor,
                                          borderRadius:
                                              BorderRadius.circular(10.r),
                                        ),
                                        imageProvider: NetworkImage(
                                          commentImg,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          );
                        }),
                      );
                    },
                  ),
                );
              } else {
                return Center(
                  child: SmallTextNotoSans(
                    text: snapshot.error.toString(),
                    color: BaseConfig.redColor1,
                  ),
                );
              }
            },
          ),
        ],
      ),
    );
  }
}

showRatingDialogue({
  Function()? onPressed,
  Orientation o = Orientation.portrait,
}) {
  Get.dialog(
    barrierDismissible: false,
    Dialog(
      backgroundColor: BaseConfig.mainBackgroundColor,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12.w),
      ),
      insetPadding: EdgeInsets.all(o == Orientation.portrait ? 16.w : 8.w),
      child: Padding(
        padding: EdgeInsets.all(o == Orientation.portrait ? 16.w : 8.w),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Padding(
              padding: EdgeInsets.symmetric(
                vertical: 4.h,
                horizontal: o == Orientation.portrait ? 16.w : 8.w,
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  CircleAvatar(
                    radius: 30.r,
                    backgroundColor: BaseConfig.ratingBackColor,
                    child: Icon(
                      Icons.star_border_outlined,
                      color: BaseConfig.appThemeColor1,
                      size: o == Orientation.portrait ? 30.w : 15.w,
                    ),
                  ),
                  SizedBox(height: o == Orientation.portrait ? 24.h : 12.h),
                  MediumTextNotoSans(
                    text: 'Rate us!',
                    size: o == Orientation.portrait ? 16.sp : 8.sp,
                    fontWeight: FontWeight.w700,
                  ),
                  SizedBox(height: o == Orientation.portrait ? 14.h : 7.h),
                  MediumTextNotoSans(
                    text:
                        'Please rate your experience with us to help resolve your queries faster',
                    fontWeight: FontWeight.w400,
                    textAlign: TextAlign.center,
                    size: o == Orientation.portrait ? 12.sp : 7.sp,
                  ),
                  SizedBox(height: o == Orientation.portrait ? 16.h : 8.h),
                  RatingBarApp(
                    rating: 3,
                    size: o == Orientation.portrait ? 40.w : 20.w,
                    onRatingUpdate: (rating) {
                      dPrint('Rating: $rating');
                    },
                  ),
                ],
              ),
            ),
            SizedBox(height: o == Orientation.portrait ? 24.h : 12.h),
            const Divider(
              color: BaseConfig.borderColor,
            ),
            SizedBox(height: o == Orientation.portrait ? 16.h : 8.h),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: FilledButtonApp(
                    text: 'Later',
                    textColor: BaseConfig.appThemeColor1,
                    onPressed: () => Get.back(),
                    backgroundColor: BaseConfig.mainBackgroundColor,
                    fontSize: o == Orientation.portrait ? 14.sp : 7.sp,
                    fontWeight: FontWeight.w600,
                    side: const BorderSide(
                      color: BaseConfig.appThemeColor1,
                      width: 1.5,
                    ),
                  ),
                ),
                SizedBox(width: 16.w),
                Expanded(
                  child: FilledButtonApp(
                    text: 'Done',
                    fontSize: o == Orientation.portrait ? 18.sp : 9.sp,
                    onPressed: onPressed,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    ),
  );
}
