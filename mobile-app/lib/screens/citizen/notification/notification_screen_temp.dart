import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/notification_controller.dart';
import 'package:mobile_app/model/citizen/notification/notification_model.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/extension/extension.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/header_widgets.dart';
import 'package:mobile_app/widgets/medium_text.dart';
import 'package:mobile_app/widgets/small_text.dart';

class NotificationScreenTemp extends StatefulWidget {
  const NotificationScreenTemp({super.key});

  @override
  State<NotificationScreenTemp> createState() => _NotificationScreenTempState();
}

class _NotificationScreenTempState extends State<NotificationScreenTemp> {
  final _notificationController = Get.find<NotificationController>();
  final _authController = Get.find<AuthController>();

  @override
  void initState() {
    _updateNotifications();
    _notificationController.setDefaultLimit();
    super.initState();
  }

  Future<void> _updateNotifications() async {
    await _notificationController.getNotificationUpdate(
      token: _authController.token!.accessToken!,
    );
  }

  Future<NotificationList?> _fetchMyNotifications() async {
    return _notificationController.getNotificationApplication(
      token: _authController.token!.accessToken!,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        //orientation: o,
        titleWidget: Wrap(
          children: [
            Text(getLocalizedString(i18.common.NOTIFICATION)),
            Obx(
              () => Text(' (${_notificationController.length})'),
            ),
          ],
        ),
        onPressed: () {
          Navigator.of(context).pop();
        },
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: FutureBuilder(
              future: _fetchMyNotifications(),
              builder: (context, AsyncSnapshot snapshot) {
                if (snapshot.hasData) {
                  if (snapshot.data is String || snapshot.data == null) {
                    return Center(
                      child: Text(
                        getLocalizedString(i18.inbox.NO_APPLICATION),
                      ),
                    );
                  }
                  final notifications = snapshot.data as NotificationList;
                  if (notifications.events!.isNotEmpty) {
                    return ListView.builder(
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: notifications.events!.length >= 10
                          ? notifications.events!.length + 1
                          : notifications.events!.length,
                      shrinkWrap: true,
                      itemBuilder: (context, index) {
                        if (index == notifications.events?.length &&
                            notifications.events!.length >= 10) {
                          return Obx(
                            () {
                              if (_notificationController.isLoading.value) {
                                // Show loading indicator while fetching more data
                                return showCircularIndicator();
                              } else {
                                // Show "load more" button
                                return IconButton(
                                  onPressed: () {
                                    _notificationController
                                        .loadMoreNotification(
                                      token:
                                          _authController.token!.accessToken!,
                                    );
                                  },
                                  icon: const Icon(
                                    Icons.expand_circle_down_outlined,
                                    size: 30,
                                    color: BaseConfig.appThemeColor1,
                                  ),
                                );
                              }
                            },
                          );
                        } else {
                          // Render each notification item
                          final notificationEvent =
                              notifications.events?[index];
                          return Card(
                            elevation: 3,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12.r),
                              side: BorderSide(
                                color: BaseConfig.borderColor,
                                width: 1.w,
                              ),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.all(10.0),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  MediumTextNotoSans(
                                    text: notificationEvent?.name ?? "N/A",
                                    fontWeight: FontWeight.w700,
                                    size: 14.sp,
                                    maxLine: 2,
                                    textOverflow: TextOverflow.ellipsis,
                                  ),
                                  const Divider(color: BaseConfig.borderColor),
                                  SizedBox(height: 4.h),
                                  SmallTextNotoSans(
                                    text:
                                        notificationEvent?.description ?? "N/A",
                                    fontWeight: FontWeight.w400,
                                    size: 12.sp,
                                  ),
                                  SizedBox(height: 4.h),
                                  SmallTextNotoSans(
                                    text: notificationEvent
                                            ?.auditDetails?.createdTime
                                            ?.timeAgo() ??
                                        "-",
                                    fontWeight: FontWeight.w400,
                                    size: 12.sp,
                                  ),
                                ],
                              ),
                            ),
                          ).marginOnly(bottom: 10);
                        }
                      },
                    );
                  } else {
                    return const NoApplicationFoundWidget();
                  }
                } else {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return showCircularIndicator();
                  } else if (snapshot.connectionState ==
                      ConnectionState.active) {
                    return showCircularIndicator();
                  } else {
                    return const NoApplicationFoundWidget();
                  }
                }
              },
            ),
          ),
        ),
      ),
    );
  }
}
