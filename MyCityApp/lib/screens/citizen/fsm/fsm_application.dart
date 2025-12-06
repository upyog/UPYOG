import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:mobile_app/components/error_page/network_error.dart';
import 'package:mobile_app/components/no_application_found/no_application_found.dart';
import 'package:mobile_app/config/base_config.dart';
import 'package:mobile_app/controller/auth_controller.dart';
import 'package:mobile_app/controller/common_controller.dart';
import 'package:mobile_app/controller/fsm_controller.dart';
import 'package:mobile_app/model/citizen/fsm/fsm.dart';
import 'package:mobile_app/routes/routes.dart';
import 'package:mobile_app/utils/constants/i18_key_constants.dart';
import 'package:mobile_app/utils/enums/modules.dart';
import 'package:mobile_app/utils/utils.dart';
import 'package:mobile_app/widgets/complain_card.dart';
import 'package:mobile_app/widgets/header_widgets.dart';

class FsmApplication extends StatefulWidget {
  const FsmApplication({super.key});

  @override
  State<FsmApplication> createState() => _FsmApplicationState();
}

class _FsmApplicationState extends State<FsmApplication> {
  final _authController = Get.find<AuthController>();
  final _fsmController = Get.find<FsmController>();
  final _commonController = Get.find<CommonController>();

  var fsmModuleLoaded = false.obs;

  @override
  void initState() {
    super.initState();
    _fsmController.setDefaultLimit();
    getFsm();
  }

  void getFsm() async {
    await _commonController.fetchLabels(modules: Modules.FSM).then((value) {
      fsmModuleLoaded.value = true;
    });

    if (!_authController.isValidUser) return;
    await _fsmController.getFsmApplications(
      token: _authController.token!.accessToken!,
      uuid: _authController.token?.userRequest?.uuid,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: HeaderTop(
        titleWidget: Obx(
          () => fsmModuleLoaded.value
              ? Wrap(
                  children: [
                    Text(
                      getLocalizedString(
                        i18.fsmLocal.FSM_APPLICATION,
                        module: Modules.FSM,
                      ),
                    ),
                    Text(' (${_fsmController.length.value})'),
                  ],
                )
              : const SizedBox.shrink(),
        ),
        onPressed: () => Navigator.of(context).pop(),
      ),
      body: SizedBox(
        height: Get.height,
        width: Get.width,
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.all(16.w),
            child: GetBuilder<FsmController>(
              builder: (fnController) {
                return StreamBuilder(
                  stream: fnController.streamCtrl.stream,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      if (snapshot.data is String || snapshot.data == null) {
                        return const NoApplicationFoundWidget();
                      }

                      FsmModel fsmModel = snapshot.data;

                      final fsmList = fsmModel.fsm;
                      fsmList?.sort(
                        (a, b) => DateTime.fromMillisecondsSinceEpoch(
                          b.auditDetails!.createdTime!,
                        ).compareTo(
                          DateTime.fromMillisecondsSinceEpoch(
                            a.auditDetails!.createdTime!,
                          ),
                        ),
                      );
                      if (fsmList!.isNotEmpty) {
                        return ListView.builder(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: fsmList.length >= 10
                              ? fsmList.length + 1
                              : fsmList.length,
                          itemBuilder: (context, index) {
                            if (index == fsmList.length &&
                                fsmList.length >= 10) {
                              return Obx(() {
                                if (fnController.isLoading.value) {
                                  return showCircularIndicator();
                                } else {
                                  return IconButton(
                                    onPressed: () async {
                                      await fnController.loadMore(
                                        token:
                                            _authController.token!.accessToken!,
                                        uuid: '',
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
                              var fsm = fsmList[index];
                              return ComplainCard(
                                title: isNotNullOrEmpty(fsm.citizen?.name)
                                    ? '${fsm.citizen?.name}'
                                    : 'N/A',
                                id: '${getLocalizedString(i18.fsmLocal.FSM_APPLICATION_NO, module: Modules.FSM)}: ${fsm.applicationNo ?? "N/A"}',
                                date:
                                    '${getLocalizedString(i18.fsmLocal.FSM_SERVICE_CATEGORY, module: Modules.FSM)}: ${getLocalizedString(i18.fsmLocal.FSM_SERVICE_CATEGORY, module: Modules.FSM)}',
                                onTap: () {
                                  Get.toNamed(
                                    AppRoutes.FSM_SCREEN_DETAIL,
                                    arguments: fsm,
                                  );
                                },
                                status: getLocalizedString(
                                  '${i18.fsmLocal.FSM_STATUS}${fsm.applicationStatus}',
                                  module: Modules.FSM,
                                ),
                                statusColor:
                                    getStatusColor('${fsm.applicationStatus}'),
                                statusBackColor: getStatusBackColor(
                                  '${fsm.applicationStatus}',
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
                        () => getFsm(),
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
