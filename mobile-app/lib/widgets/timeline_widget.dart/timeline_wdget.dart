import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/network_img.dart';
import 'package:mobile_app/components/rating_bar_widget.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/file_controller.dart';
import 'package:mobile_app/controller/timeline_controller.dart';
import 'package:mobile_app/model/citizen/timeline/timeline.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/app_enums.dart';
import 'package:mobile_app/utils/enums/emp_enums.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';
import 'package:photo_view/photo_view.dart';
import 'package:timelines_plus/timelines_plus.dart' as tl;

class TimelineHistoryApp {
  static final _timelineController = Get.find<TimelineController>();

  /// Get the timeline history dialogue
  static dynamic buildTimelineDialogue(
    BuildContext context, {
    required String tenantId,
  }) {
    return showCustomDialogue(
      context,
      child: StreamBuilder<Timeline?>(
        stream: _timelineController.timelineStreamCtrl.stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    SizedBox(height: 10.w),
                    Expanded(
                      child: MediumSelectableTextNotoSans(
                        text: getLocalizedString(
                          i18.tradeLicense.APPLICATION_TIMELINE,
                        ),
                        size: 16.sp,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    IconButton(
                      icon: const Icon(
                        Icons.cancel_presentation_outlined,
                        color: BaseConfig.appThemeColor1,
                      ),
                      onPressed: () => Get.back(),
                    ),
                  ],
                ),
                _buildTimeLineHistory(
                  context,
                  tenantId: tenantId,
                  timeline: snapshot.data,
                ),
              ],
            );
          } else if (snapshot.hasError) {
            return const SizedBox.shrink();
          } else {
            switch (snapshot.connectionState) {
              case ConnectionState.waiting:
                return showCircularIndicator();
              case ConnectionState.active:
                return showCircularIndicator();
              default:
                return const SizedBox.shrink();
            }
          }
        },
      ),
    );
  }

  static Widget _buildTimeLineHistory(
    BuildContext context, {
    required String tenantId,
    Timeline? timeline,
  }) {
    // const kTileHeight = 100.0;

    final timelineData = timeline?.processInstancesList;
    timelineData?.sort(
      (a, b) =>
          DateTime.fromMillisecondsSinceEpoch(b.auditDetails!.createdTime!)
              .compareTo(
        DateTime.fromMillisecondsSinceEpoch(
          a.auditDetails!.createdTime!,
        ),
      ),
    );
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // const SizedBox(height: 10),

        !isNotNullOrEmpty(timelineData)
            ? const Center(
                child: MediumSelectableTextNotoSans(text: 'No data found!'),
              ).marginOnly(top: 50.h)
            : SizedBox(
                height: MediaQuery.sizeOf(context).height * 0.6,
                child: SingleChildScrollView(
                  physics: const AlwaysScrollableScrollPhysics(),
                  child: tl.Timeline.tileBuilder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    theme: tl.TimelineThemeData(
                      nodePosition: 0,
                      connectorTheme: const tl.ConnectorThemeData(
                        thickness: 3.0,
                        color: BaseConfig.greyColor2,
                      ),
                      indicatorTheme: const tl.IndicatorThemeData(
                        size: 15.0,
                      ),
                    ),
                    builder: tl.TimelineTileBuilder.connected(
                      contentsBuilder: (context, index) {
                        final timeData = timelineData[index];
                        dPrint(timeData.businessService.toString());

                        return buildStatusCard(
                          timeData: timeData,
                          tenantId: tenantId,
                          businessService: timeData.businessService!,
                          index: index,
                          timeline: timelineData,
                        );
                      },
                      connectorBuilder: (_, index, __) {
                        return const tl.SolidLineConnector();
                      },
                      indicatorBuilder: (_, index) {
                        if (index == 0) {
                          return const tl.DotIndicator(
                            color: BaseConfig.appThemeColor1,
                          );
                        } else {
                          return const tl.OutlinedDotIndicator(
                            color: BaseConfig.greyColor1,
                            borderWidth: 2.0,
                            backgroundColor: BaseConfig.greyColor1,
                          );
                        }
                      },
                      // itemExtentBuilder: (_, __) => kTileHeight,
                      itemCount: timelineData!.length,
                    ),
                  ),
                ),
              ),
      ],
    );
  }

  static Widget buildStatusCard({
    ProcessInstanceTimeline? timeData,
    required String tenantId,
    required String businessService,
    required int index,
    required List<ProcessInstanceTimeline>? timeline,
  }) {
    final authController = Get.find<AuthController>();

    String timelineStatusPostFix = '';

    final isBpa = (businessService == BusinessServicesEmp.BPA.name ||
        businessService == BusinessServicesEmp.BPA_LOW.name ||
        businessService == BusinessServicesEmp.BPA_OC.name);

    if (isBpa) {
      if (index != 0 &&
          (timeline?[index - 1].state?.state?.contains('BACK_FROM') ??
              false ||
                  timeline![index - 1]
                      .state!
                      .state!
                      .contains('SEND_TO_CITIZEN'))) {
        timelineStatusPostFix = '_NOT_DONE`';
        timeData = timeline![index - 1];
      } else if (timeData?.state?.state == 'SEND_TO_ARCHITECT') {
        timelineStatusPostFix = '_BY_ARCHITECT_DONE';
        timeData = timeline![index];
      } else {
        timelineStatusPostFix = index == 0 ? '' : '_DONE';
        timeData = timeline![index];
      }
    }

    final isEmp = authController.userType?.value == UserType.EMPLOYEE.name;

    dPrint(isEmp.toString());
    dPrint(timeData!.moduleName);
    dPrint("----------------------------");
    dPrint(timeData.state!.state);

    return Padding(
      padding: const EdgeInsets.all(10.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          if (timeData.state?.state != null)
            MediumSelectableTextNotoSans(
              text: timeData.moduleName == ModulesEmp.SW_SERVICES.name
                  ? getLocalizedString(
                      'CS_${timeData.state!.state}'.toUpperCase(),
                    )
                  : getLocalizedString(
                      timeData.moduleName == 'fsm'
                          ? 'CS_COMMON_${timeData.state!.state}'
                          : isBpa
                              ? 'WF_${timeData.businessService}_${timeData.state!.state}$timelineStatusPostFix'
                              : (isEmp && timeData.moduleName == 'PT')
                                  ? 'ES_${timeData.moduleName}_COMMON_STATUS_${timeData.state?.state}'
                                  : 'WF_${timeData.businessService}_${timeData.state!.state}'
                                      .toUpperCase(),
                      module: isBpa && index != 0
                          ? Modules.BPA
                          : (isEmp && timeData.moduleName == 'PT')
                              ? Modules.PT
                              : timeData.moduleName == 'fsm'
                                  ? Modules.FSM
                                  : Modules.COMMON,
                    ),
            ),
          SmallSelectableTextNotoSans(
            text: timeData.auditDetails?.createdTime
                    .toCustomDateFormat(pattern: 'dd/MM/yyyy') ??
                '',
            color: BaseConfig.textColor,
          ),
          if (timeData.assignes != null
              // && timeData!.state!.state == TimelineStateType.PENDINGATLME.name
              )
            ...timeData.assignes!.map(
              (assign) => Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SmallSelectableTextNotoSans(
                    text: assign.name ?? '',
                    color: BaseConfig.textColor,
                  ),
                  Wrap(
                    children: [
                      Icon(
                        Icons.phone,
                        color: BaseConfig.textColor,
                        size: 12.sp,
                      ),
                      SizedBox(width: 5.w),
                      SmallSelectableTextNotoSans(
                        text: assign.mobileNumber ?? '',
                        fontWeight: FontWeight.w400,
                        size: 10.sp,
                      ),
                    ],
                  ).ripple(() async {
                    if (!isNotNullOrEmpty(assign.mobileNumber)) return;
                    await launchPhoneDialer(
                      assign.mobileNumber!,
                    );
                  }),
                ],
              ),
            ),
          if (timeData.comment != null) _buildComment(timeData),
          if (timeData.documents != null) ...[
            for (var file in timeData.documents!)
              _buildCommentPhotos(file, tenantId: tenantId),
          ],
          if (timeData.state?.state != null) ...[
            if (timeData.state!.state ==
                TimelineStateType.CLOSEDAFTERRESOLUTION.name)
              Wrap(
                direction: Axis.horizontal,
                children: [
                  const SmallText(text: 'You Rated'),
                  RatingBarApp(
                    rating: timeData.rating ?? 0,
                    onRatingUpdate: (rating) {
                      dPrint('Rating: $rating');
                    },
                  ),
                ],
              ),
          ],
          const SizedBox(height: 10),
          const Divider(),
        ],
      ),
    );
  }

  static Widget _buildComment(timeData) {
    return Container(
      width: Get.width,
      margin: const EdgeInsets.symmetric(vertical: 5),
      decoration: BoxDecoration(
        color: BaseConfig.dotGrayColor.withAlpha(50),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            MediumSelectableTextNotoSans(
              text: '${getLocalizedString(i18.common.COMMENTS)}:',
            ),
            SmallSelectableTextNotoSans(
              text: timeData!.comment!,
              color: BaseConfig.textColor,
            ),
          ],
        ),
      ),
    );
  }

  static Widget _buildCommentPhotos(
    DocumentTimeLine documentTimeLine, {
    required String tenantId,
  }) {
    return Container(
      width: Get.width,
      height: 100,
      margin: const EdgeInsets.symmetric(vertical: 5),
      decoration: BoxDecoration(
        color: BaseConfig.mainBackgroundColor,
        borderRadius: BorderRadius.circular(10),
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const MediumSelectableTextNotoSans(text: 'Documents'),
            const SizedBox(height: 5),
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
                  return GridView.builder(
                    shrinkWrap: true,
                    itemCount: snapshot.data?.fileStoreIds?.length,
                    gridDelegate:
                        const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 4,
                    ),
                    itemBuilder: (context, index) {
                      final commentImg = snapshot
                          .data?.fileStoreIds?[index].url!
                          .split(',')
                          .first;

                      // dPrint(commentImg.toString());
                      return ClipRRect(
                        borderRadius: BorderRadius.circular(10),
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
                  );
                } else {
                  return Center(
                    child: SmallText(
                      text: snapshot.error.toString(),
                      color: BaseConfig.redColor1,
                    ),
                  );
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}
