import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/fire_noc_controller.dart';
import 'package:mobile_app/model/citizen/fire_noc/fire_noc.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class FireNocApplication extends StatefulWidget {
  const FireNocApplication({super.key});

  @override
  State<FireNocApplication> createState() => _FireNocState();
}

class _FireNocState extends State<FireNocApplication> {
  final _authController = Get.find<AuthController>();
  final _fireNocController = Get.find<FireNocController>();
  final _commonController = Get.find<CommonController>();

  @override
  void initState() {
    super.initState();
    _fireNocController.setDefaultLimit();
    getFireNoc();
  }

  void getFireNoc() async {
    if (!_authController.isValidUser) return;
    await _commonController.fetchLabels(modules: Modules.NOC);
    await _fireNocController.getFireNocApplications(
      token: _authController.token!.accessToken!,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => Wrap(
            children: [
              Text(
                getLocalizedString(
                  i18.noc.NOC_APPLICATION,
                  module: Modules.NOC,
                ),
              ),
              Text(' (${_fireNocController.length.value})'),
            ],
          ),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.all(16.w),
            child: GetBuilder<FireNocController>(
              builder: (fnController) {
                return StreamBuilder(
                  stream: fnController.streamCtrl.stream,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      if (snapshot.data is String || snapshot.data == null) {
                        return const NoApplicationFoundWidget();
                      }

                      FireNocModel myFireNoc = snapshot.data;

                      final fireNocList = myFireNoc.fireNoCs;
                      fireNocList?.sort(
                        (a, b) => DateTime.fromMillisecondsSinceEpoch(
                          b.auditDetails!.createdTime!,
                        ).compareTo(
                          DateTime.fromMillisecondsSinceEpoch(
                            a.auditDetails!.createdTime!,
                          ),
                        ),
                      );
                      if (fireNocList!.isNotEmpty) {
                        return ListView.builder(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: fireNocList.length >= 10
                              ? fireNocList.length + 1
                              : fireNocList.length,
                          itemBuilder: (context, index) {
                            if (index == fireNocList.length &&
                                fireNocList.length >= 10) {
                              return Obx(() {
                                if (fnController.isLoading.value) {
                                  return showCircularIndicator();
                                } else {
                                  return IconButton(
                                    onPressed: () async {
                                      await fnController.loadMore(
                                        token: _authController
                                                .token?.accessToken ??
                                            '',
                                      );
                                    },
                                    icon: const Icon(
                                      Icons.expand_circle_down_outlined,
                                      size: 30,
                                      color: BaseConfig.appThemeColor1,
                                    ),
                                  );
                                }
                              });
                            } else {
                              var fire = fireNocList[index];
                              return ComplainCard(
                                title: isNotNullOrEmpty(
                                  fire.fireNocDetails?.buildings?.first.name,
                                )
                                    ? '${fire.fireNocDetails?.buildings?.first.name}'
                                    : 'N/A',
                                id: '${getLocalizedString(i18.noc.NOC_APPLICATION_NO, module: Modules.NOC)}: ${fire.fireNocDetails?.applicationNumber}',
                                address: '${getLocalizedString(
                                  i18.noc.NOC_APPLICANT_NAME,
                                )}: ${fire.fireNocDetails?.applicantDetails?.owners?.first.name}',
                                date: '${getLocalizedString(
                                  i18.noc.NOC_NOC_NO,
                                )}: ${fire.fireNocNumber ?? " N/A"}',
                                onTap: () {
                                  Get.toNamed(
                                    AppRoutes.FIRE_NOC_SCREEN_DETAIL,
                                    arguments: fire,
                                  );
                                },
                                status: getLocalizedString(
                                  '${i18.noc.NOC_STATUS_RES}${fire.fireNocDetails?.status}',
                                ),
                                statusColor: getStatusColor(
                                  '${fire.fireNocDetails?.status}',
                                ),
                                statusBackColor: getStatusBackColor(
                                  '${fire.fireNocDetails?.status}',
                                ),
                              ).paddingOnly(bottom: 12);
                            }
                          },
                        );
                      } else {
                        return const NoApplicationFoundWidget();
                      }
                    } else if (snapshot.hasError) {
                      return networkErrorPage(
                        context,
                        () => getFireNoc(),
                      );
                    } else {
                      if (snapshot.connectionState == ConnectionState.waiting ||
                          snapshot.connectionState == ConnectionState.active) {
                        return SizedBox(
                          height: Get.height / 1.2,
                          width: Get.width,
                          child: showCircularIndicator(),
                        );
                      } else {
                        return const SizedBox.shrink();
                      }
                    }
                  },
                );
              },
            ),
          ),
        ),
      ),
    );
  }
}
